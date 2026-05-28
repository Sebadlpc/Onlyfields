package com.fullstack.suscripciones.repository;

import com.fullstack.suscripciones.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad {@link Suscripcion}.
 */
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    /**
     * Busca todas las suscripciones (activas e inactivas) asociadas a un ID de cliente.
     * @param clienteId El ID del cliente.
     * @return Una lista de suscripciones.
     */
    List<Suscripcion> findByClienteId(Long clienteId);

    /**
     * Busca una suscripción de un cliente específico que se encuentre en un estado determinado.
     * Útil para validar si un cliente ya tiene una suscripción activa antes de crear una nueva.
     * @param clienteId El ID del cliente.
     * @param estado El estado de la suscripción a buscar (ej. "ACTIVA").
     * @return Un {@link Optional} que contiene la suscripción si se encuentra.
     */
    Optional<Suscripcion> findByClienteIdAndEstado(Long clienteId, String estado);

    /**
     * Busca todas las suscripciones que se encuentran en un estado específico.
     * Utilizado por la tarea programada para encontrar todas las suscripciones activas
     * y verificar si han vencido.
     * @param estado El estado a buscar.
     * @return Una lista de suscripciones en el estado especificado.
     */
    List<Suscripcion> findAllByEstado(String estado);
}
