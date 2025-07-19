package com.ecabs.Ecabs.ms.dto.exceptions;

import java.util.List;


public class ErrorResponseDTO {

    private ErrorDetail error;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(String message, List<String> details) {
        this.error = new ErrorDetail(message, details);
    }

    public ErrorDetail getError() {
        return error;
    }

    public void setError(ErrorDetail error) {
        this.error = error;
    }

    public static class ErrorDetail {
        private String message;
        private List<String> details;

        public ErrorDetail() {
        }

        public ErrorDetail(String message, List<String> details) {
            this.message = message;
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getDetails() {
            return details;
        }

        public void setDetails(List<String> details) {
            this.details = details;
        }
    }
}
