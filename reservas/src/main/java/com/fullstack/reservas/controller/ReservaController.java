package com.fullstack.reservas.controller;

import com.fullstack.reservas.dto.ReservaDTO;
import com.fullstack.reservas.model.Reserva;
import com.fullstack.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fullstack.reservas.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public List<Reserva> listarTodas() {
        return reservaService.listarTodas();
    }

    @GetMapping("/{id}")
    public Reserva buscarPorId(@PathVariable Long id) {
    return reservaService.buscarPorId(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

    }

    @PostMapping
    public Reserva crear(@Valid @RequestBody ReservaDTO dto) {

        Reserva reserva = new Reserva(
                dto.getClienteId(),
                dto.getServicioId(),
                dto.getFechaReserva(),
                dto.getHoraReserva(),
                dto.getEstado(),
                dto.getObservaciones()
        );

        return reservaService.guardar(reserva);
    }

    @PutMapping("/{id}")
    public Reserva actualizar(@PathVariable Long id,
                              @Valid @RequestBody ReservaDTO dto) {

        Reserva reserva = new Reserva(
                dto.getClienteId(),
                dto.getServicioId(),
                dto.getFechaReserva(),
                dto.getHoraReserva(),
                dto.getEstado(),
                dto.getObservaciones()
        );

        return reservaService.actualizar(id, reserva);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return "Reserva eliminada correctamente";
    }
}