package com.fullstack.configuracion.controller;

import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.exception.SinPermisoAdminException;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.service.ConfiguracionService;
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
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    // ------------------- Endpoints para Configuraciones Globales -------------------

    /**
     * Obtiene una lista de todas las configuraciones globales del sistema.
     * @return Una lista de todas las configuraciones.
     */
    @GetMapping("/config")
    public ResponseEntity<List<ConfiguracionGlobal>> listarConfiguraciones() {
        return ResponseEntity.ok(configuracionService.listarConfiguraciones());
    }

    /**
     * Obtiene una configuración específica por su clave.
     * @param clave La clave única de la configuración (ej. "TASA_IVA").
     * @return La configuración encontrada.
     */
    @GetMapping("/config/{clave}")
    public ResponseEntity<ConfiguracionGlobal> obtenerConfiguracion(@PathVariable String clave) {
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
    public ResponseEntity<ConfiguracionGlobal> actualizarConfiguracion(
            @PathVariable String clave,
            @RequestHeader(value = "X-User-Role", defaultValue = "USER") String userRole,
            @Valid @RequestBody ConfiguracionRequestDTO dto) {

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
    public ResponseEntity<FeriadoBloqueo> registrarFeriado(@Valid @RequestBody FeriadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configuracionService.registrarFeriado(dto));
    }

    /**
     * Obtiene una lista de todos los feriados o días de bloqueo registrados.
     */
    @GetMapping("/feriados")
    public ResponseEntity<List<FeriadoBloqueo>> listarFeriados() {
        return ResponseEntity.ok(configuracionService.listarFeriados());
    }

    /**
     * Elimina un feriado o día de bloqueo por su ID.
     * @param id El ID del feriado a eliminar.
     */
    @DeleteMapping("/feriados/{id}")
    public ResponseEntity<Void> eliminarFeriado(@PathVariable Long id) {
        configuracionService.eliminarFeriado(id);
        return ResponseEntity.noContent().build();
    }
}
