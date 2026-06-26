package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.dto.*;
import com.fullstack.suscripciones.model.HistorialEstado;
import com.fullstack.suscripciones.service.SuscripcionService;
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

/**
 * Controlador REST para gestionar las suscripciones de los clientes.
 * Expone endpoints para crear, consultar y modificar el estado de las suscripciones.
 */
@RestController
@RequestMapping("/api/v1/suscripciones")
@RequiredArgsConstructor
@Tag(name = "Suscripciones", description = "Operaciones relacionadas con las suscripciones de los clientes")
public class SuscripcionController {

    private final SuscripcionService service;

    /**
     * Crea una nueva suscripción para un cliente a un plan específico.
     * @param dto El DTO con los detalles de la suscripción a crear.
     * @return Un DTO con la información de la suscripción recién creada.
     */
    @PostMapping
    @Operation(summary = "Crear nueva suscripción", description = "Crea una nueva suscripción para un cliente asociado a un plan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Suscripción creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuscripcionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<SuscripcionResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para crear la suscripción") @Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSuscripcion(dto));
    }

    /**
     * Obtiene todas las suscripciones de un cliente específico.
     * @param clienteId El ID del cliente.
     * @return Una lista de DTOs con las suscripciones del cliente.
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar suscripciones por cliente", description = "Obtiene una lista de todas las suscripciones asociadas a un cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SuscripcionResponseDTO.class))))
    })
    public ResponseEntity<List<SuscripcionResponseDTO>> listarPorCliente(
            @Parameter(description = "ID del cliente", required = true) @PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    /**
     * Congela una suscripción activa por un período determinado.
     * @param id El ID de la suscripción a congelar.
     * @param dto El DTO con las fechas de inicio y fin del congelamiento.
     * @return Un DTO con el estado actualizado de la suscripción.
     */
    @PutMapping("/{id}/congelar")
    @Operation(summary = "Congelar suscripción", description = "Congela temporalmente una suscripción activa por un período determinado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suscripción congelada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuscripcionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Suscripción no encontrada", content = @Content)
    })
    public ResponseEntity<SuscripcionResponseDTO> congelar(
            @Parameter(description = "ID de la suscripción a congelar", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fechas para el congelamiento") @Valid @RequestBody CongelarRequestDTO dto) {
        return ResponseEntity.ok(service.congelar(id, dto));
    }

    /**
     * Reactiva una suscripción que estaba congelada.
     * @param id El ID de la suscripción a reactivar.
     * @return Un DTO con el estado actualizado de la suscripción.
     */
    @PutMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar suscripción", description = "Reactiva una suscripción que se encontraba congelada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suscripción reactivada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuscripcionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Suscripción no encontrada", content = @Content)
    })
    public ResponseEntity<SuscripcionResponseDTO> reactivar(
            @Parameter(description = "ID de la suscripción a reactivar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.reactivar(id));
    }

    /**
     * Cancela una suscripción.
     * @param id El ID de la suscripción a cancelar.
     * @return Un DTO con el estado actualizado de la suscripción.
     */
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar suscripción", description = "Cancela definitivamente una suscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suscripción cancelada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuscripcionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Suscripción no encontrada", content = @Content)
    })
    public ResponseEntity<SuscripcionResponseDTO> cancelar(
            @Parameter(description = "ID de la suscripción a cancelar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    /**
     * Obtiene el historial de cambios de estado de una suscripción.
     * @param id El ID de la suscripción.
     * @return Una lista con el historial de estados.
     */
    @GetMapping("/{id}/historial")
    @Operation(summary = "Obtener historial de estados", description = "Obtiene el historial completo de cambios de estado de una suscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HistorialEstado.class)))),
            @ApiResponse(responseCode = "404", description = "Suscripción no encontrada", content = @Content)
    })
    public ResponseEntity<List<HistorialEstado>> obtenerHistorial(
            @Parameter(description = "ID de la suscripción para buscar historial", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerHistorial(id));
    }
}
