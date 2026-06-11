package com.fullstack.accesos.controller;

import com.fullstack.accesos.dto.RegistroAccesoDTO;
import com.fullstack.accesos.model.QrToken;
import com.fullstack.accesos.model.RegistroAcceso;
import com.fullstack.accesos.model.ResultadoAcceso;
import com.fullstack.accesos.service.AccesosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Control de Accesos",
        description = "Gestion de QR, ingresos y aforo en vivo")
public class AccesosController {

    private final AccesosService accesosService;

    @PostMapping("/qr/generar")
    @Operation(summary = "Generar QR",
                description = "Crear token de acceso para un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "QR creado exitosamente."),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado."),
            @ApiResponse(responseCode = "409", description = "El cliente ya tiene un QR activo.")
    })
    public ResponseEntity<QrToken> generarQr(@RequestParam Long clienteId) {
        QrToken token = accesosService.generarQr(clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/accesos/validar")
    @Operation(summary = "Validar QR",
                    description = "Validad token y registra el ingreso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceso permitido."),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Token inválido/expirado)."),
            @ApiResponse(responseCode = "404", description = "Token no registrado.")
    })
    public ResponseEntity<RegistroAccesoDTO> validarQr(@RequestParam String token) {
        RegistroAcceso registro = accesosService.validarEntrada(token);

        RegistroAccesoDTO dto = RegistroAccesoDTO.builder()
                .clienteId(registro.getClienteId())
                .tipo(registro.getTipo().name())
                .resultado(registro.getResultado().name())
                .motivoRechazo(registro.getMotivoRechazo())
                .fechaHora(registro.getFechaHora())
                .build();

        if (registro.getResultado() == ResultadoAcceso.DENEGADO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(dto);
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/accesos/historial")
    @Operation(summary = "Historial Global",
                    description = "Lista todas las entradas y salidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido con éxito.")
    })
    public ResponseEntity<List<RegistroAcceso>> obtenerHistorial() {
        return ResponseEntity.ok(accesosService.obtenerHistorial());
    }

    @GetMapping("/accesos/cliente/{clienteId}")
    @Operation(summary = "Historial por Cliente",
            description = "Lista todas las entradas y salidas de un cliente especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial del cliente obtenido."),
            @ApiResponse(responseCode = "404", description = "Cliente sin registros.")
    })
    public ResponseEntity<List<RegistroAcceso>> obtenerHistorialCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(accesosService.obtenerHistorialCliente(clienteId));
    }

    @GetMapping("/accesos/activos")
    @Operation(summary = "Clientes Activos",
            description = "Muestra los usuarios dentro del recinto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de aforo actual obtenida.")
    })
    public ResponseEntity<List<RegistroAcceso>> obtenerActivos() {
        return ResponseEntity.ok(accesosService.obtenerActivos());
    }

    @PostMapping("/accesos/salida")
    @Operation(summary = "Registra Salida",
            description = "Registra salida manual y libera aforo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salida procesada correctamente."),
            @ApiResponse(responseCode = "404", description = "El cliente no registra una entrada activa.")
    })
    public ResponseEntity<RegistroAcceso> registrarSalida(@RequestParam Long clienteId) {
        RegistroAcceso registro = accesosService.registrarSalida(clienteId);
        return ResponseEntity.ok(registro);
    }
}
