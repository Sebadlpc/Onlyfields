package com.fullstack.notificaciones.controller;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Operaciones relacionadas con el envío y consulta de notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    @PostMapping("/enviar-comprobante")
    @Operation(summary = "Enviar comprobante de reserva")
    public ResponseEntity<String> enviarComprobante(@RequestBody Map<String, Object> datosReserva) {
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
        return ResponseEntity.ok("Comprobante procesado correctamente");
    }

    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones")
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener notificación por ID")
    public ResponseEntity<NotificacionDTO> obtenerPorId(@PathVariable Long id) {
        NotificacionDTO notificacion = service.obtenerNotificacion(id); // Usa el nombre correcto del service
        return notificacion != null ? ResponseEntity.ok(notificacion) : ResponseEntity.notFound().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener notificaciones por cliente")
    public ResponseEntity<List<NotificacionDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorDestinatario(clienteId)); // Usa el nombre correcto del service
    }
}