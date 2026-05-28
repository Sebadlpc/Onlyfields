package com.fullstack.usuarios.controller;

import com.fullstack.usuarios.dto.AuthLoginDTO;
import com.fullstack.usuarios.dto.UsuarioRegistroDTO;
import com.fullstack.usuarios.dto.UsuarioRespuestaDTO;
import com.fullstack.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para gestionar las operaciones de usuarios.
 * Expone endpoints para registro, login, consulta y eliminación de usuarios.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    // Inyección de dependencias del servicio de usuario.
    private final UsuarioService usuarioService;

    /**
     * Obtiene la información pública de un usuario por su ID.
     * @param id El ID del usuario a buscar.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param dto El DTO con los datos de registro del usuario.
     */
    @PostMapping
    public ResponseEntity<UsuarioRespuestaDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuario(dto));
    }

    /**
     * Autentica a un usuario en el sistema.
     * @param dto El DTO con las credenciales de login (email y contraseña).
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioRespuestaDTO> login(@Valid @RequestBody AuthLoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    /**
     * Obtiene una lista de todos los usuarios registrados.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioRespuestaDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    /**
     * Elimina un usuario del sistema por su ID.
     * @param id El ID del usuario a eliminar.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
