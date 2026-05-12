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

    // GET /api/reservas
    @GetMapping
    public List<ReservaResponseDTO> listar() {
        return reservaService.obtenerTodasLasReservas()
                .stream()
                .map(ReservaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /api/reservas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.obtenerReservaPorId(id);
        return ResponseEntity.ok(ReservaResponseDTO.fromEntity(reserva));
    }

    // POST /api/reservas
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ReservaRequestDTO dto) {
        try {
            Reserva nueva = reservaService.crearReserva(dto.toEntity());
            return new ResponseEntity<>(ReservaResponseDTO.fromEntity(nueva), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/reservas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ReservaRequestDTO dto) {
        try {
            Reserva actualizada = reservaService.actualizarReserva(id, dto.toEntity());
            return ResponseEntity.ok(ReservaResponseDTO.fromEntity(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE /api/reservas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            reservaService.eliminarReserva(id);
            return ResponseEntity.ok("Reserva eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}