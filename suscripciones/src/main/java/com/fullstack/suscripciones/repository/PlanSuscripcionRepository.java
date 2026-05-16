package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.model.PlanSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanSuscripcionRepository extends JpaRepository<PlanSuscripcion, Long> {
    Optional<PlanSuscripcion> findByNombre(String nombre);
}