package com.fullstack.usuarios.exception;

/**
 * Excepción que se lanza al intentar eliminar un rol que todavía tiene usuarios asignados.
 * Será manejada globalmente para devolver un estado HTTP 409 (Conflict).
 */
public class RolConUsuariosException extends RuntimeException {
    public RolConUsuariosException(String message) {
        super(message);
    }
}
