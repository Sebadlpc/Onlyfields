package com.fullstack.configuracion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción que se lanza cuando se intenta acceder a una configuración
 * que no existe en la base de datos.
 *
 * Devuelve un estado HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConfigNoEncontradaException extends RuntimeException {
    public ConfigNoEncontradaException(String message) {
        super(message);
    }
}
