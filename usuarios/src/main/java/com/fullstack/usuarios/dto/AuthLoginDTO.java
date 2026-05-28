package com.fullstack.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de autenticación (login).
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param correoElectronico El email del usuario.
 * @param password          La contraseña del usuario.
 */
public record AuthLoginDTO(
    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Debe proporcionar un formato de correo electrónico válido.")
    String correoElectronico,

    @NotBlank(message = "La contraseña no puede estar vacía.")
    String password
) {
}
