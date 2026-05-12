package com.fullstack.reservas.controller;

import com.fullstack.reservas.models.Cancha;
import com.fullstack.reservas.repository.CanchaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canchas")
@CrossOrigin(origins = "*")
public class CanchaController {

    @Autowired
    private CanchaRepository canchaRepository;

    @GetMapping
    public List<Cancha> listarTodas() {
        return canchaRepository.findAll();
    }

    @GetMapping("/deporte/{deporte}")
    public List<Cancha> listarPorDeporte(@PathVariable String deporte) {
        return canchaRepository.findByDeporte(deporte);
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Cancha cancha) {
        if (canchaRepository.existsByNombre(cancha.getNombre())) {
            return ResponseEntity.badRequest().body("Ya existe una cancha con ese nombre.");
        }
        return new ResponseEntity<>(canchaRepository.save(cancha), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        if (!canchaRepository.existsById(id)) return ResponseEntity.notFound().build();
        canchaRepository.deleteById(id);
        return ResponseEntity.ok("Cancha eliminada exitosamente");
    }
}