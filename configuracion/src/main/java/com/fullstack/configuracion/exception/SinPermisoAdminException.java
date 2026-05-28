package com.fullstack.configuracion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando un usuario sin rol de 'ADMIN'
 * intenta realizar una operación restringida.
 *
 * Devuelve un estado HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class SinPermisoAdminException extends RuntimeException {
    public SinPermisoAdminException(String message) {
        super(message);
    }
}
