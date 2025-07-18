package com.ecabs.Ecabs.ms.dto;

import com.ecabs.Ecabs.ms.entities.Location;

public class RideResponseDTO {

    private Long rideId;
    private String driverName;
    private String carName;
    private Location driverLocation;

    public RideResponseDTO(){
    }


    public RideResponseDTO(long rideId, String driverName, String carName, Location driverLocation) {
        this.rideId = rideId;
        this.driverName = driverName;
        this.carName = carName;
        this.driverLocation = driverLocation;
    }

    public long getRideId() {
        return rideId;
    }
    public String getCarName() {
        return carName;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public Location getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(Location driverLocation) {
        this.driverLocation = driverLocation;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

}
