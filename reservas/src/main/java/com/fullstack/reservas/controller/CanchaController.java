package com.fullstack.reservas.controller;

import com.fullstack.reservas.dto.*;
import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.models.BloqueHorario;
import com.fullstack.reservas.repository.CanchaRepository;
import com.fullstack.reservas.repository.BloqueHorarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/canchas")
@CrossOrigin(origins = "*")
public class CanchaController {

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private BloqueHorarioRepository bloqueHorarioRepository;

    @GetMapping
    public List<CanchaResponseDTO> listarTodas() {
        return canchaRepository.findAll().stream().map(CanchaResponseDTO::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CanchaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return canchaRepository.findById(id)
                .map(CanchaResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/bloquear")
    public ResponseEntity<?> bloquearCancha(@PathVariable Long id, @Valid @RequestBody BloqueHorarioRequestDTO dto) {
        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) return ResponseEntity.badRequest().body("Fechas inválidas");
        
        dto.setCanchaId(id); // Forzamos el ID de la URL
        if (!bloqueHorarioRepository.buscarChoques(id, dto.getFechaInicio(), dto.getFechaFin()).isEmpty()) {
            return ResponseEntity.badRequest().body("Ya existe un bloqueo en este horario.");
        }
        
        return new ResponseEntity<>(BloqueHorarioResponseDTO.fromEntity(bloqueHorarioRepository.save(dto.toEntity())), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/desbloquear/{bloqueoId}")
    public ResponseEntity<?> desbloquearCancha(@PathVariable Long id, @PathVariable Long bloqueoId) {
        if (!bloqueHorarioRepository.existsById(bloqueoId)) return ResponseEntity.notFound().build();
        bloqueHorarioRepository.deleteById(bloqueoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CanchaRequestDTO dto) {
        return new ResponseEntity<>(CanchaResponseDTO.fromEntity(canchaRepository.save(dto.toEntity())), HttpStatus.CREATED);
    }
}