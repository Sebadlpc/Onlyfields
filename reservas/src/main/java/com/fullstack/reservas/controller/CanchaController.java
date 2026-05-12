package com.fullstack.reservas.controller;

import com.fullstack.reservas.dto.CanchaRequestDTO;
import com.fullstack.reservas.dto.CanchaResponseDTO;
import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.repository.CanchaRepository;
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

    // GET /api/canchas
    @GetMapping
    public List<CanchaResponseDTO> listarTodas() {
        return canchaRepository.findAll()
                .stream()
                .map(CanchaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // GET /api/canchas/deporte/{deporte}
    @GetMapping("/deporte/{deporte}")
    public List<CanchaResponseDTO> listarPorDeporte(@PathVariable String deporte) {
        return canchaRepository.findByDeporte(deporte)
                .stream()
                .map(CanchaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // POST /api/canchas
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CanchaRequestDTO dto) {
        if (canchaRepository.existsByNombre(dto.getNombre())) {
            return ResponseEntity.badRequest().body("Ya existe una cancha con ese nombre.");
        }
        return new ResponseEntity<>(
                CanchaResponseDTO.fromEntity(canchaRepository.save(dto.toEntity())),
                HttpStatus.CREATED
        );
    }

    // DELETE /api/canchas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (!canchaRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la cancha con ID: " + id);
        }
        canchaRepository.deleteById(id);
        return ResponseEntity.ok("Cancha eliminada exitosamente");
    }
    @PutMapping("/{id}")
public ResponseEntity<Cancha> actualizar(@PathVariable Long id, @RequestBody Cancha detallesCancha) {
    return canchaRepository.findById(id)
            .map(cancha -> {
                cancha.setNombre(detallesCancha.getNombre());
                cancha.setDeporte(detallesCancha.getDeporte());
                cancha.setCapacidad(detallesCancha.getCapacidad());
                cancha.setTarifaHora(detallesCancha.getTarifaHora());
                cancha.setEstado(detallesCancha.getEstado());
                return ResponseEntity.ok(canchaRepository.save(cancha));
            })
            .orElse(ResponseEntity.notFound().build());
}
}