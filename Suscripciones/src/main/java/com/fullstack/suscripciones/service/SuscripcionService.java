package com.fullstack.suscripciones.service;

import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.dto.*;
import com.fullstack.suscripciones.model.*;
import com.fullstack.suscripciones.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final HistorialEstadoRepository historialRepository;
    private final UsuarioClient usuarioClient;

    @Transactional
    public SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO dto) {
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            throw new RuntimeException("Error de validación: El cliente ID " + dto.getClienteId() + " no existe en el sistema de usuarios.");
        }

        suscripcionRepository.findByClienteIdAndEstado(dto.getClienteId(), "ACTIVA")
                .ifPresent(s -> { throw new IllegalStateException("El cliente ya posee una membresía ACTIVA actualmente."); });

        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));

        LocalDate fechaFin = dto.getFechaInicio().plusDays(plan.getDuracionDias());

        Suscripcion suscripcion = Suscripcion.builder()
                .clienteId(dto.getClienteId())
                .plan(plan)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(fechaFin)
                .estado("ACTIVA")
                .diasCongelados(0)
                .build();

        suscripcion = suscripcionRepository.save(suscripcion);
        registrarHistorial(suscripcion, null, "ACTIVA", "Alta inicial de membresía");

        return mapearADTO(suscripcion);
    }

    @Transactional
    public SuscripcionResponseDTO congelar(Long id, CongelarRequestDTO dto) {
        Suscripcion sub = suscripcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

        if (!"ACTIVA".equals(sub.getEstado())) {
            throw new IllegalStateException("Solo se pueden congelar membresías en estado ACTIVA");
        }

        if (sub.getDiasCongelados() + dto.getDias() > 30) {
            throw new IllegalStateException("Acción rechazada: Supera el límite máximo de 30 días de congelamiento anual.");
        }

        sub.setDiasCongelados(sub.getDiasCongelados() + dto.getDias());
        sub.setEstado("CONGELADA");
        sub.setFechaFin(sub.getFechaFin().plusDays(dto.getDias()));

        suscripcionRepository.save(sub);
        registrarHistorial(sub, "ACTIVA", "CONGELADA", dto.getMotivo());

        return mapearADTO(sub);
    }

    @Transactional
    public SuscripcionResponseDTO reactivar(Long id) {
        Suscripcion sub = suscripcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

        if (!"CONGELADA".equals(sub.getEstado())) {
            throw new IllegalStateException("La suscripción no está congelada");
        }

        sub.setEstado("ACTIVA");
        suscripcionRepository.save(sub);
        registrarHistorial(sub, "CONGELADA", "ACTIVA", "Reactivación manual solicitada por el cliente");

        return mapearADTO(sub);
    }

    @Transactional
    public SuscripcionResponseDTO cancelar(Long id) {
        Suscripcion sub = suscripcionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

        String estadoAnterior = sub.getEstado();
        sub.setEstado("CANCELADA");
        suscripcionRepository.save(sub);
        registrarHistorial(sub, estadoAnterior, "CANCELADA", "Cancelación definitiva del servicio");

        return mapearADTO(sub);
    }
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarPorCliente(Long clienteId) {
        return suscripcionRepository.findByClienteId(clienteId).stream()
                .map(this::mapearADTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistorialEstado> obtenerHistorial(Long suscripcionId) {
        return historialRepository.findBySuscripcionIdOrderByIdDesc(suscripcionId);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verificarVencimientosProporcionales() {
        LocalDate hoy = LocalDate.now();
        suscripcionRepository.findAll().stream()
                .filter(s -> "ACTIVA".equals(s.getEstado()) && hoy.isAfter(s.getFechaFin()))
                .forEach(s -> {
                    s.setEstado("VENCIDA");
                    suscripcionRepository.save(s);
                    registrarHistorial(s, "ACTIVA", "VENCIDA", "Cierre automático del sistema por alcance de fecha fin.");
                });
    }

    private void registrarHistorial(Suscripcion sub, String ant, String nuevo, String mot) {
        HistorialEstado hist = HistorialEstado.builder()
                .suscripcion(sub)
                .estadoAnterior(ant)
                .estadoNuevo(nuevo)
                .fechaCambio(LocalDateTime.now())
                .motivo(mot)
                .build();
        historialRepository.save(hist);
    }

    private SuscripcionResponseDTO mapearADTO(Suscripcion s) {
        return SuscripcionResponseDTO.builder()
                .id(s.getId())
                .clienteId(s.getClienteId())
                .planId(s.getPlan().getId())
                .planNombre(s.getPlan().getNombre())
                .fechaInicio(s.getFechaInicio())
                .fechaFin(s.getFechaFin())
                .estado(s.getEstado())
                .diasCongelados(s.getDiasCongelados())
                .build();
    }
}