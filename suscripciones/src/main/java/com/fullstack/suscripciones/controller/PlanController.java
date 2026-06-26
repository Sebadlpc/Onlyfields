package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.model.Plan;
import com.fullstack.suscripciones.repository.PlanRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
@Tag(name = "Planes", description = "Operaciones relacionadas con los planes de suscripción")
public class PlanController {

    private final PlanRepository planRepository;

    @GetMapping
    @Operation(summary = "Obtener todos los planes", description = "Obtiene una lista de todos los planes disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Plan.class))))
    })
    public ResponseEntity<List<Plan>> listarTodos() {
        return ResponseEntity.ok(planRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un plan por ID", description = "Busca y devuelve los detalles de un plan específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Plan.class))),
            @ApiResponse(responseCode = "404", description = "Plan no encontrado", content = @Content)
    })
    public ResponseEntity<Plan> obtenerDetalle(
            @Parameter(description = "ID del plan a buscar", required = true) @PathVariable Long id) {
        return planRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
