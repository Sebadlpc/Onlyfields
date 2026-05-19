package com.fullstack.suscripciones.service;

import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.dto.external.UsuarioExternoDTO;
import com.fullstack.suscripciones.model.PlanSuscripcion;
import com.fullstack.suscripciones.model.Suscripcion;
import com.fullstack.suscripciones.repository.PlanSuscripcionRepository;
import com.fullstack.suscripciones.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final UsuarioClient usuarioClient;
    private final SuscripcionRepository repository;
    private final PlanSuscripcionRepository planRepository;

    @Transactional(readOnly = true)
    public List<Suscripcion> listarTodas() {
        log.info("[ms-suscripciones] Solicitando listado completo de suscripciones");
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Suscripcion obtenerPorId(Long id) {
        log.info("[ms-suscripciones] Buscando suscripción con ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada con ID: " + id));
    }

    @Transactional
    public Suscripcion crear(Suscripcion suscripcion) {
        log.info("[ms-suscripciones] Iniciando proceso de creación para el cliente ID: {}", suscripcion.getClienteId());

        UsuarioExternoDTO usuario = usuarioClient.obtenerUsuarioPorId(suscripcion.getClienteId());
        if (usuario == null) {
            log.warn("[ms-suscripciones] Validación fallida: El cliente ID {} no existe en ms-usuarios", suscripcion.getClienteId());
            throw new RuntimeException("El cliente no existe en ms-usuarios");
        }

        PlanSuscripcion planCompleto = planRepository.findById(suscripcion.getPlan().getId())
                .orElseThrow(() -> new RuntimeException("El Plan especificado no existe con ID: " + suscripcion.getPlan().getId()));

        suscripcion.setPlan(planCompleto);
        suscripcion.setEstado("ACTIVA");
        suscripcion.setDiasCongelados(0);

        Suscripcion nuevaSuscripcion = repository.save(suscripcion);
        log.info("[ms-suscripciones] Suscripción creada con éxito. ID asignado: {}", nuevaSuscripcion.getId());

        return nuevaSuscripcion;
    }

    @Transactional
    public Suscripcion actualizar(Long id, Suscripcion suscripcionActualizada) {
        log.info("[ms-suscripciones] Modificando registro de suscripción ID: {}", id);
        Suscripcion existente = obtenerPorId(id);

        // Si en la actualización se envía un cambio de plan, resolvemos el plan completo en memoria
        if (suscripcionActualizada.getPlan() != null && suscripcionActualizada.getPlan().getId() != null) {
            PlanSuscripcion planCompleto = planRepository.findById(suscripcionActualizada.getPlan().getId())
                    .orElseThrow(() -> new RuntimeException("El nuevo Plan especificado no existe"));
            existente.setPlan(planCompleto);
        }

        existente.setFechaInicio(suscripcionActualizada.getFechaInicio());
        existente.setFechaFin(suscripcionActualizada.getFechaFin());
        existente.setEstado(suscripcionActualizada.getEstado());

        if (suscripcionActualizada.getDiasCongelados() != null) {
            existente.setDiasCongelados(suscripcionActualizada.getDiasCongelados());
        }

        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("[ms-suscripciones] Aplicando cancelación lógica a la suscripción ID: {}", id);
        Suscripcion existente = obtenerPorId(id);
        existente.setEstado("CANCELADA");
        repository.save(existente);
    }
}