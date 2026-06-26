package com.fullstack.configuracion.controller;

import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.exception.SinPermisoAdminException;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.service.ConfiguracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las configuraciones globales y los feriados/bloqueos del sistema.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Configuración", description = "Operaciones relacionadas con las configuraciones del sistema y feriados")
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    // ------------------- Endpoints para Configuraciones Globales -------------------

    /**
     * Obtiene una lista de todas las configuraciones globales del sistema.
     * @return Una lista de todas las configuraciones.
     */
    @GetMapping("/config")
    @Operation(summary = "Listar configuraciones globales", description = "Obtiene una lista de todas las configuraciones globales del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConfiguracionGlobal.class))))
    })
    public ResponseEntity<List<ConfiguracionGlobal>> listarConfiguraciones() {
        return ResponseEntity.ok(configuracionService.listarConfiguraciones());
    }

    /**
     * Obtiene una configuración específica por su clave.
     * @param clave La clave única de la configuración (ej. "TASA_IVA").
     * @return La configuración encontrada.
     */
    @GetMapping("/config/{clave}")
    @Operation(summary = "Obtener configuración por clave", description = "Obtiene los detalles de una configuración específica mediante su clave única")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracionGlobal.class))),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada", content = @Content)
    })
    public ResponseEntity<ConfiguracionGlobal> obtenerConfiguracion(
            @Parameter(description = "Clave única de la configuración", required = true) @PathVariable String clave) {
        return ResponseEntity.ok(configuracionService.obtenerPorClave(clave));
    }

    /**
     * Actualiza el valor de una configuración existente.
     * Este endpoint requiere que el rol del usuario sea 'ADMIN'.
     * @param clave La clave de la configuración a actualizar.
     * @param userRole El rol del usuario, obtenido de la cabecera 'X-User-Role'.
     * @param dto El DTO con el nuevo valor para la configuración.
     * @throws SinPermisoAdminException si el usuario no es 'ADMIN'.
     */
    @PutMapping("/config/{clave}")
    @Operation(summary = "Actualizar configuración", description = "Modifica el valor de una configuración existente (Requiere rol ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracionGlobal.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador", content = @Content),
            @ApiResponse(responseCode = "404", description = "Configuración no encontrada", content = @Content)
    })
    public ResponseEntity<ConfiguracionGlobal> actualizarConfiguracion(
            @Parameter(description = "Clave de la configuración a modificar", required = true) @PathVariable String clave,
            @Parameter(description = "Rol del usuario para autorización") @RequestHeader(value = "X-User-Role", defaultValue = "USER") String userRole,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos para la configuración") @Valid @RequestBody ConfiguracionRequestDTO dto) {

        // Simulación de autorización.
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            throw new SinPermisoAdminException("Solo los administradores pueden modificar configuraciones.");
        }
        return ResponseEntity.ok(configuracionService.actualizarConfiguracion(clave, dto));
    }

    // ------------------- Endpoints para Feriados y Bloqueos -------------------

    /**
     * Registra un nuevo día feriado o de bloqueo en el sistema.
     * @param dto El DTO con la información del feriado.
     */
    @PostMapping("/feriados")
    @Operation(summary = "Registrar feriado", description = "Registra un nuevo día feriado o de bloqueo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Feriado registrado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FeriadoBloqueo.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<FeriadoBloqueo> registrarFeriado(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para registrar el feriado") @Valid @RequestBody FeriadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configuracionService.registrarFeriado(dto));
    }

    /**
     * Obtiene una lista de todos los feriados o días de bloqueo registrados.
     */
    @GetMapping("/feriados")
    @Operation(summary = "Listar feriados", description = "Obtiene una lista de todos los feriados o días de bloqueo registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FeriadoBloqueo.class))))
    })
    public ResponseEntity<List<FeriadoBloqueo>> listarFeriados() {
        return ResponseEntity.ok(configuracionService.listarFeriados());
    }

    /**
     * Elimina un feriado o día de bloqueo por su ID.
     * @param id El ID del feriado a eliminar.
     */
    @DeleteMapping("/feriados/{id}")
    @Operation(summary = "Eliminar feriado", description = "Elimina un feriado o día de bloqueo del sistema mediante su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feriado eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Feriado no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminarFeriado(
            @Parameter(description = "ID del feriado a eliminar", required = true) @PathVariable Long id) {
        configuracionService.eliminarFeriado(id);
        return ResponseEntity.noContent().build();
    }
}
