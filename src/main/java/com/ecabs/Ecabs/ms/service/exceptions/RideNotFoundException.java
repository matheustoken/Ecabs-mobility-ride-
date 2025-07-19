package com.ecabs.Ecabs.ms.service.exceptions;

import java.util.List;

public class RideNotFoundException extends RuntimeException {
    private List<String> errors;

    public RideNotFoundException(List<String> errors) {
        super("Ride Not Found");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

}
