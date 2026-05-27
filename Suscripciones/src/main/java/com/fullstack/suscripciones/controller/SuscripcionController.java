package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.dto.*;
import com.fullstack.suscripciones.model.HistorialEstado;
import com.fullstack.suscripciones.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService service;

    @PostMapping
    public ResponseEntity<SuscripcionResponseDTO> crear(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearSuscripcion(dto));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<SuscripcionResponseDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @PutMapping("/{id}/congelar")
    public ResponseEntity<SuscripcionResponseDTO> congelar(@PathVariable Long id, @Valid @RequestBody CongelarRequestDTO dto) {
        return ResponseEntity.ok(service.congelar(id, dto));
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<SuscripcionResponseDTO> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reactivar(id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<SuscripcionResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstado>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerHistorial(id));
    }
}