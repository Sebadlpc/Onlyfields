package com.fullstack.suscripciones.service;

import com.fullstack.suscripciones.client.NotificacionClient;
import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.dto.*;
import com.fullstack.suscripciones.model.*;
import com.fullstack.suscripciones.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final HistorialEstadoRepository historialRepository;
    private final UsuarioClient usuarioClient;
    private final NotificacionClient notificacionClient;

    @Transactional
    public SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO dto) {
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            throw new EntityNotFoundException("El cliente con ID " + dto.getClienteId() + " no existe.");
        }

        suscripcionRepository.findByClienteIdAndEstado(dto.getClienteId(), "ACTIVA")
                .ifPresent(s -> { throw new IllegalStateException("El cliente ya tiene una suscripción activa."); });

        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("El plan con ID " + dto.getPlanId() + " no existe."));

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
        registrarHistorial(suscripcion, null, "ACTIVA", "Alta inicial de suscripción.");

        // Actualizar estado del cliente en ms-usuarios
        try {
            usuarioClient.actualizarEstadoUsuario(dto.getClienteId(), new UsuarioClient.EstadoUsuarioDTO("SUSCRITO"));
            log.info("Estado del cliente {} actualizado a SUSCRITO en ms-usuarios.", dto.getClienteId());
        } catch (Exception e) {
            log.error("Error al actualizar estado del cliente {} en ms-usuarios: {}", dto.getClienteId(), e.getMessage());
        }

        return mapearADTO(suscripcion);
    }

    @Transactional
    public SuscripcionResponseDTO congelar(Long id, CongelarRequestDTO dto) {
        Suscripcion sub = suscripcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suscripción no encontrada con ID: " + id));

        if (!"ACTIVA".equals(sub.getEstado())) {
            throw new IllegalStateException("Solo se pueden congelar suscripciones en estado 'ACTIVA'.");
        }

        final int LIMITE_DIAS_CONGELADOS = 30;
        if (sub.getDiasCongelados() + dto.getDias() > LIMITE_DIAS_CONGELADOS) {
            throw new IllegalStateException("Se excede el límite máximo de " + LIMITE_DIAS_CONGELADOS + " días de congelamiento.");
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
                .orElseThrow(() -> new EntityNotFoundException("Suscripción no encontrada con ID: " + id));

        if (!"CONGELADA".equals(sub.getEstado())) {
            throw new IllegalStateException("La suscripción no está en estado 'CONGELADA'.");
        }

        sub.setEstado("ACTIVA");
        suscripcionRepository.save(sub);
        registrarHistorial(sub, "CONGELADA", "ACTIVA", "Reactivación manual.");

        return mapearADTO(sub);
    }

    @Transactional
    public SuscripcionResponseDTO cancelar(Long id) {
        Suscripcion sub = suscripcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suscripción no encontrada con ID: " + id));

        String estadoAnterior = sub.getEstado();
        sub.setEstado("CANCELADA");
        suscripcionRepository.save(sub);
        registrarHistorial(sub, estadoAnterior, "CANCELADA", "Cancelación definitiva del servicio.");

        // Actualizar estado del cliente en ms-usuarios
        try {
            usuarioClient.actualizarEstadoUsuario(sub.getClienteId(), new UsuarioClient.EstadoUsuarioDTO("NO_SUSCRITO"));
            log.info("Estado del cliente {} actualizado a NO_SUSCRITO en ms-usuarios.", sub.getClienteId());
        } catch (Exception e) {
            log.error("Error al actualizar estado del cliente {} en ms-usuarios: {}", sub.getClienteId(), e.getMessage());
        }

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
    public void verificarVencimientos() {
        log.info("Iniciando tarea programada: Verificación de suscripciones vencidas...");
        LocalDate hoy = LocalDate.now();
        List<Suscripcion> suscripcionesActivas = suscripcionRepository.findAllByEstado("ACTIVA");

        suscripcionesActivas.stream()
                .filter(s -> hoy.isAfter(s.getFechaFin()))
                .forEach(s -> {
                    log.info("Suscripción ID {} ha vencido. Cambiando estado a VENCIDA.", s.getId());
                    s.setEstado("VENCIDA");
                    suscripcionRepository.save(s);
                    registrarHistorial(s, "ACTIVA", "VENCIDA", "Vencimiento automático por fecha.");

                    // Notificar al cliente sobre membresía vencida
                    try {
                        notificacionClient.enviarNotificacion(new NotificacionClient.NotificacionDTO(s.getClienteId(), "MEMBRESIA_VENCIDA", "Tu membresía ha vencido. Por favor, renuévala para seguir disfrutando de nuestros servicios."));
                        log.info("Notificación de membresía vencida enviada para el cliente {}", s.getClienteId());
                    } catch (Exception e) {
                        log.error("Error al enviar notificación de membresía vencida para el cliente {}: {}", s.getClienteId(), e.getMessage());
                    }

                    // Actualizar estado del cliente en ms-usuarios
                    try {
                        usuarioClient.actualizarEstadoUsuario(s.getClienteId(), new UsuarioClient.EstadoUsuarioDTO("NO_SUSCRITO"));
                        log.info("Estado del cliente {} actualizado a NO_SUSCRITO en ms-usuarios.", s.getClienteId());
                    } catch (Exception e) {
                        log.error("Error al actualizar estado del cliente {} en ms-usuarios: {}", s.getClienteId(), e.getMessage());
                    }
                });
        log.info("Tarea de verificación de vencimientos finalizada.");
    }

    private void registrarHistorial(Suscripcion sub, String estadoAnterior, String estadoNuevo, String motivo) {
        HistorialEstado historial = HistorialEstado.builder()
                .suscripcion(sub)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .fechaCambio(LocalDateTime.now())
                .motivo(motivo)
                .build();
        historialRepository.save(historial);
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
