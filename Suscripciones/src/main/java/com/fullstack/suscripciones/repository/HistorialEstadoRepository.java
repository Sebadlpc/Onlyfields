package com.fullstack.suscripciones.repository;
import com.fullstack.suscripciones.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {
    List<HistorialEstado> findBySuscripcionIdOrderByIdDesc(Long suscripcionId);
}