package com.ecabs.Ecabs.ms.controller.handler;


import com.ecabs.Ecabs.ms.dto.exceptions.ErrorResponseDTO;
import com.ecabs.Ecabs.ms.service.exceptions.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String message = "Invalid input data";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(message, details);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleRegisterValidationException(
            ValidationException ex
    ) {
        List<String> details = ex.getErrors();
        String message = "Invalid input data";
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                message,
                details
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        String message = "Invalid value for field 'status or location'";
        List<String> details = List.of("Status must be either 'AVAILABLE' or 'UNAVAILABLE' and location must be a number");

        String exMsg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();

        if (exMsg != null && exMsg.contains("DriverStatus")) {
            message = "Invalid value for field 'status'";
            details = List.of("Status must be either 'AVAILABLE' or 'UNAVAILABLE'");
        }
        else if (exMsg != null && (exMsg.contains("Double") || exMsg.contains("number"))) {
            message = "Invalid value for field 'location'";
            details = List.of("Location must be a numeric value");

        }
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(message, details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String fieldName = ex.getName(); // nome do parâmetro com erro
        String detailMessage = "Invalid value for field location";
        List<String> details = List.of(detailMessage);

        String message = "Invalid input data";
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(message, details);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }



}