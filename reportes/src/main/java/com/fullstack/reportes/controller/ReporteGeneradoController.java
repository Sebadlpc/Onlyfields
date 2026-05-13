package com.fullstack.reportes.controller;

import com.fullstack.reportes.dto.ReporteGeneradoRequestDTO;
import com.fullstack.reportes.dto.ReporteGeneradoResponseDTO;
import com.fullstack.reportes.service.IReporteGeneradoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteGeneradoController {

    @Autowired
    private IReporteGeneradoService reporteService;

    // GET /api/reportes
    @GetMapping
    public List<ReporteGeneradoResponseDTO> listarTodos() {
        return reporteService.obtenerTodos()
                .stream()
                .map(ReporteGeneradoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /api/reportes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    ReporteGeneradoResponseDTO.fromEntity(reporteService.obtenerPorId(id))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // GET /api/reportes/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public List<ReporteGeneradoResponseDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return reporteService.obtenerPorUsuario(usuarioId)
                .stream()
                .map(ReporteGeneradoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /api/reportes/tipo/{tipo}
    @GetMapping("/tipo/{tipo}")
    public List<ReporteGeneradoResponseDTO> listarPorTipo(@PathVariable String tipo) {
        return reporteService.obtenerPorTipo(tipo)
                .stream()
                .map(ReporteGeneradoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    
    @GetMapping("/rango")
    public ResponseEntity<?> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        try {
            List<ReporteGeneradoResponseDTO> resultado = reporteService
                    .obtenerPorRangoFechas(desde, hasta)
                    .stream()
                    .map(ReporteGeneradoResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /api/reportes
    @PostMapping
    public ResponseEntity<?> generar(@Valid @RequestBody ReporteGeneradoRequestDTO dto) {
        try {
            return new ResponseEntity<>(
                    ReporteGeneradoResponseDTO.fromEntity(
                            reporteService.generarReporte(dto.toEntity())
                    ),
                    HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/reportes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return ResponseEntity.ok("Reporte eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}