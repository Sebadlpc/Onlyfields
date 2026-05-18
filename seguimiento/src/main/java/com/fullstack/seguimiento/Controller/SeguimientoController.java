package com.fullstack.seguimiento.Controller;

import com.fullstack.seguimiento.Model.FichaCliente;
import com.fullstack.seguimiento.Model.MedicionCorporal;
import com.fullstack.seguimiento.Service.SeguimientoService;
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

    // POST /api/v1/fichas (Crea ficha para cliente)
    @PostMapping
    public ResponseEntity<?> crearFicha(@RequestBody FichaCliente ficha) {
        try {
            FichaCliente nuevaFicha = seguimientoService.crearFicha(ficha);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFicha);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // GET /api/v1/fichas/cliente/{clienteId} (Obtiene ficha del cliente)
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> obtenerFichaCliente(@PathVariable Long clienteId) {
        try {
            FichaCliente ficha = seguimientoService.obtenerFichaPorCliente(clienteId);
            return ResponseEntity.ok(ficha);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // PUT /api/v1/fichas/{id} (Actualiza ficha médica)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarFicha(@PathVariable Long id, @RequestBody FichaCliente ficha) {
        try {
            FichaCliente actualizada = seguimientoService.actualizarFicha(id, ficha);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE /api/v1/fichas/{id} (Elimina ficha)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFicha(@PathVariable Long id) {
        try {
            seguimientoService.eliminarFicha(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- SUB-RUTAS DE MEDICIONES ---

    // POST /api/v1/fichas/{id}/mediciones (Agrega medición corporal a la ficha)
    @PostMapping("/{id}/mediciones")
    public ResponseEntity<?> agregarMedicion(@PathVariable Long id, @RequestBody MedicionCorporal medicion) {
        try {
            MedicionCorporal nuevaMedicion = seguimientoService.agregarMedicion(id, medicion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMedicion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // GET /api/v1/fichas/{id}/mediciones (Historial de mediciones de una ficha)
    @GetMapping("/{id}/mediciones")
    public ResponseEntity<?> obtenerMediciones(@PathVariable Long id) {
        try {
            List<MedicionCorporal> mediciones = seguimientoService.obtenerHistorialMediciones(id);
            return ResponseEntity.ok(mediciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
