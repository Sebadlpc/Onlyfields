package com.fullstack.reservas.controller;

import com.fullstack.reservas.models.BloqueHorario;
import com.fullstack.reservas.repository.BloqueHorarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bloqueos")
@CrossOrigin(origins = "*")
public class BloqueHorarioController {

    @Autowired
    private BloqueHorarioRepository bloqueHorarioRepository;

    @GetMapping("/cancha/{canchaId}")
    public List<BloqueHorario> listarPorCancha(@PathVariable Long canchaId) {
        return bloqueHorarioRepository.findByCanchaId(canchaId);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody BloqueHorario bloque) {
    // 1. Llamas a tu consulta actual
    List<BloqueHorario> conflictos = bloqueHorarioRepository.buscarChoques(
        bloque.getCancha().getId(), 
        bloque.getFechaInicio(), 
        bloque.getFechaFin()
    );

    // 2. Si la lista tiene algo, es que SI hay un bloqueo
    if (!conflictos.isEmpty()) {
        return ResponseEntity.badRequest()
            .body("No se puede agendar: Ya existe un bloqueo en este horario para esta cancha.");
    }

    // 3. Si está vacía, procede a guardar
    return new ResponseEntity<>(bloqueHorarioRepository.save(bloque), HttpStatus.CREATED);
}
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bloqueHorarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}