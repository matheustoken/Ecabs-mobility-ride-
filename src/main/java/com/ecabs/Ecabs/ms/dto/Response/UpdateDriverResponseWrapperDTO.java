package com.ecabs.Ecabs.ms.dto.Response;

public class UpdateDriverResponseWrapperDTO {

    private String message;
    private UpdateDriverResponseDTO driver;

    public UpdateDriverResponseWrapperDTO(String message, UpdateDriverResponseDTO driver) {
        this.message = message;
        this.driver = driver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdateDriverResponseDTO getDriver() {
        return driver;
    }

    public void setDriver(UpdateDriverResponseDTO driver) {
        this.driver = driver;
    }
}
