package com.fullstack.suscripciones.service;

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

/**
 * Servicio que encapsula la lógica de negocio para la gestión de suscripciones de clientes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final HistorialEstadoRepository historialRepository;
    private final UsuarioClient usuarioClient; // Cliente Feign para comunicarse con el microservicio de usuarios.

    /**
     * Crea una nueva suscripción para un cliente.
     * @param dto DTO con los datos de la nueva suscripción.
     * @return DTO con la información de la suscripción creada.
     * @throws EntityNotFoundException si el cliente o el plan no existen.
     * @throws IllegalStateException si el cliente ya tiene una suscripción activa.
     */
    @Transactional
    public SuscripcionResponseDTO crearSuscripcion(SuscripcionRequestDTO dto) {
        // 1. Validar que el cliente existe llamando al microservicio de usuarios.
        try {
            usuarioClient.obtenerUsuarioPorId(dto.getClienteId());
        } catch (Exception e) {
            // Si el cliente Feign falla (ej. devuelve 404), lanzamos una excepción clara.
            throw new EntityNotFoundException("El cliente con ID " + dto.getClienteId() + " no existe.");
        }

        // 2. Validar que el cliente no tenga ya una suscripción activa.
        suscripcionRepository.findByClienteIdAndEstado(dto.getClienteId(), "ACTIVA")
                .ifPresent(s -> { throw new IllegalStateException("El cliente ya tiene una suscripción activa."); });

        // 3. Obtener el plan seleccionado.
        Plan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("El plan con ID " + dto.getPlanId() + " no existe."));

        // 4. Calcular la fecha de fin de la suscripción.
        LocalDate fechaFin = dto.getFechaInicio().plusDays(plan.getDuracionDias());

        // 5. Crear y guardar la nueva entidad Suscripcion.
        Suscripcion suscripcion = Suscripcion.builder()
                .clienteId(dto.getClienteId())
                .plan(plan)
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(fechaFin)
                .estado("ACTIVA")
                .diasCongelados(0)
                .build();

        suscripcion = suscripcionRepository.save(suscripcion);
        
        // 6. Registrar el evento en el historial.
        registrarHistorial(suscripcion, null, "ACTIVA", "Alta inicial de suscripción.");

        return mapearADTO(suscripcion);
    }

    /**
     * Congela una suscripción activa.
     * @param id El ID de la suscripción.
     * @param dto DTO con los detalles del congelamiento.
     * @return La suscripción actualizada.
     * @throws EntityNotFoundException si la suscripción no existe.
     * @throws IllegalStateException si la suscripción no está activa o si se excede el límite de días de congelamiento.
     */
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
        sub.setFechaFin(sub.getFechaFin().plusDays(dto.getDias())); // La fecha de fin se extiende.

        suscripcionRepository.save(sub);
        registrarHistorial(sub, "ACTIVA", "CONGELADA", dto.getMotivo());

        return mapearADTO(sub);
    }

    /**
     * Reactiva una suscripción que estaba congelada.
     * @param id El ID de la suscripción.
     * @return La suscripción actualizada.
     */
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

    /**
     * Cancela una suscripción de forma definitiva.
     * @param id El ID de la suscripción.
     * @return La suscripción actualizada.
     */
    @Transactional
    public SuscripcionResponseDTO cancelar(Long id) {
        Suscripcion sub = suscripcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suscripción no encontrada con ID: " + id));

        String estadoAnterior = sub.getEstado();
        sub.setEstado("CANCELADA");
        suscripcionRepository.save(sub);
        registrarHistorial(sub, estadoAnterior, "CANCELADA", "Cancelación definitiva del servicio.");

        return mapearADTO(sub);
    }

    /**
     * Lista todas las suscripciones (activas e inactivas) de un cliente.
     * @param clienteId El ID del cliente.
     * @return Una lista de DTOs de suscripción.
     */
    @Transactional(readOnly = true)
    public List<SuscripcionResponseDTO> listarPorCliente(Long clienteId) {
        return suscripcionRepository.findByClienteId(clienteId).stream()
                .map(this::mapearADTO).collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de cambios de estado de una suscripción.
     * @param suscripcionId El ID de la suscripción.
     * @return Lista del historial de estados, ordenada del más reciente al más antiguo.
     */
    @Transactional(readOnly = true)
    public List<HistorialEstado> obtenerHistorial(Long suscripcionId) {
        return historialRepository.findBySuscripcionIdOrderByIdDesc(suscripcionId);
    }

    /**
     * Tarea programada que se ejecuta todos los días a medianoche.
     * Busca suscripciones activas cuya fecha de fin ha pasado y las marca como "VENCIDA".
     */
    @Scheduled(cron = "0 0 0 * * ?") // Se ejecuta a las 00:00:00 todos los días
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
                });
        log.info("Tarea de verificación de vencimientos finalizada.");
    }

    /**
     * Método de utilidad para registrar un cambio de estado en el historial.
     */
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

    /**
     * Método de utilidad para mapear una entidad Suscripcion a su DTO de respuesta.
     */
    private SuscripcionResponseDTO mapearADTO(Suscripcion s) {
        return new SuscripcionResponseDTO(
                s.getId(),
                s.getClienteId(),
                s.getPlan().getId(),
                s.getPlan().getNombre(),
                s.getFechaInicio(),
                s.getFechaFin(),
                s.getEstado(),
                s.getDiasCongelados()
        );
    }
}
