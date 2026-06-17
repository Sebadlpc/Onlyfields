package com.fullstack.reportes.controller;

import com.fullstack.reportes.dto.ReporteGeneradoRequestDTO;
import com.fullstack.reportes.dto.ReporteGeneradoResponseDTO;
import com.fullstack.reportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API para la generación y consulta de reportes del sistema")
public class ReporteGeneradoController {

    private final ReporteService reporteService;

    @Operation(summary = "Obtener todos los reportes", description = "Devuelve una lista de todos los reportes generados históricamente.")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @GetMapping
    public List<ReporteGeneradoResponseDTO> listarTodos() {
        return reporteService.obtenerTodos()
                .stream()
                .map(ReporteGeneradoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Obtener reporte por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
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

    @Operation(summary = "Generar un nuevo reporte", description = "Se comunica con los otros microservicios (Feign) para consolidar la data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reporte generado con éxito"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud o en la comunicación con otros MS")
    })
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

    @Operation(summary = "Eliminar un reporte")
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