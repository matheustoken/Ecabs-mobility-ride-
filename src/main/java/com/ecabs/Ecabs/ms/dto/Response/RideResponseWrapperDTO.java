package com.ecabs.Ecabs.ms.dto.Response;

public class RideResponseWrapperDTO {
    private String message;
    private RideResponseDTO ride;

    public RideResponseWrapperDTO(String message, RideResponseDTO ride) {
        this.message = message;
        this.ride = ride;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RideResponseDTO getRide() {
        return ride;
    }

    public void setRide(RideResponseDTO ride) {
        this.ride = ride;
    }
}
