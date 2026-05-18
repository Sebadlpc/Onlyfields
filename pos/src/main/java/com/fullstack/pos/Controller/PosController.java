package com.fullstack.pos.Controller;

import com.fullstack.pos.Model.Caja;
import com.fullstack.pos.Model.Transaccion;
import com.fullstack.pos.Service.PosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PosController {

    @Autowired
    private PosService posService;

    // --- ENDPOINTS DE CAJA ---

    @PostMapping("/caja/abrir")
    public ResponseEntity<?> abrirCaja(@RequestParam Long usuarioId, @RequestParam Double montoInicial) {
        try {
            Caja caja = posService.abrirCaja(usuarioId, montoInicial);
            return ResponseEntity.ok(caja);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/caja/cerrar")
    public ResponseEntity<?> cerrarCaja() {
        try {
            Caja caja = posService.cerrarCaja();
            return ResponseEntity.ok(caja);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/caja/actual")
    public ResponseEntity<?> obtenerCajaActual() {
        try {
            Caja caja = posService.obtenerCajaActual();
            return ResponseEntity.ok(caja);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- ENDPOINTS DE TRANSACCIONES ---

    @PostMapping("/transacciones")
    public ResponseEntity<?> registrarTransaccion(@RequestBody Transaccion transaccion) {
        try {
            Transaccion nueva = posService.registrarTransaccion(transaccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/transacciones/{id}")
    public ResponseEntity<?> obtenerTransaccion(@PathVariable Long id) {
        try {
            Transaccion transaccion = posService.obtenerTransaccionPorId(id);
            return ResponseEntity.ok(transaccion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<Transaccion>> listarTransacciones() {
        return ResponseEntity.ok(posService.obtenerTodasLasTransacciones());
    }

    // --- ENDPOINTS DE REPORTES Y COMPROBANTES (Mocks temporales) ---

    @GetMapping("/reportes/diario")
    public ResponseEntity<String> reporteDiario() {
        // Aquí iría la lógica de agrupación por día, devolvemos un string para cumplir con el endpoint
        return ResponseEntity.ok("Reporte diario en construcción (Pendiente lógica MS-Reportes)");
    }

    @GetMapping("/comprobantes/{id}")
    public ResponseEntity<String> descargarComprobante(@PathVariable Long id) {
        // Generar un PDF real requiere librerías como iText o JasperReports
        return ResponseEntity.ok("Enlace simulado para descargar comprobante PDF de transacción #" + id);
    }
}
