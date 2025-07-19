package com.ecabs.Ecabs.ms.service.exceptions;

import java.util.List;

public class DriversNotFoundException extends RuntimeException {
    private List<String> errors;

    public DriversNotFoundException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

}
