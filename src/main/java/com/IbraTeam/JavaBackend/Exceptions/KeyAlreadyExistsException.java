package com.IbraTeam.JavaBackend.Exceptions;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class KeyAlreadyExistsException extends BadRequestException {
    public KeyAlreadyExistsException(String message) {
        super(message);
    }
}
