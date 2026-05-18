package com.fullstack.pos.Service;

import com.fullstack.pos.Model.*;
import com.fullstack.pos.Repository.CajaRepository;
import com.fullstack.pos.Repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PosService {

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    // --- LÓGICA DE CAJAS ---

    @Transactional
    public Caja abrirCaja(Long usuarioId, Double montoInicial) {
        if (cajaRepository.findByEstado(EstadoCaja.ABIERTA).isPresent()) {
            throw new RuntimeException("CajaYaAbiertaException: Ya existe una caja abierta en el turno.");
        }

        Caja nuevaCaja = Caja.builder()
                .usuarioId(usuarioId)
                .montoInicial(montoInicial)
                .totalEfectivo(0f)
                .totalTarjeta(0f)
                .build();

        return cajaRepository.save(nuevaCaja);
    }

    @Transactional
    public Caja cerrarCaja() {
        Caja caja = obtenerCajaActual();
        caja.setFechaCierre(LocalDateTime.now());
        caja.setEstado(EstadoCaja.CERRADA);
        return cajaRepository.save(caja);
    }

    public Caja obtenerCajaActual() {
        return cajaRepository.findByEstado(EstadoCaja.ABIERTA)
                .orElseThrow(() -> new RuntimeException("CajaNoEncontradaException: No hay ninguna caja abierta."));
    }

    // --- LÓGICA DE TRANSACCIONES ---

    @Transactional
    public Transaccion registrarTransaccion(Transaccion transaccion) {
        // 1. Validar que la caja esté abierta
        Caja cajaActiva = obtenerCajaActual();
        transaccion.setCajaId(cajaActiva.getId());

        // 2. TRUCO DE HIBERNATE: Enlazar bidireccionalmente los ítems a la transacción
        if (transaccion.getItems() != null && !transaccion.getItems().isEmpty()) {
            transaccion.getItems().forEach(item -> item.setTransaccion(transaccion));
        } else {
            throw new RuntimeException("TransaccionVaciaException: La transacción debe tener al menos un ítem.");
        }

        // 3. Guardar la transacción (por CascadeType.ALL, guardará los ítems automáticamente)
        Transaccion guardada = transaccionRepository.save(transaccion);

        // 4. Actualizar los totales de la caja activa
        if (guardada.getMetodoPago() == MetodoPago.EFECTIVO) {
            cajaActiva.setTotalEfectivo(cajaActiva.getTotalEfectivo() + guardada.getTotal().floatValue());
        } else if (guardada.getMetodoPago() == MetodoPago.TARJETA) {
            cajaActiva.setTotalTarjeta(cajaActiva.getTotalTarjeta() + guardada.getTotal().floatValue());
        }
        cajaRepository.save(cajaActiva);

        return guardada;
    }

    public Transaccion obtenerTransaccionPorId(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaccion no encontrada"));
    }

    public List<Transaccion> obtenerTodasLasTransacciones() {
        return transaccionRepository.findAll();
    }
}
