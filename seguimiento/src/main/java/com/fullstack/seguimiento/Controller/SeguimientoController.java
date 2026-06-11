package com.fullstack.seguimiento.controller;

import com.fullstack.seguimiento.dto.FichaClienteDTO;
import com.fullstack.seguimiento.dto.MedicionCorporalDTO;
import com.fullstack.seguimiento.service.SeguimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/seguimiento/fichas")
@RequiredArgsConstructor
@Tag(name = "Seguimiento Deportivo", description = "Gestión de fichas médicas y registros corporales")
public class SeguimientoController {

    private final SeguimientoService seguimientoService;

    @PostMapping
    @Operation(summary = "Crear Ficha", description = "Registra una nueva ficha médica para un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ficha creada con éxito."),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos."),
            @ApiResponse(responseCode = "401", description = "Falta el encabezado de identificación.")
    })
    public ResponseEntity<FichaClienteDTO> crearFicha(
            @Parameter(description = "ID del usuario/profesional que crea la ficha", required = true, example = "10")
            @RequestHeader("X-Usuario-Id") Long creadorId,
            @Valid @RequestBody FichaClienteDTO fichaDto) {
        FichaClienteDTO nuevaFicha = seguimientoService.crearFicha(creadorId, fichaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFicha);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener Ficha por Cliente", description = "Busca la ficha activa de un socio específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha obtenida correctamente."),
            @ApiResponse(responseCode = "404", description = "El cliente no tiene ficha registrada.")
    })
    public ResponseEntity<FichaClienteDTO> obtenerFichaCliente(@PathVariable Long clienteId) {
        FichaClienteDTO fichaDto = seguimientoService.obtenerFichaPorCliente(clienteId);
        return ResponseEntity.ok(fichaDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Ficha", description = "Modifica los datos generales de una ficha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficha actualizada con éxito."),
            @ApiResponse(responseCode = "404", description = "ID de ficha no encontrado.")
    })
    public ResponseEntity<FichaClienteDTO> actualizarFicha(@PathVariable Long id, @Valid @RequestBody FichaClienteDTO fichaDto) {
        FichaClienteDTO actualizada = seguimientoService.actualizarFicha(id, fichaDto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Ficha", description = "Elimina de forma permanente o lógica una ficha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ficha eliminada correctamente."),
            @ApiResponse(responseCode = "404", description = "ID de ficha no encontrado.")
    })
    public ResponseEntity<Void> eliminarFicha(@PathVariable Long id) {
        seguimientoService.eliminarFicha(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/mediciones")
    @Operation(summary = "Agregar Medición", description = "Registra un nuevo control físico (peso, grasa, etc.) en la ficha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medición añadida correctamente."),
            @ApiResponse(responseCode = "400", description = "Cálculos o rangos corporales inválidos."),
            @ApiResponse(responseCode = "404", description = "ID de ficha no encontrado.")
    })
    public ResponseEntity<MedicionCorporalDTO> agregarMedicion(@PathVariable Long id, @Valid @RequestBody MedicionCorporalDTO medicionDto) {
        MedicionCorporalDTO nuevaMedicion = seguimientoService.agregarMedicion(id, medicionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMedicion);
    }

    @GetMapping("/{id}/mediciones")
    @Operation(summary = "Historial de Mediciones", description = "Lista todas las mediciones asociadas a una ficha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial corporal obtenido con éxito."),
            @ApiResponse(responseCode = "404", description = "ID de ficha no encontrado.")
    })
    public ResponseEntity<List<MedicionCorporalDTO>> obtenerMediciones(@PathVariable Long id) {
        List<MedicionCorporalDTO> mediciones = seguimientoService.obtenerHistorialMediciones(id);
        return ResponseEntity.ok(mediciones);
    }
}
