package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.model.Suscripcion;
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

    private final SuscripcionService suscripcionService;

    @GetMapping
    public ResponseEntity<List<Suscripcion>> listarTodas() {
        return ResponseEntity.ok(suscripcionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Suscripcion> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(suscripcionService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Suscripcion> crear(@Valid @RequestBody Suscripcion suscripcion) {
        Suscripcion nuevaSuscripcion = suscripcionService.crear(suscripcion);
        return new ResponseEntity<>(nuevaSuscripcion, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Suscripcion> actualizar(@PathVariable Long id, @Valid @RequestBody Suscripcion suscripcion) {
        return ResponseEntity.ok(suscripcionService.actualizar(id, suscripcion));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        suscripcionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}