package com.fullstack.usuarios.dto;

import java.time.LocalDateTime;

/**
 * DTO estándar para devolver respuestas de error en el API.
 * Proporciona una estructura consistente para todos los errores.
 *
 * @param timestamp La fecha y hora en que ocurrió el error.
 * @param status    El código de estado HTTP.
 * @param error     Una descripción corta del estado HTTP (ej. "Not Found").
 * @param message   El mensaje detallado de la excepción.
 * @param path      La ruta del endpoint que originó el error.
 */
public record ErrorRespuestaDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {
}
