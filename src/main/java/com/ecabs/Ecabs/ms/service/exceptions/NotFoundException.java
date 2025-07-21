package com.ecabs.Ecabs.ms.service.exceptions;

import java.util.List;

public class NotFoundException extends RuntimeException {
    private final List<String> errors;

    public NotFoundException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
