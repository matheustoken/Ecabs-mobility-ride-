package com.ecabs.Ecabs.ms.entities;

import jakarta.validation.constraints.NotBlank;

public class Driver {


    private Long driverId;

    private String name;
    private String car;
    private Location currentLocation;
    private DriverStatus status;

    public Driver(){
    }


    public Driver(Long driverId, String name, String car, Location currentLocation) {
        this.driverId = driverId;
        this.currentLocation = currentLocation;
        this.car = car;
        this.name = name;
        this.status = DriverStatus.AVAILABLE;
    }

    public Long getDriverId() {
        return driverId;
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

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
