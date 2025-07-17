package com.ecabs.Ecabs.ms.dto;

import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;

public class RegisterDriverResponseDTO {
    private Long driverId;
    private String name;
    private String car;
    private Location currentLocation;
    private DriverStatus status;

    public RegisterDriverResponseDTO(){
    }

    public RegisterDriverResponseDTO(Long driverId, String name,String car, Location currentLocation, DriverStatus status) {
        this.driverId = driverId;
        this.status = DriverStatus.AVAILABLE;
        this.currentLocation = currentLocation;
        this.car = car;
        this.name = name;
    }

    public Long getDriverId() {
        return driverId;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public String getCar() {
        return car;
    }

    public String getName() {
        return name;
    }
}
