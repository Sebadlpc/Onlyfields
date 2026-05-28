package com.fullstack.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para representar la información de un Rol.
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param id          El ID único del rol.
 * @param nombre      El nombre del rol (ej. "ADMIN").
 * @param descripcion Una breve descripción de los permisos del rol.
 */
public record RolDTO(
    Long id,

    @NotBlank(message = "El nombre del rol no puede estar vacío.")
    String nombre,

    String descripcion
) {
}
