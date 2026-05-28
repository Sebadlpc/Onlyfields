package com.fullstack.reportes.service.impl;

import com.fullstack.reportes.dto.ReservaExternaDTO;
import com.fullstack.reportes.model.ReporteGenerado;
import com.fullstack.reportes.repository.ReporteGeneradoRepository;
import com.fullstack.reportes.service.IReporteGeneradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding; // <- NUEVO IMPORT

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ReporteGeneradoServiceImpl implements IReporteGeneradoService {

    @Autowired
    private ReporteGeneradoRepository reporteRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String msReservasUrl = "http://ms-reservas:8080/api/reservas";

    @Override
    @Transactional
    @RegisterReflectionForBinding(ReservaExternaDTO.class) 
    public ReporteGenerado generarReporte(ReporteGenerado reporte) {
        reporte.setFechaGeneracion(LocalDateTime.now());

        if ("FINANCIERO".equalsIgnoreCase(reporte.getTipo())) {
            try {
                ReservaExternaDTO[] reservas = restTemplate.getForObject(msReservasUrl, ReservaExternaDTO[].class);

                BigDecimal totalIngresos = BigDecimal.ZERO; 
                int canchasAlquiladas = 0;

                if (reservas != null) {
                    for (ReservaExternaDTO r : reservas) {
                        String estado = r.getEstado();
                        BigDecimal total = r.getTotalCobrado();

                        if (("CONFIRMADA".equals(estado) || "CANCELADA".equals(estado)) && total != null) {
                            totalIngresos = totalIngresos.add(total);
                        }
                        if ("CONFIRMADA".equals(estado)) {
                            canchasAlquiladas++;
                        }
                    }
                }

                String resultadoJson = "{\"ingresosTotales\": " + totalIngresos + ", \"canchasAlquiladas\": " + canchasAlquiladas + "}";
                
                reporte.setParametros(resultadoJson);
                reporte.setRutaArchivo("Cálculo en vivo (Sin archivo físico)");

            } catch (Exception e) {
                System.err.println("❌ Error al traer datos de ms-reservas: " + e.getMessage());
                reporte.setParametros("{\"error\": \"No se pudo calcular el reporte financiero. Verifica que ms-reservas esté encendido y sin errores.\"}");
            }
        } else {
            reporte.setParametros("{\"mensaje\": \"Tipo de reporte no implementado aún\"}");
            reporte.setRutaArchivo("N/A");
        }

        return reporteRepository.save(reporte);
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteGenerado obtenerPorId(Long id) {
        return reporteRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontró el reporte con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerTodos() { return reporteRepository.findAll(); }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerPorUsuario(Long usuarioId) { return reporteRepository.findByUsuarioId(usuarioId); }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerPorTipo(String tipo) { return reporteRepository.findByTipo(tipo); }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerPorRangoFechas(LocalDateTime desde, LocalDateTime hasta) {
        if (hasta.isBefore(desde)) { throw new RuntimeException("La fecha 'hasta' debe ser posterior a la fecha 'desde'."); }
        return reporteRepository.findByFechaGeneracionBetween(desde, hasta);
    }

    @Override
    @Transactional
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) { throw new RuntimeException("No se puede eliminar: no se encontró el reporte con ID: " + id); }
        reporteRepository.deleteById(id);
    }
}