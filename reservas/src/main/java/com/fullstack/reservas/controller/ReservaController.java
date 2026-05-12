package com.fullstack.reservas.controller;

import com.fullstack.reservas.models.Reserva;
import com.fullstack.reservas.service.IReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @GetMapping
    public List<Reserva> listar() {
        return reservaService.obtenerTodasLasReservas();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Reserva reserva) {
        try {
            Reserva nueva = reservaService.crearReserva(reserva);
            return new ResponseEntity<>(nueva, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}") // El {id} debe coincidir con el nombre del parámetro
public ResponseEntity<String> eliminar(@PathVariable Long id) {
    reservaService.eliminarReserva(id);
    return ResponseEntity.ok("Reserva eliminada exitosamente");
    }
}