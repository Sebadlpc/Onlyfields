package com.fullstack.configuracion.controller;

import com.fullstack.configuracion.dto.ConfiguracionRequestDTO;
import com.fullstack.configuracion.dto.FeriadoRequestDTO;
import com.fullstack.configuracion.model.ConfiguracionGlobal;
import com.fullstack.configuracion.model.FeriadoBloqueo;
import com.fullstack.configuracion.service.ConfiguracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;


    @GetMapping("/config")
    public ResponseEntity<List<ConfiguracionGlobal>> listarConfiguraciones() {
        return ResponseEntity.ok(configuracionService.listarConfiguraciones());
    }

    @GetMapping("/config/{clave}")
    public ResponseEntity<ConfiguracionGlobal> obtenerConfiguracion(@PathVariable String clave) {
        return ResponseEntity.ok(configuracionService.obtenerPorClave(clave));
    }

    @PutMapping("/config/{clave}")
    public ResponseEntity<ConfiguracionGlobal> actualizarConfiguracion(
            @PathVariable String clave,
            @RequestHeader(value = "X-User-Role", defaultValue = "ADMIN") String userRole,
            @Valid @RequestBody ConfiguracionRequestDTO dto) {

        if (!"ADMIN".equals(userRole)) {
            throw new RuntimeException("SinPermisoAdminException: Solo el Administrador puede modificar configuraciones.");
        }
        return ResponseEntity.ok(configuracionService.actualizarConfiguracion(clave, dto));
    }


    @PostMapping("/feriados")
    public ResponseEntity<FeriadoBloqueo> registrarFeriado(@Valid @RequestBody FeriadoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configuracionService.registrarFeriado(dto));
    }

    @GetMapping("/feriados")
    public ResponseEntity<List<FeriadoBloqueo>> listarFeriados() {
        return ResponseEntity.ok(configuracionService.listarFeriados());
    }

    @DeleteMapping("/feriados/{id}")
    public ResponseEntity<Void> eliminarFeriado(@PathVariable Long id) {
        configuracionService.eliminarFeriado(id);
        return ResponseEntity.noContent().build();
    }
}