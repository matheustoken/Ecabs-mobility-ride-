package com.ecabs.Ecabs.ms.dto;

public class ResponseCompleteRideDTO {
    private String message;
    private String status;

    public ResponseCompleteRideDTO(String message, String status) {
        this.message = message;
        this.status =status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
