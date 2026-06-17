package com.fullstack.reportes.service;

import com.fullstack.reportes.client.InventarioClient;
import com.fullstack.reportes.client.PosClient;
import com.fullstack.reportes.client.ReservasClient;
import com.fullstack.reportes.client.SuscripcionesClient;
import com.fullstack.reportes.model.ReporteGenerado;
import com.fullstack.reportes.repository.ReporteGeneradoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {

    private final ReporteGeneradoRepository reporteRepository;
    private final PosClient posClient;
    private final ReservasClient reservasClient;
    private final SuscripcionesClient suscripcionesClient;
    private final InventarioClient inventarioClient;

    @Transactional
    public ReporteGenerado generarReporte(ReporteGenerado reporte) {
        reporte.setFechaGeneracion(LocalDateTime.now());

        try {
            log.info("Iniciando generación de reporte tipo: {}", reporte.getTipo());

            if ("FINANCIERO".equalsIgnoreCase(reporte.getTipo())) {
                List<Object> transacciones = posClient.obtenerTransacciones();
                List<Object> reservas = reservasClient.obtenerReservas();
                reporte.setParametros("{\"transacciones\": " + transacciones.size() + ", \"reservas\": " + reservas.size() + "}");

            } else if ("INVENTARIO".equalsIgnoreCase(reporte.getTipo())) {
                List<Object> productos = inventarioClient.obtenerProductos();
                reporte.setParametros("{\"productos\": " + productos.size() + "}");

            } else {
                reporte.setParametros("{\"mensaje\": \"Tipo de reporte no implementado aún\"}");
            }

            reporte.setRutaArchivo("Cálculo en vivo (Sin archivo físico)");
        } catch (Exception e) {
            log.error("Error de comunicación con otros MS al generar reporte: {}", e.getMessage());
            reporte.setParametros("{\"error\": \"Fallo de comunicación: " + e.getMessage() + "\"}");
        }

        return reporteRepository.save(reporte);
    }

    @Transactional(readOnly = true)
    public ReporteGenerado obtenerPorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el reporte con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReporteGenerado> obtenerTodos() {
        return reporteRepository.findAll();
    }

    @Transactional
    public void eliminarReporte(Long id) {
        if (!reporteRepository.existsById(id)) {
            throw new RuntimeException("No se encontró el reporte con ID: " + id);
        }
        reporteRepository.deleteById(id);
    }
}