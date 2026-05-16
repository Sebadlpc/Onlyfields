package com.fullstack.suscripciones.service;

import com.fullstack.suscripciones.client.UsuarioClient;
import com.fullstack.suscripciones.dto.external.UsuarioExternoDTO;
import com.fullstack.suscripciones.model.Suscripcion;
import com.fullstack.suscripciones.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuscripcionService {

    private final UsuarioClient usuarioClient;
    private final SuscripcionRepository repository;

    public List<Suscripcion> listarTodas() {
        return repository.findAll();
    }

    public Suscripcion obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
    }

    public Suscripcion crear(Suscripcion suscripcion) {
        UsuarioExternoDTO usuario = usuarioClient.obtenerUsuarioPorId(suscripcion.getClienteId());
        
        if (usuario == null) {
            throw new RuntimeException("El cliente no existe en ms-usuarios");
        }
        
        suscripcion.setEstado("ACTIVA");
        return repository.save(suscripcion);
    }

    public Suscripcion actualizar(Long id, Suscripcion suscripcionActualizada) {
        Suscripcion existente = obtenerPorId(id);
        existente.setFechaFin(suscripcionActualizada.getFechaFin());
        existente.setEstado(suscripcionActualizada.getEstado());
        return repository.save(existente);
    }

    public void eliminar(Long id) {
        Suscripcion existente = obtenerPorId(id);
        existente.setEstado("CANCELADA");
        repository.save(existente);
    }
}