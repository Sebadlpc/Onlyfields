package com.fullstack.reservas.controller;

import com.fullstack.reservas.dto.BloqueHorarioRequestDTO;
import com.fullstack.reservas.dto.BloqueHorarioResponseDTO;
import com.fullstack.reservas.models.BloqueHorario;
import com.fullstack.reservas.repository.BloqueHorarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bloqueos")
@CrossOrigin(origins = "*")
public class BloqueHorarioController {

    @Autowired
    private BloqueHorarioRepository bloqueHorarioRepository;

    // GET /api/bloqueos/cancha/{canchaId}
    @GetMapping("/cancha/{canchaId}")
    public List<BloqueHorarioResponseDTO> listarPorCancha(@PathVariable Long canchaId) {
        return bloqueHorarioRepository.findByCanchaId(canchaId)
                .stream()
                .map(BloqueHorarioResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // POST /api/bloqueos
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody BloqueHorarioRequestDTO dto) {
        // Validar que fechaFin sea posterior a fechaInicio
        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        // Verificar solapamiento con bloqueos existentes
        List<BloqueHorario> conflictos = bloqueHorarioRepository.buscarChoques(
                dto.getCanchaId(),
                dto.getFechaInicio(),
                dto.getFechaFin()
        );

        if (!conflictos.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("No se puede agendar: ya existe un bloqueo en este horario para esta cancha.");
        }

        BloqueHorario guardado = bloqueHorarioRepository.save(dto.toEntity());
        return new ResponseEntity<>(BloqueHorarioResponseDTO.fromEntity(guardado), HttpStatus.CREATED);
    }

    // DELETE /api/bloqueos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (!bloqueHorarioRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el bloqueo con ID: " + id);
        }
        bloqueHorarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}