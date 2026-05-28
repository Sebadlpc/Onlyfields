package com.fullstack.configuracion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando se utiliza una clave de configuración
 * que no está en la lista de claves permitidas.
 *
 * Devuelve un estado HTTP 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClaveInvalidaException extends RuntimeException {
    public ClaveInvalidaException(String message) {
        super(message);
    }
}
