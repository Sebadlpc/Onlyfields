package com.fullstack.pos.controller;

import com.fullstack.pos.dto.CajaDTO;
import com.fullstack.pos.dto.TransaccionDTO;
import com.fullstack.pos.service.PosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pos")
@RequiredArgsConstructor
public class PosController {

    private final PosService posService;

    // --- ENDPOINTS DE CAJA ---

    @PostMapping("/caja/abrir")
    public ResponseEntity<CajaDTO> abrirCaja(@RequestParam Long usuarioId, @RequestParam Double montoInicial) {
        CajaDTO caja = posService.abrirCaja(usuarioId, montoInicial);
        return ResponseEntity.ok(caja);
    }

    @PostMapping("/caja/cerrar")
    public ResponseEntity<CajaDTO> cerrarCaja() {
        CajaDTO caja = posService.cerrarCaja();
        return ResponseEntity.ok(caja);
    }

    @GetMapping("/caja/actual")
    public ResponseEntity<CajaDTO> obtenerCajaActual() {
        CajaDTO caja = posService.obtenerCajaActual();
        return ResponseEntity.ok(caja);
    }

    // --- ENDPOINTS DE TRANSACCIONES ---

    @PostMapping("/transacciones")
    public ResponseEntity<TransaccionDTO> registrarTransaccion(@Valid @RequestBody TransaccionDTO transaccionDto) {
        TransaccionDTO nueva = posService.registrarTransaccion(transaccionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/transacciones/{id}")
    public ResponseEntity<TransaccionDTO> obtenerTransaccion(@PathVariable Long id) {
        TransaccionDTO transaccion = posService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(transaccion);
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<TransaccionDTO>> listarTransacciones() {
        return ResponseEntity.ok(posService.obtenerTodasLasTransacciones());
    }
}
