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
}