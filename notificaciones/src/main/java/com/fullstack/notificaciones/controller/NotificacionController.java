package com.fullstack.notificaciones.controller;

import com.fullstack.notificaciones.dto.NotificacionDTO;
import com.fullstack.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService service;

    // 0. GET Todas las notificaciones (Historial completo)
    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // 1. Encolar email (fire-and-forget) -> Responde 202 Accepted
    @PostMapping("/email")
    public ResponseEntity<NotificacionDTO> encolarEmail(@Valid @RequestBody NotificacionDTO dto) {
        return new ResponseEntity<>(service.crearNotificacion(dto), HttpStatus.ACCEPTED);
    }

    // 2. Detalle de notificación por ID
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // 3. Histórico por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacionDTO>> obtenerPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.obtenerPorCliente(clienteId));
    }

    // 4. Notificaciones pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionDTO>> obtenerPendientes() {
        return ResponseEntity.ok(service.obtenerPendientes());
    }

    // 5. Reenviar manualmente -> Responde 202 Accepted
    @PostMapping("/reenviar/{id}")
    public ResponseEntity<Void> reenviar(@PathVariable Long id) {
        service.reenviar(id);
        return ResponseEntity.accepted().build();
    }

    // 6. Recibir la reserva y traducirla a NotificacionDTO
    @PostMapping("/enviar-comprobante")
    public ResponseEntity<String> enviarComprobante(@RequestBody java.util.Map<String, Object> datosReserva) {
        
        System.out.println("✅ Petición recibida desde ms-reservas: " + datosReserva);

        // Extraemos el clienteId de la reserva (viene como número en el JSON)
        Number clienteId = (Number) datosReserva.get("clienteId");
        Long idDestinatario = clienteId != null ? clienteId.longValue() : 1L;

        // Armamos el DTO
        NotificacionDTO notificacion = NotificacionDTO.builder()
                .destinatarioId(idDestinatario)
                .destinatarioEmail("cliente" + idDestinatario + "@onlyfields.com")
                .tipo("COMPROBANTE")
                .canal("EMAIL")
                .asunto("Confirmación de Reserva OnlyFields")
                .cuerpo("Tu reserva ha sido confirmada exitosamente. Total cobrado: $" + datosReserva.get("totalCobrado"))
                .idempotencyKey(java.util.UUID.randomUUID().toString()) // Generamos una clave única
                .build();

        // Le pasamos el DTO válido a tu servicio existente
        service.crearNotificacion(notificacion);
        
        System.out.println("✅ Comprobante encolado exitosamente para el cliente " + idDestinatario);

        // Retornamos el String que ms-reservas está esperando
        return ResponseEntity.ok("Comprobante processed correctamente");
    }
}