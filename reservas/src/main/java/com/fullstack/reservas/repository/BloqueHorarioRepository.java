package com.fullstack.reservas.repository;

import com.fullstack.reservas.models.BloqueHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long> {

    // Obtener todos los bloqueos de una cancha
    List<BloqueHorario> findByCanchaId(Long canchaId);

    
    @Query("""
        SELECT b FROM BloqueHorario b
        WHERE b.cancha.id = :canchaId
          AND b.fechaInicio < :fechaFin
          AND b.fechaFin > :fechaInicio
    """)
    List<BloqueHorario> buscarChoques(
        @Param("canchaId")    Long canchaId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin")    LocalDateTime fechaFin
    );
}