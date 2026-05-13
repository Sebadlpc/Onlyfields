package com.fullstack.notificaciones.service;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.model.Notificacion;
import com.fullstack.notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repository;

    @Transactional(readOnly = true)
    public List<NotificacionDTO> listarTodas() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificacionDTO crearNotificacion(NotificacionDTO dto) {
        if (dto.getIdempotencyKey() != null) {
            var existente = repository.findByIdempotencyKey(dto.getIdempotencyKey());
            if (existente.isPresent()) return convertToDto(existente.get());
        }

        Notificacion notificacion = convertToEntity(dto);
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setEstado("PENDIENTE");
        notificacion.setIntentos(0);

        return convertToDto(repository.save(notificacion));
    }

    private NotificacionDTO convertToDto(Notificacion n) {
        return NotificacionDTO.builder()
                .id(n.getId())
                .destinatarioId(n.getDestinatarioId())
                .tipo(n.getTipo())
                .canal(n.getCanal())
                .asunto(n.getAsunto())
                .cuerpo(n.getCuerpo())
                .estado(n.getEstado())
                .fechaEnvio(n.getFechaEnvio())
                .intentos(n.getIntentos())
                .idempotencyKey(n.getIdempotencyKey())
                .build();
    }

    private Notificacion convertToEntity(NotificacionDTO dto) {
        return Notificacion.builder()
                .destinatarioId(dto.getDestinatarioId())
                .tipo(dto.getTipo())
                .canal(dto.getCanal())
                .asunto(dto.getAsunto())
                .cuerpo(dto.getCuerpo())
                .idempotencyKey(dto.getIdempotencyKey())
                .build();
    }
}