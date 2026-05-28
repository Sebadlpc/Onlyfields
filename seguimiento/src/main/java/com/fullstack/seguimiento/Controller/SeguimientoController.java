package com.fullstack.seguimiento.controller;

import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.service.SeguimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seguimiento/fichas")
@RequiredArgsConstructor
public class SeguimientoController {

    private final SeguimientoService seguimientoService;

    @PostMapping
    public ResponseEntity<FichaClienteDTO> crearFicha(@RequestHeader("X-Usuario-Id") Long creadorId, @Valid @RequestBody FichaClienteDTO fichaDto) {
        FichaClienteDTO nuevaFicha = seguimientoService.crearFicha(creadorId, fichaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFicha);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<FichaClienteDTO> obtenerFichaCliente(@PathVariable Long clienteId) {
        FichaClienteDTO fichaDto = seguimientoService.obtenerFichaPorCliente(clienteId);
        return ResponseEntity.ok(fichaDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaClienteDTO> actualizarFicha(@PathVariable Long id, @Valid @RequestBody FichaClienteDTO fichaDto) {
        FichaClienteDTO actualizada = seguimientoService.actualizarFicha(id, fichaDto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFicha(@PathVariable Long id) {
        seguimientoService.eliminarFicha(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mediciones")
    public ResponseEntity<MedicionCorporalDTO> agregarMedicion(@PathVariable Long id, @Valid @RequestBody MedicionCorporalDTO medicionDto) {
        MedicionCorporalDTO nuevaMedicion = seguimientoService.agregarMedicion(id, medicionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMedicion);
    }

    @GetMapping("/{id}/mediciones")
    public ResponseEntity<List<MedicionCorporalDTO>> obtenerMediciones(@PathVariable Long id) {
        List<MedicionCorporalDTO> mediciones = seguimientoService.obtenerHistorialMediciones(id);
        return ResponseEntity.ok(mediciones);
    }
}
