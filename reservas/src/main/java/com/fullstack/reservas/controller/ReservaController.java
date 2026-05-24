package com.fullstack.reservas.controller;

import com.fullstack.reservas.dto.ReservaRequestDTO;
import com.fullstack.reservas.dto.ReservaResponseDTO;
import com.fullstack.reservas.models.Reserva;
import com.fullstack.reservas.service.IReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @GetMapping
    public List<ReservaResponseDTO> listar() {
        return reservaService.obtenerTodasLasReservas().stream().map(ReservaResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ReservaResponseDTO.fromEntity(reservaService.obtenerReservaPorId(id)));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<ReservaResponseDTO> listarPorCliente(@PathVariable Long clienteId) {
        return reservaService.obtenerPorCliente(clienteId).stream().map(ReservaResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        try {
            return new ResponseEntity<>(ReservaResponseDTO.fromEntity(reservaService.crearReserva(dto.toEntity())), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ReservaResponseDTO.fromEntity(reservaService.cancelarReserva(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
    Reserva reservaConfirmada = reservaService.confirmarReserva(id);
    return ResponseEntity.ok(reservaConfirmada);
}
}