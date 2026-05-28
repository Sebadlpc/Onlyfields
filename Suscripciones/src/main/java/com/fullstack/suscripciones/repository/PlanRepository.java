package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Plan}.
 * Proporciona todos los métodos CRUD básicos para la gestión de los planes de suscripción.
 * No se necesitan consultas personalizadas adicionales por el momento.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
}
