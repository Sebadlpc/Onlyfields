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

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> enviar(@Valid @RequestBody NotificacionDTO dto) {
        return new ResponseEntity<>(service.crearNotificacion(dto), HttpStatus.CREATED);
    }
}