package com.ecabs.Ecabs.ms.entities;

public class Ride {
    private final Long rideId;
    private final Driver driver;
    private final Location pickupLocation;
    private boolean isCompleted;

    public Ride(Long rideId, Driver driver, Location pickupLocation) {
        this.rideId = rideId;
        this.driver = driver;
        this.pickupLocation = pickupLocation;
        this.isCompleted = false;
    }

    public Long getRideId() {
        return rideId;
    }

    public Long setRideId(){
        return rideId;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

