package com.fullstack.notificaciones.controller;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.service.NotificacionService;
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
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Operaciones relacionadas con el envío y consulta de notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones", description = "Obtiene un historial completo de todas las notificaciones enviadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NotificacionDTO.class))))
    })
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @PostMapping("/email")
    @Operation(summary = "Encolar una nueva notificación por email", description = "Registra y encola una nueva notificación para ser enviada por email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Notificación aceptada para procesamiento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<NotificacionDTO> encolarEmail(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "DTO con los detalles de la notificación") @Valid @RequestBody NotificacionDTO dto) {
        return new ResponseEntity<>(service.crearNotificacion(dto), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una notificación por ID", description = "Busca y devuelve los detalles de una notificación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<NotificacionDTO> obtenerPorId(@Parameter(description = "ID de la notificación a buscar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener notificaciones por cliente", description = "Obtiene un historial de todas las notificaciones enviadas a un cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NotificacionDTO.class))))
    })
    public ResponseEntity<List<NotificacionDTO>> obtenerPorCliente(@Parameter(description = "ID del cliente para buscar sus notificaciones", required = true) @PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Obtener notificaciones pendientes", description = "Obtiene una lista de todas las notificaciones que aún no han sido enviadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NotificacionDTO.class))))
    })
    public ResponseEntity<List<NotificacionDTO>> obtenerPendientes() {
        return ResponseEntity.ok(service.obtenerPendientes());
    }

    @PostMapping("/reenviar/{id}")
    @Operation(summary = "Reenviar una notificación", description = "Marca una notificación existente para ser reenviada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Notificación aceptada para reenvío"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<Void> reenviar(@Parameter(description = "ID de la notificación a reenviar", required = true) @PathVariable Long id) {
        service.reenviar(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/enviar-comprobante")
    @Operation(summary = "Recibir y procesar un comprobante de reserva", description = "Endpoint interno para recibir datos de una reserva y encolar una notificación de comprobante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comprobante procesado correctamente")
    })
    public ResponseEntity<String> enviarComprobante(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la reserva en formato JSON genérico") @RequestBody java.util.Map<String, Object> datosReserva) {
        
        System.out.println("✅ Petición recibida desde ms-reservas: " + datosReserva);

        Number clienteId = (Number) datosReserva.get("clienteId");
        Long idDestinatario = clienteId != null ? clienteId.longValue() : 1L;

        NotificacionDTO notificacion = NotificacionDTO.builder()
                .destinatarioId(idDestinatario)
                .destinatarioEmail("cliente" + idDestinatario + "@onlyfields.com")
                .tipo("COMPROBANTE")
                .canal("EMAIL")
                .asunto("Confirmación de Reserva OnlyFields")
                .cuerpo("Tu reserva ha sido confirmada exitosamente. Total cobrado: $" + datosReserva.get("totalCobrado"))
                .idempotencyKey(java.util.UUID.randomUUID().toString())
                .build();

        service.crearNotificacion(notificacion);
        
        System.out.println("✅ Comprobante encolado exitosamente para el cliente " + idDestinatario);

        return ResponseEntity.ok("Comprobante processed correctamente");
    }
}