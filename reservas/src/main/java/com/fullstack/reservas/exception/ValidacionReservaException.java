package com.fullstack.reservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidacionReservaException extends RuntimeException {
    public ValidacionReservaException(String message) {
        super(message);
    }
}
