package com.fullstack.notificaciones.repository;

import com.fullstack.notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    Optional<Notificacion> findByIdempotencyKey(String idempotencyKey);
    List<Notificacion> findByDestinatarioId(Long destinatarioId);
    List<Notificacion> findByEstado(String estado);
}