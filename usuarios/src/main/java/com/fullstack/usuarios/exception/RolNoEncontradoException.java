package com.fullstack.usuarios.exception;

/**
 * Excepción que se lanza cuando se busca un rol y no se encuentra en la base de datos.
 * Será manejada globalmente para devolver un estado HTTP 404 (Not Found).
 */
public class RolNoEncontradoException extends RuntimeException {
    public RolNoEncontradoException(String message) {
        super(message);
    }
}
