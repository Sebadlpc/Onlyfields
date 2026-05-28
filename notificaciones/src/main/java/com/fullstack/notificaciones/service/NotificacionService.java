package com.fullstack.notificaciones.service;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.model.Notificacion;
import com.fullstack.notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repository;

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

        Notificacion guardada = repository.save(notificacion);
        
        // Ejecuta el envío en segundo plano (Fire-and-forget)
        procesarEnvio(guardada.getId());

        return convertToDto(guardada);
    }

    @Async 
    @Transactional
    public void procesarEnvio(Long id) {
        repository.findById(id).ifPresent(notificacion -> {
            try {
                notificacion.setIntentos(notificacion.getIntentos() + 1);

                notificacion.setEstado("ENVIADO");
                notificacion.setFechaEnvio(LocalDateTime.now());
            } catch (Exception e) {
                // Si falla y ya superó los 3 intentos (backoff simulado)
                if (notificacion.getIntentos() >= 3) {
                    notificacion.setEstado("FALLIDO");
                }
            }
            repository.save(notificacion);
        });
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificacionDTO obtenerPorId(Long id) {
        return repository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> obtenerPorCliente(Long clienteId) {
        return repository.findByDestinatarioId(clienteId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> obtenerPendientes() {
        return repository.findByEstado("PENDIENTE").stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reenviar(Long id) {
        repository.findById(id).ifPresent(n -> {
            n.setEstado("PENDIENTE");
            repository.save(n);
            procesarEnvio(n.getId()); // Reintenta asíncronamente
        });
    }

    private NotificacionDTO convertToDto(Notificacion n) {
        return NotificacionDTO.builder()
                .id(n.getId())
                .destinatarioId(n.getDestinatarioId())
                .destinatarioEmail(n.getDestinatarioEmail())
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
                .destinatarioEmail(dto.getDestinatarioEmail())
                .tipo(dto.getTipo())
                .canal(dto.getCanal())
                .asunto(dto.getAsunto())
                .cuerpo(dto.getCuerpo())
                .idempotencyKey(dto.getIdempotencyKey())
                .build();
    }
    
}