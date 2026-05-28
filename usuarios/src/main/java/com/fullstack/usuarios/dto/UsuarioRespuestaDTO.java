package com.fullstack.usuarios.dto;

import java.time.LocalDateTime;

/**
 * DTO para devolver la información pública de un usuario.
 * Se utiliza como respuesta en los endpoints para no exponer datos sensibles como el hash de la contraseña.
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param id                El ID único del usuario.
 * @param nombre            El nombre del usuario.
 * @param correoElectronico El email del usuario.
 * @param estado            El estado actual de la cuenta (ej. "ACTIVO").
 * @param fechaCreacion     La fecha en que se creó la cuenta.
 * @param rolNombre         El nombre del rol principal del usuario.
 */
public record UsuarioRespuestaDTO(
    Long id,
    String nombre,
    String correoElectronico,
    String estado,
    LocalDateTime fechaCreacion,
    String rolNombre
) {
}
