package com.fullstack.reportes.controller;

import com.fullstack.reportes.dto.ReporteGeneradoRequestDTO;
import com.fullstack.reportes.dto.ReporteGeneradoResponseDTO;
import com.fullstack.reportes.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "API para la generación y consulta de reportes del sistema")
public class ReporteGeneradoController {

    private final ReporteService reporteService;

    @GetMapping
    @Operation(summary = "Obtener todos los reportes", description = "Devuelve una lista de todos los reportes generados históricamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReporteGeneradoResponseDTO.class))))
    })
    public List<ReporteGeneradoResponseDTO> listarTodos() {
        return reporteService.obtenerTodos()
                .stream()
                .map(ReporteGeneradoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reporte por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReporteGeneradoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public ResponseEntity<?> obtenerPorId(@Parameter(description = "ID del reporte a buscar", required = true) @PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    ReporteGeneradoResponseDTO.fromEntity(reporteService.obtenerPorId(id))
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Generar un nuevo reporte", description = "Se comunica con los otros microservicios (Feign) para consolidar la data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reporte generado con éxito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReporteGeneradoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud o en la comunicación con otros MS")
    })
    public ResponseEntity<?> generar(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DTO con los detalles del reporte a generar") @Valid @RequestBody ReporteGeneradoRequestDTO dto) {
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un reporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public ResponseEntity<String> eliminar(@Parameter(description = "ID del reporte a eliminar", required = true) @PathVariable Long id) {
        try {
            reporteService.eliminarReporte(id);
            return ResponseEntity.ok("Reporte eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}