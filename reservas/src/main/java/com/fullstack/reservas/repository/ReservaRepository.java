package com.fullstack.reservas.repository;

import com.fullstack.reservas.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Buscar todas las reservas de un cliente
    List<Reserva> findByClienteId(Long clienteId);

    // Buscar todas las reservas de una cancha
    List<Reserva> findByCanchaId(Long canchaId);

    // Buscar reservas por estado 
    List<Reserva> findByEstado(String estado);

    // Buscar reservas de un cliente filtradas por estado
    List<Reserva> findByClienteIdAndEstado(Long clienteId, String estado);

  
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.cancha.id = :canchaId
          AND r.estado != 'CANCELADA'
          AND r.fechaInicio < :fechaFin
          AND r.fechaFin > :fechaInicio
    """)
    List<Reserva> findReservasSolapadas(
        @Param("canchaId")    Long canchaId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin")    LocalDateTime fechaFin
    );
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.cancha.id = :canchaId
          AND r.fechaInicio < :fechaFin
          AND r.fechaFin > :fechaInicio
          AND r.estado = 'CONFIRMADA'
    """)
    List<Reserva> buscarChoquesReserva(
        @Param("canchaId")    Long canchaId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin")    LocalDateTime fechaFin
    );
}