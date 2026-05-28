package com.fullstack.usuarios.exception;

/**
 * Excepción que se lanza cuando se busca un usuario y no se encuentra en la base de datos.
 * Será manejada globalmente para devolver un estado HTTP 404 (Not Found).
 */
public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}
