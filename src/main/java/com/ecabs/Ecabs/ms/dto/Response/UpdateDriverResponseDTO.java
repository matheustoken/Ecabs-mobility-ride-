package com.ecabs.Ecabs.ms.dto.Response;

import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;

public class UpdateDriverResponseDTO {

    private String name;
    private String car;
    private Location currentLocation;
    private DriverStatus status;

    public UpdateDriverResponseDTO(){
    }

    public UpdateDriverResponseDTO(String name,String car, Location currentLocation, DriverStatus status) {
        this.car = car;
        this.name = name;
        this.currentLocation = currentLocation;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }


}
