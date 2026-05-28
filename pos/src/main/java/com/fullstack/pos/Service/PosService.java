package com.fullstack.pos.service;

import com.fullstack.pos.client.InventarioClient;
import com.fullstack.pos.client.NotificacionClient;
import com.fullstack.pos.client.SuscripcionesClient;
import com.fullstack.pos.client.UsuarioClient;
import com.fullstack.pos.dto.CajaDTO;
import com.fullstack.pos.dto.ItemTransaccionDTO;
import com.fullstack.pos.dto.TransaccionDTO;
import com.fullstack.pos.exception.CajaException;
import com.fullstack.pos.exception.TransaccionException;
import com.fullstack.pos.model.*;
import com.fullstack.pos.repository.CajaRepository;
import com.fullstack.pos.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PosService {

    private final CajaRepository cajaRepository;
    private final TransaccionRepository transaccionRepository;
    private final UsuarioClient usuarioClient;
    private final InventarioClient inventarioClient;
    private final SuscripcionesClient suscripcionesClient;
    private final NotificacionClient notificacionClient;

    @Transactional
    public CajaDTO abrirCaja(Long usuarioId, Double montoInicial) {
        if (cajaRepository.findByEstado(EstadoCaja.ABIERTA).isPresent()) {
            throw new CajaException("Ya existe una caja abierta.");
        }
        try {
            // Validar que el recepcionista existe y está activo
            usuarioClient.obtenerUsuarioPorId(usuarioId);
        } catch (Exception e) {
            throw new CajaException("El usuario (recepcionista) con ID " + usuarioId + " no existe o no está activo.");
        }

        Caja nuevaCaja = Caja.builder()
                .usuarioId(usuarioId)
                .montoInicial(BigDecimal.valueOf(montoInicial))
                .build();
        return mapearCajaADTO(cajaRepository.save(nuevaCaja));
    }

    @Transactional
    public CajaDTO cerrarCaja() {
        Caja caja = obtenerCajaActualEntidad();
        caja.setFechaCierre(LocalDateTime.now());
        caja.setEstado(EstadoCaja.CERRADA);
        return mapearCajaADTO(cajaRepository.save(caja));
    }

    public CajaDTO obtenerCajaActual() {
        return mapearCajaADTO(obtenerCajaActualEntidad());
    }

    private Caja obtenerCajaActualEntidad() {
        return cajaRepository.findByEstado(EstadoCaja.ABIERTA)
                .orElseThrow(() -> new CajaException("No hay ninguna caja abierta."));
    }

    @Transactional
    public TransaccionDTO registrarTransaccion(TransaccionDTO dto) {
        Caja cajaActiva = obtenerCajaActualEntidad();
        
        if (dto.getClienteId() != null) {
            try {
                usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
            } catch (Exception e) {
                throw new TransaccionException("El cliente con ID " + dto.getClienteId() + " no existe.");
            }
        }

        Transaccion transaccion = Transaccion.builder()
                .cajaId(cajaActiva.getId())
                .clienteId(dto.getClienteId())
                .tipo(dto.getTipo())
                .total(dto.getTotal())
                .metodoPago(dto.getMetodoPago())
                .build();

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new TransaccionException("La transacción debe tener al menos un ítem.");
        }
        
        List<ItemTransaccion> items = dto.getItems().stream()
                .map(itemDto -> ItemTransaccion.builder()
                        .productoId(itemDto.getProductoId())
                        .descripcion(itemDto.getDescripcion())
                        .cantidad(itemDto.getCantidad())
                        .precioUnitario(itemDto.getPrecioUnitario())
                        .transaccion(transaccion)
                        .build())
                .collect(Collectors.toList());
        transaccion.setItems(items);

        Transaccion guardada = transaccionRepository.save(transaccion);

        // Lógicas específicas de negocio según el tipo y los items
        procesarReglasDeNegocio(guardada);

        // Actualizar totales de la caja
        if (guardada.getMetodoPago() == MetodoPago.EFECTIVO) {
            cajaActiva.setTotalEfectivo(cajaActiva.getTotalEfectivo().add(guardada.getTotal()));
        } else if (guardada.getMetodoPago() == MetodoPago.TARJETA) {
            cajaActiva.setTotalTarjeta(cajaActiva.getTotalTarjeta().add(guardada.getTotal()));
        }
        cajaRepository.save(cajaActiva);

        // Enviar comprobante
        try {
            // Asumiendo que podemos obtener el email del cliente, por ahora usamos un placeholder
            String emailCliente = dto.getClienteId() != null ? "cliente_" + dto.getClienteId() + "@onlyfields.com" : "anonimo@onlyfields.com";
            notificacionClient.enviarComprobante(new NotificacionClient.ComprobantePagoDTO(guardada.getId(), emailCliente, guardada.getTotal()));
            log.info("Comprobante enviado para la transacción {}", guardada.getId());
        } catch (Exception e) {
            log.error("Error al enviar comprobante para la transacción {}: {}", guardada.getId(), e.getMessage());
        }

        return mapearTransaccionADTO(guardada);
    }

    private void procesarReglasDeNegocio(Transaccion transaccion) {
        if (transaccion.getTipo() == TipoTransaccion.VENTA) {
            // Descontar stock para productos físicos
            for (ItemTransaccion item : transaccion.getItems()) {
                if (item.getProductoId() != null) {
                    try {
                        inventarioClient.actualizarStock(item.getProductoId(), new InventarioClient.MovimientoStockDTO("SALIDA", item.getCantidad(), "Venta POS #" + transaccion.getId()));
                        log.info("Stock descontado para el producto {}", item.getProductoId());
                    } catch (Exception e) {
                        log.error("Error al descontar stock del producto {}: {}", item.getProductoId(), e.getMessage());
                        // Dependiendo del negocio, esto podría lanzar excepción o solo loguear
                    }
                }
            }
        } else if (transaccion.getTipo() == TipoTransaccion.PAGO_SUSCRIPCION) {
            // Confirmar el pago de un plan/membresía
            // Asumimos que el primer item contiene el ID de la suscripción en 'productoId'
            if (!transaccion.getItems().isEmpty() && transaccion.getItems().get(0).getProductoId() != null) {
                Long suscripcionId = transaccion.getItems().get(0).getProductoId();
                try {
                    suscripcionesClient.confirmarPago(suscripcionId);
                    log.info("Pago confirmado para la suscripción {}", suscripcionId);
                } catch (Exception e) {
                    log.error("Error al confirmar pago de suscripción {}: {}", suscripcionId, e.getMessage());
                }
            }
        }
    }

    public TransaccionDTO obtenerTransaccionPorId(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new TransaccionException("Transaccion no encontrada con ID: " + id));
        return mapearTransaccionADTO(transaccion);
    }

    public List<TransaccionDTO> obtenerTodasLasTransacciones() {
        return transaccionRepository.findAll().stream()
                .map(this::mapearTransaccionADTO)
                .collect(Collectors.toList());
    }

    private CajaDTO mapearCajaADTO(Caja caja) {
        if (caja == null) return null;
        return CajaDTO.builder()
                .id(caja.getId())
                .usuarioId(caja.getUsuarioId())
                .fechaApertura(caja.getFechaApertura())
                .fechaCierre(caja.getFechaCierre())
                .montoInicial(caja.getMontoInicial())
                .totalEfectivo(caja.getTotalEfectivo())
                .totalTarjeta(caja.getTotalTarjeta())
                .estado(caja.getEstado())
                .build();
    }

    private TransaccionDTO mapearTransaccionADTO(Transaccion t) {
        if (t == null) return null;
        List<ItemTransaccionDTO> itemsDto = t.getItems().stream()
                .map(i -> ItemTransaccionDTO.builder()
                        .id(i.getId())
                        .transaccionId(t.getId())
                        .productoId(i.getProductoId())
                        .descripcion(i.getDescripcion())
                        .cantidad(i.getCantidad())
                        .precioUnitario(i.getPrecioUnitario())
                        .subTotal(i.getSubTotal())
                        .build())
                .collect(Collectors.toList());

        return TransaccionDTO.builder()
                .id(t.getId())
                .cajaId(t.getCajaId())
                .clienteId(t.getClienteId())
                .tipo(t.getTipo())
                .total(t.getTotal())
                .metodoPago(t.getMetodoPago())
                .estado(t.getEstado())
                .fechaHora(t.getFechaHora())
                .items(itemsDto)
                .build();
    }
}
