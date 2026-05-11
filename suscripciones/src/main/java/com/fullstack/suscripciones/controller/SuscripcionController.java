package com.fullstack.suscripciones.controller;

import com.fullstack.suscripciones.dto.SuscripcionRequestDTO;
import com.fullstack.suscripciones.model.Suscripcion;
import com.fullstack.suscripciones.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suscripciones")
@RequiredArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    @PostMapping
    public ResponseEntity<Suscripcion> crear(@Valid @RequestBody SuscripcionRequestDTO dto) {
        return ResponseEntity.status(201).body(suscripcionService.crearSuscripcion(dto));
    }
}