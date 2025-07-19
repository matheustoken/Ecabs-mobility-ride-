package com.ecabs.Ecabs.ms.entities;

public class Ride {

    private final long rideId;
    private final Driver driver;
    private final Location pickupLocation;
    private boolean isCompleted;

    public Ride(long rideId, Driver driver, Location pickupLocation) {
        this.rideId = rideId;
        this.driver = driver;
        this.pickupLocation = pickupLocation;
        this.isCompleted = false;
    }

    public long getRideId() {
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

    public void completeRide() {
        this.isCompleted = true;
        driver.setStatus(DriverStatus.AVAILABLE);
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}

