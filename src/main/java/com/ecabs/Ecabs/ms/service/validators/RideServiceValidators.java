package com.ecabs.Ecabs.ms.service.validators;

import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import com.ecabs.Ecabs.ms.service.exceptions.DriversNotFoundException;
import com.ecabs.Ecabs.ms.service.exceptions.RideNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class RideServiceValidators {

    public List<String> requestRideValidator(Location location){
        List<String> errors = new ArrayList<>();
        if(locationValidator(location) != null){
            errors.add(locationValidator(location));
        }
        return errors;
    }
    public void validateNearestDriver(List<Driver> driver) {
        if (driver == null || driver.isEmpty()) {
            throw new DriversNotFoundException(List.of("No drivers available at the moment"));
        }
    }

    private String locationValidator(Location location) {
        if (location == null) {
            return "Location is required";
        } else {
            if (locationXValidator(location) != null) {
                return locationXValidator(location);
            }
            if (locationYValidator(location) != null) {
                return locationYValidator(location);
            }
        }
        return null;
    }

    private String locationXValidator(Location location) {
        if (location.currentLocationX() == null) {
            return "Location X is required";
        }
        return null;
    }

    private String locationYValidator(Location location) {
        if (location.currentLocationY() == null) {
            return "Location Y is required";
        }
        return null;
    }

    public void validateRideExists(Ride ride){
        if (ride == null){
            throw new RideNotFoundException(List.of("Ride Not Found"));
        }
    }

}
