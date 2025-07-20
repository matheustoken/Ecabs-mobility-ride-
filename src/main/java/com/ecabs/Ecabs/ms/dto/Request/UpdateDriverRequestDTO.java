package com.ecabs.Ecabs.ms.dto.Request;

import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;

public class UpdateDriverRequestDTO {
    private Location currentLocation;
    private DriverStatus status;

    public UpdateDriverRequestDTO( Location currentLocation, DriverStatus status) {
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
