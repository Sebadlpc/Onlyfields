package com.fullstack.configuracion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de actualización de una configuración global.
 * Se define como un 'record' de Java para concisión e inmutabilidad.
 *
 * @param valor       El nuevo valor para la configuración. No puede estar vacío.
 * @param descripcion La nueva descripción (opcional).
 * @param usuarioId   ID del usuario que realiza la modificación, para auditoría. No puede ser nulo.
 */
public record ConfiguracionRequestDTO(
    @NotBlank(message = "El valor no puede estar vacío")
    String valor,

    String descripcion,

    @NotNull(message = "El ID del usuario es obligatorio para auditoría")
    Long usuarioId
) {
}
