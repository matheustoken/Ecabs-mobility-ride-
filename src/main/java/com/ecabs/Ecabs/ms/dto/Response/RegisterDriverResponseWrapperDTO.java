package com.ecabs.Ecabs.ms.dto.Response;

public class RegisterDriverResponseWrapperDTO {

    private String message;
    private RegisterDriverResponseDTO driver;

    public RegisterDriverResponseWrapperDTO(String message,RegisterDriverResponseDTO driver) {
        this.message = message;
        this.driver = driver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RegisterDriverResponseDTO getDriver() {
        return driver;
    }

    public void setDriver(RegisterDriverResponseDTO driver) {
        this.driver = driver;
    }
}

