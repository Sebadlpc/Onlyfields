package com.fullstack.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 * Se define como un 'record' para concisión e inmutabilidad.
 *
 * @param nombre            El nombre completo del usuario.
 * @param correoElectronico El email del usuario, que será su identificador para login.
 * @param password          La contraseña. Debe tener al menos 8 caracteres.
 * @param rolId             El ID del rol que se asignará al usuario.
 */
public record UsuarioRegistroDTO(
    @NotBlank(message = "El nombre no puede estar vacío.")
    String nombre,

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Debe proporcionar un formato de correo electrónico válido.")
    String correoElectronico,

    @NotBlank(message = "La contraseña не puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    String password,

    @NotNull(message = "Debe especificar un ID de rol para el usuario.")
    Long rolId
) {
}
