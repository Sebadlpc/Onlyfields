package com.fullstack.accesos.controller;

import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.service.AccesosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AccesosController {

    @Autowired
    private AccesosService accesosService;

    // POST /api/v1/qr/generar
    @PostMapping("/qr/generar")
    public ResponseEntity<QrToken> generarQr(@RequestParam Long clienteId) {
        QrToken token = accesosService.generarQr(clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    // POST /api/v1/accesos/validar
    @PostMapping("/accesos/validar")
    public ResponseEntity<RegistroAcceso> validarQr(@RequestParam String token) {
        RegistroAcceso registro = accesosService.validarEntrada(token);

        if (registro.getResultado() == ResultadoAcceso.DENEGADO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(registro);
        }
        return ResponseEntity.ok(registro);
    }

    // GET /api/v1/accesos/historial
    @GetMapping("/accesos/historial")
    public ResponseEntity<List<RegistroAcceso>> obtenerHistorial() {
        return ResponseEntity.ok(accesosService.obtenerHistorial());
    }

    // GET /api/v1/accesos/cliente/{clienteId}
    @GetMapping("/accesos/cliente/{clienteId}")
    public ResponseEntity<List<RegistroAcceso>> obtenerHistorialCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(accesosService.obtenerHistorialCliente(clienteId));
    }

    // GET /api/v1/accesos/activos
    @GetMapping("/accesos/activos")
    public ResponseEntity<List<RegistroAcceso>> obtenerActivos() {
        return ResponseEntity.ok(accesosService.obtenerActivos());
    }

    // POST /api/v1/accesos/salida
    @PostMapping("/accesos/salida")
    public ResponseEntity<RegistroAcceso> registrarSalida(@RequestParam Long clienteId) {
        RegistroAcceso registro = accesosService.registrarSalida(clienteId);
        return ResponseEntity.ok(registro);
    }
}
