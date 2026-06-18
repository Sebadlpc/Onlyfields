package com.fullstack.reservas.controller;

import com.fullstack.reservas.dto.ReservaRequestDTO;
import com.fullstack.reservas.dto.ReservaResponseDTO;
import com.fullstack.reservas.service.ReservaService;
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

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones relacionadas con la creación y gestión de reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    @Operation(summary = "Listar todas las reservas", description = "Obtiene una lista de todas las reservas en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    })
    public ResponseEntity<List<ReservaResponseDTO>> listar() {
        return ResponseEntity.ok(reservaService.obtenerTodasLasReservas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una reserva por ID", description = "Busca y devuelve los detalles de una reserva específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@Parameter(description = "ID de la reserva a buscar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerReservaPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar reservas por cliente", description = "Obtiene una lista de todas las reservas de un cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaResponseDTO.class))))
    })
    public ResponseEntity<List<ReservaResponseDTO>> listarPorCliente(@Parameter(description = "ID del cliente para buscar sus reservas", required = true) @PathVariable Long clienteId) {
        return ResponseEntity.ok(reservaService.obtenerPorCliente(clienteId));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva", description = "Registra una nueva reserva en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ReservaResponseDTO> crear(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DTO con los detalles de la reserva a crear") @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crearReserva(dto));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva", description = "Cambia el estado de una reserva a 'CANCELADA'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponseDTO> cancelar(@Parameter(description = "ID de la reserva a cancelar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar una reserva", description = "Cambia el estado de una reserva a 'CONFIRMADA'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva confirmada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<ReservaResponseDTO> confirmarReserva(@Parameter(description = "ID de la reserva a confirmar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmarReserva(id));
    }
}