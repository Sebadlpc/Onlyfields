package com.fullstack.usuarios.exception;

/**
 * Excepción que se lanza durante el login si la contraseña es incorrecta o la cuenta no es válida.
 * Será manejada globalmente para devolver un estado HTTP 401 (Unauthorized).
 */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
