package com.fullstack.configuracion.exception;
public class SinPermisoAdminException extends RuntimeException {
    public SinPermisoAdminException(String message) { super(message); }
}