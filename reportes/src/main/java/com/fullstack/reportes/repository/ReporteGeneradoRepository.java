package com.fullstack.reportes.repository;

import com.fullstack.reportes.model.ReporteGenerado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteGeneradoRepository extends JpaRepository<ReporteGenerado, Long> {

    // Buscar todos los reportes de un usuario
    List<ReporteGenerado> findByUsuarioId(Long usuarioId);

    // Buscar por tipo de reporte 
    List<ReporteGenerado> findByTipo(String tipo);

    // Buscar reportes generados en un rango de fechas
    List<ReporteGenerado> findByFechaGeneracionBetween(LocalDateTime desde, LocalDateTime hasta);

    // Buscar reportes de un usuario filtrados por tipo
    List<ReporteGenerado> findByUsuarioIdAndTipo(Long usuarioId, String tipo);
}