package com.ecabs.Ecabs.ms.dto.Request;

import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateDriverRequestDTO {


    @NotBlank(message = "Location X is required")
    @NotNull(message = "Location is required")
    private Location currentLocation;
    @NotBlank(message = "Status is required")
    @NotNull(message = "Status is required")
    private DriverStatus status;


    UpdateDriverRequestDTO(){
    }

    public UpdateDriverRequestDTO(Long id,Location currentLocation, DriverStatus status) {
        this.currentLocation = currentLocation;
        this.status = status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }
}
