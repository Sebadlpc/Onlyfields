package com.fullstack.pos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransaccionException extends RuntimeException {
    public TransaccionException(String message) {
        super(message);
    }
}
