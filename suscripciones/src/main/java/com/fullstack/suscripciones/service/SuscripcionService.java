package com.fullstack.suscripciones.service;

import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.dto.SuscripcionRequestDTO;
import com.fullstack.suscripciones.dto.external.UsuarioExternoDTO;
import com.fullstack.suscripciones.model.PlanSuscripcion;
import com.fullstack.suscripciones.model.Suscripcion;
import com.fullstack.suscripciones.repository.PlanSuscripcionRepository;
import com.fullstack.suscripciones.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanSuscripcionRepository planRepository;
    private final UsuarioClient usuarioClient;

    @Transactional
    public Suscripcion crearSuscripcion(SuscripcionRequestDTO dto) {
        log.info("[ms-suscripciones] Intentando crear suscripcion para Usuario ID: {}", dto.getUsuarioId());

        try {
            UsuarioExternoDTO usuario = usuarioClient.obtenerUsuarioPorId(dto.getUsuarioId());
            if (!"ACTIVO".equals(usuario.getEstado())) {
                throw new RuntimeException("El usuario existe pero no está ACTIVO");
            }
        } catch (Exception e) {
            log.error("[ms-suscripciones] Error al validar usuario: {}", e.getMessage());
            throw new RuntimeException("No se pudo validar el usuario en el ms-usuarios");
        }

        PlanSuscripcion plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("El plan de suscripción no existe"));

        // 3. Persistencia
        Suscripcion nueva = new Suscripcion();
        nueva.setUsuarioId(dto.getUsuarioId());
        nueva.setPlan(plan);
        nueva.setFechaInicio(LocalDateTime.now());
        nueva.setEstado("ACTIVA");

        return suscripcionRepository.save(nueva);
    }
}