package com.fullstack.reservas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictoReservaException extends RuntimeException {
    public ConflictoReservaException(String message) {
        super(message);
    }
}
