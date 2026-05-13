package com.fullstack.reportes.service.impl;

import com.fullstack.reportes.model.ReporteGenerado;
import com.fullstack.reportes.repository.ReporteGeneradoRepository;
import com.fullstack.reportes.service.IReporteGeneradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteGeneradoServiceImpl implements IReporteGeneradoService {

    @Autowired
    private ReporteGeneradoRepository reporteRepository;

    
    @Override
    @Transactional
    public ReporteGenerado generarReporte(ReporteGenerado reporte) {
        // Asignar la fecha de generación automáticamente al momento de crear
        reporte.setFechaGeneracion(LocalDateTime.now());
        return reporteRepository.save(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteGenerado obtenerPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el reporte con ID: " + id
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerTodos() {
        return reporteRepository.findAll();
    }
    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerPorUsuario(Long usuarioId) {
        return reporteRepository.findByUsuarioId(usuarioId);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerPorTipo(String tipo) {
        return reporteRepository.findByTipo(tipo);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        if (hasta.isBefore(desde)) {
            throw new RuntimeException(
                    "La fecha 'hasta' debe ser posterior a la fecha 'desde'."
            );
        }
        return reporteRepository.findByFechaGeneracionBetween(desde, hasta);
    }

    @Override
    @Transactional
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException(
                    "No se puede eliminar: no se encontró el reporte con ID: " + id
            );
        }
        reporteRepository.deleteById(id);
    }
}