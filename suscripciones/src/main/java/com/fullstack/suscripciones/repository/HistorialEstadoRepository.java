package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio de Spring Data JPA para la entidad {@link HistorialEstado}.
 * Proporciona métodos CRUD y consultas para el historial de cambios de estado de las suscripciones.
 */
@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    /**
     * Busca todo el historial de estados de una suscripción específica,
     * ordenado por ID en orden descendente (los cambios más recientes primero).
     *
     * @param suscripcionId El ID de la suscripción.
     * @return Una lista ordenada del historial de estados.
     */
    List<HistorialEstado> findBySuscripcionIdOrderByIdDesc(Long suscripcionId);
}
