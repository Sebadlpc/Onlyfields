package com.fullstack.seguimiento.Controller;

import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.Service.SeguimientoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fichas")
public class SeguimientoController {

    @Autowired
    private SeguimientoService seguimientoService;

    @PostMapping
    public ResponseEntity<?> crearFicha(@Valid @RequestBody FichaClienteDTO fichaDto) {
        try {
            FichaClienteDTO nuevaFicha = seguimientoService.crearFicha(fichaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFicha);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerFichaCliente(@PathVariable Long clienteId) {
        try {
            FichaClienteDTO fichaDto = seguimientoService.obtenerFichaPorCliente(clienteId);
            return ResponseEntity.ok(fichaDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFicha(@PathVariable Long id, @Valid @RequestBody FichaClienteDTO fichaDto) {
        try {
            FichaClienteDTO actualizada = seguimientoService.actualizarFicha(id, fichaDto);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFicha(@PathVariable Long id) {
        try {
            seguimientoService.eliminarFicha(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- SUB-RUTAS DE MEDICIONES ---

    @PostMapping("/{id}/mediciones")
    public ResponseEntity<?> agregarMedicion(@PathVariable Long id, @Valid @RequestBody MedicionCorporalDTO medicionDto) {
        try {
            MedicionCorporalDTO nuevaMedicion = seguimientoService.agregarMedicion(id, medicionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMedicion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/mediciones")
    public ResponseEntity<?> obtenerMediciones(@PathVariable Long id) {
        try {
            List<MedicionCorporalDTO> mediciones = seguimientoService.obtenerHistorialMediciones(id);
            return ResponseEntity.ok(mediciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
