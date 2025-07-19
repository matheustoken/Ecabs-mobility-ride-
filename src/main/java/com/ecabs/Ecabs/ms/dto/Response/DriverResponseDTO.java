package com.ecabs.Ecabs.ms.dto.Response;

public class DriverResponseDTO {
    private long  driverId;
    private String name;
    private String car;
    private double distance;

    public DriverResponseDTO(){
    }

    public DriverResponseDTO(String name, String car, double distance) {
        this.name = name;
        this.car = car;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getCar() {
        return car;
    }

    public double getDistance() {
        return distance;
    }
}
