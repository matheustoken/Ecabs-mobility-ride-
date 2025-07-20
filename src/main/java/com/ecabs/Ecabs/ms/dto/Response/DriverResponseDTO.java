package com.ecabs.Ecabs.ms.dto.Response;

import com.ecabs.Ecabs.ms.entities.Location;

public class DriverResponseDTO {
    private Long  driverId;
    private String name;
    private String car;
    private Double distance;


    public DriverResponseDTO(){
    }

    public DriverResponseDTO(String name, String car, Double distance) {
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
