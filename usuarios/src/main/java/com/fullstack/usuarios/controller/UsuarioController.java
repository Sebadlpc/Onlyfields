package com.fullstack.usuarios.controller;

import com.fullstack.usuarios.dto.AuthLoginDTO;
import com.fullstack.usuarios.dto.UsuarioRegistroDTO;
import com.fullstack.usuarios.dto.UsuarioRespuestaDTO;
import com.fullstack.usuarios.service.UsuarioService;
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
 * Controlador REST para gestionar las operaciones de usuarios.
 * Expone endpoints para registro, login, consulta y eliminación de usuarios.
 */
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Registro, modificacion y eliminacion de usuarios dentro del microservicio.")
public class UsuarioController {

    // Inyección de dependencias del servicio de usuario.
    private final UsuarioService usuarioService;

    /**
     * Obtiene la información pública de un usuario por su ID.
     * @param id El ID del usuario a buscar.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID", description = "Obtiene los datos públicos de un usuario específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioRespuestaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    public ResponseEntity<UsuarioRespuestaDTO> obtenerPorId(
            @Parameter(description = "ID del usuario a buscar", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param dto El DTO con los datos de registro del usuario.
     */
    @PostMapping
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioRespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<UsuarioRespuestaDTO> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para el registro del nuevo usuario") @Valid @RequestBody UsuarioRegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuario(dto));
    }

    /**
     * Autentica a un usuario en el sistema.
     * @param dto El DTO con las credenciales de login (email y contraseña).
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar un usuario", description = "Valida las credenciales de un usuario y devuelve sus datos si son correctas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioRespuestaDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    })
    public ResponseEntity<UsuarioRespuestaDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Credenciales para el inicio de sesión") @Valid @RequestBody AuthLoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    /**
     * Obtiene una lista de todos los usuarios registrados.
     */
    @GetMapping
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene una lista de todos los usuarios registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UsuarioRespuestaDTO.class))))
    })
    public ResponseEntity<List<UsuarioRespuestaDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    /**
     * Elimina un usuario del sistema por su ID.
     * @param id El ID del usuario a eliminar.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema en base a su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del usuario a eliminar", required = true) @PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
