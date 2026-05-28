package com.fullstack.configuracion.repository;

import com.fullstack.configuracion.model.FeriadoBloqueo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la entidad {@link FeriadoBloqueo}.
 * Proporciona todos los métodos CRUD básicos para la gestión de días feriados o de bloqueo.
 * No se necesitan consultas personalizadas adicionales por el momento.
 */
public interface FeriadoRepository extends JpaRepository<FeriadoBloqueo, Long> {
}
