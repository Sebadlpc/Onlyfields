package com.fullstack.reportes.service;

import com.fullstack.reportes.model.ReporteGenerado;

import java.time.LocalDateTime;
import java.util.List;

public interface IReporteGeneradoService {

    ReporteGenerado generarReporte(ReporteGenerado reporte);

    ReporteGenerado obtenerPorId(Long id);

    List<ReporteGenerado> obtenerTodos();

    List<ReporteGenerado> obtenerPorUsuario(Long usuarioId);

    List<ReporteGenerado> obtenerPorTipo(String tipo);

    List<ReporteGenerado> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta);

    void eliminarReporte(Long id);
}