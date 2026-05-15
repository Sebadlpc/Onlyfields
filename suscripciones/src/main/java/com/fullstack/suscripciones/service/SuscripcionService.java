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

    private final UsuarioClient usuarioClient;
    private final SuscripcionRepository repository;

    public Suscripcion crearSuscripcion(Long usuarioId, Suscripcion suscripcion) {
        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        
        if (usuario == null) {
            throw new RuntimeException("El usuario no existe");
        }
        
        suscripcion.setUsuarioId(usuario.getId());
        return repository.save(suscripcion);
    }
}