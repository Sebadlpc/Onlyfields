package com.fullstack.pos.Controller;

import com.fullstack.pos.dto.CajaDTO;
import com.fullstack.pos.dto.TransaccionDTO;
import com.fullstack.pos.Service.PosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pos")
@RequiredArgsConstructor
@Tag(name = "Punto de Venta (POS)", description = "Gestión de cajas transaccionales y registro de ventas")
public class PosController {

    private final PosService posService;

    // --- ENDPOINTS DE CAJA ---

    @PostMapping("/caja/abrir")
    @Operation(summary = "Abrir Caja", description = "Inicializa la jornada de caja con un monto base.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caja abierta exitosamente."),
            @ApiResponse(responseCode = "400", description = "Ya existe una caja abierta para este usuario.")
    })
    public ResponseEntity<CajaDTO> abrirCaja(@RequestParam Long usuarioId, @RequestParam Double montoInicial) {
        CajaDTO caja = posService.abrirCaja(usuarioId, montoInicial);
        return ResponseEntity.ok(caja);
    }

    @PostMapping("/caja/cerrar")
    @Operation(summary = "Cerrar Caja", description = "Cierra la caja activa y cuadra el saldo final.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caja cerrada y arqueada con éxito."),
            @ApiResponse(responseCode = "400", description = "No existe ninguna caja activa para cerrar.")
    })
    public ResponseEntity<CajaDTO> cerrarCaja() {
        CajaDTO caja = posService.cerrarCaja();
        return ResponseEntity.ok(caja);
    }

    @GetMapping("/caja/actual")
    @Operation(summary = "Caja Actual", description = "Obtiene los datos del arqueo de la caja vigente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos de caja obtenidos."),
            @ApiResponse(responseCode = "404", description = "No hay ninguna caja abierta actualmente.")
    })
    public ResponseEntity<CajaDTO> obtenerCajaActual() {
        CajaDTO caja = posService.obtenerCajaActual();
        return ResponseEntity.ok(caja);
    }

    // --- ENDPOINTS DE TRANSACCIONES ---

    @PostMapping("/transacciones")
    @Operation(summary = "Registrar Transacción", description = "Procesa una venta y actualiza el saldo de la caja.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transacción guardada correctamente."),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o la caja se encuentra cerrada.")
    })
    public ResponseEntity<TransaccionDTO> registrarTransaccion(@Valid @RequestBody TransaccionDTO transaccionDto) {
        TransaccionDTO nueva = posService.registrarTransaccion(transaccionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/transacciones/{id}")
    @Operation(summary = "Obtener Transacción", description = "Busca una venta o arriendo mediante su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción localizada con éxito."),
            @ApiResponse(responseCode = "404", description = "ID de transacción no encontrado.")
    })
    public ResponseEntity<TransaccionDTO> obtenerTransaccion(@PathVariable Long id) {
        TransaccionDTO transaccion = posService.obtenerTransaccionPorId(id);
        return ResponseEntity.ok(transaccion);
    }

    @GetMapping("/transacciones")
    @Operation(summary = "Listar Transacciones", description = "Retorna el historial completo de flujos de caja.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial financiero obtenido.")
    })
    public ResponseEntity<List<TransaccionDTO>> listarTransacciones() {
        return ResponseEntity.ok(posService.obtenerTodasLasTransacciones());
    }
}
