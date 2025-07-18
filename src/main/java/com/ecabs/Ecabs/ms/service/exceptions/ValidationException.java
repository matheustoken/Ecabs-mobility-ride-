package com.ecabs.Ecabs.ms.service.exceptions;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    private List<String> errors;

    public ValidationException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

}
