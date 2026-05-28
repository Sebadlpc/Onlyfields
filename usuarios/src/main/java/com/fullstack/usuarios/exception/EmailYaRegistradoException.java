package com.fullstack.usuarios.exception;

/**
 * Excepción que se lanza al intentar registrar un usuario con un correo electrónico que ya existe.
 * Será manejada globalmente para devolver un estado HTTP 409 (Conflict).
 */
public class EmailYaRegistradoException extends RuntimeException {
    public EmailYaRegistradoException(String message) {
        super(message);
    }
}
