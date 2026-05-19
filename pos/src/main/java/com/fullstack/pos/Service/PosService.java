package com.fullstack.pos.Service;

import com.fullstack.pos.dto.CajaDTO;
import com.fullstack.pos.dto.ItemTransaccionDTO;
import com.fullstack.pos.dto.TransaccionDTO;
import com.fullstack.pos.Model.*;
import com.fullstack.pos.Repository.CajaRepository;
import com.fullstack.pos.Repository.TransaccionRepository;
import com.fullstack.pos.client.UsuarioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PosService {

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private UsuarioClient usuarioClient; // Inyección de Feign

    // --- LÓGICA DE CAJAS ---

    @Transactional
    public CajaDTO abrirCaja(Long usuarioId, Double montoInicial) {
        if (cajaRepository.findByEstado(EstadoCaja.ABIERTA).isPresent()) {
            throw new RuntimeException("CajaYaAbiertaException: Ya existe una caja abierta en el turno.");
        }

        // VALIDACIÓN FEIGN 1: ¿El empleado que abre la caja existe?
        try {
            usuarioClient.obtenerUsuarioPorId(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("UsuarioNotFoundException: El usuario (cajero) con ID " + usuarioId + " no existe.");
        }

        Caja nuevaCaja = Caja.builder()
                .usuarioId(usuarioId)
                .montoInicial(montoInicial)
                .totalEfectivo(0f)
                .totalTarjeta(0f)
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
                .orElseThrow(() -> new RuntimeException("CajaNoEncontradaException: No hay ninguna caja abierta."));
    }

    // --- LÓGICA DE TRANSACCIONES ---

    @Transactional
    public TransaccionDTO registrarTransaccion(TransaccionDTO dto) {
        Caja cajaActiva = obtenerCajaActualEntidad();

        // VALIDACIÓN FEIGN 2: ¿El cliente que compra existe?
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("UsuarioNotFoundException: El cliente con ID " + dto.getClienteId() + " no existe.");
        }

        // Mapeo manual de DTO a Entidad
        Transaccion transaccion = Transaccion.builder()
                .cajaId(cajaActiva.getId())
                .clienteId(dto.getClienteId())
                .tipo(dto.getTipo())
                .total(dto.getTotal())
                .metodoPago(dto.getMetodoPago())
                .build();

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<ItemTransaccion> items = dto.getItems().stream()
                    .map(itemDto -> ItemTransaccion.builder()
                            .productoId(itemDto.getProductoId())
                            .descripcion(itemDto.getDescripcion())
                            .cantidad(itemDto.getCantidad())
                            .precioUnitario(itemDto.getPrecioUnitario())
                            .transaccion(transaccion) // Bidireccionalidad segura
                            .build())
                    .collect(Collectors.toList());
            transaccion.setItems(items);
        } else {
            throw new RuntimeException("TransaccionVaciaException: La transacción debe tener al menos un ítem.");
        }

        Transaccion guardada = transaccionRepository.save(transaccion);

        // Actualizar totales de la caja
        if (guardada.getMetodoPago() == MetodoPago.EFECTIVO) {
            cajaActiva.setTotalEfectivo(cajaActiva.getTotalEfectivo() + guardada.getTotal().floatValue());
        } else if (guardada.getMetodoPago() == MetodoPago.TARJETA) {
            cajaActiva.setTotalTarjeta(cajaActiva.getTotalTarjeta() + guardada.getTotal().floatValue());
        }
        cajaRepository.save(cajaActiva);

        return mapearTransaccionADTO(guardada);
    }

    public TransaccionDTO obtenerTransaccionPorId(Long id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaccion no encontrada"));
        return mapearTransaccionADTO(transaccion);
    }

    public List<TransaccionDTO> obtenerTodasLasTransacciones() {
        return transaccionRepository.findAll().stream()
                .map(this::mapearTransaccionADTO)
                .collect(Collectors.toList());
    }

    // --- MAPPERS ---

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
        List<ItemTransaccionDTO> itemsDto = t.getItems() != null ? t.getItems().stream()
                .map(i -> ItemTransaccionDTO.builder()
                        .id(i.getId())
                        .transaccionId(t.getId())
                        .productoId(i.getProductoId())
                        .descripcion(i.getDescripcion())
                        .cantidad(i.getCantidad())
                        .precioUnitario(i.getPrecioUnitario())
                        .subTotal(i.getSubTotal())
                        .build())
                .collect(Collectors.toList()) : null;

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
