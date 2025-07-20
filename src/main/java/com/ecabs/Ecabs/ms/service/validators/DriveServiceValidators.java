package com.ecabs.Ecabs.ms.service.validators;

import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Request.UpdateDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.service.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DriveServiceValidators {
    public List<String> registerDriverValidator(RegisterDriverRequestDTO driver) {
        List<String> errors = new ArrayList<>();

        if (driver.getCar() == null || driver.getCar().trim().isEmpty()) {
            errors.add("Car is required");
        }

        if (driver.getName() == null || driver.getName().trim().isEmpty()) {
            errors.add("Driver name is required");
        }

        if (locationValidator(driver.getCurrentLocation()) != null) {
            errors.add(locationValidator(driver.getCurrentLocation()));
        }

        return errors;
    }

    public Boolean updateDriverIdValidator(Long driverId, Map<Long, Driver> drivers) {
        return driverId ==null|| !drivers.containsKey(driverId);
    }

    public List<String> updateDriverValidator(UpdateDriverRequestDTO driver) {
        List<String> errors = new ArrayList<>();

        if (locationValidator(driver.getCurrentLocation()) != null) {
            errors.add(locationValidator(driver.getCurrentLocation()));
        }

        if (driver.getStatus() == null ||
                (driver.getStatus() != DriverStatus.AVAILABLE && driver.getStatus() != DriverStatus.UNAVAILABLE)) {
            errors.add("Driver status must be AVAILABLE or UNAVAILABLE");
        }
        return errors;
    }

    public List<String> getNearestAvailableDriversValidator(Location location) {
        List<String> errors = new ArrayList<>();

        if (locationXValidator(location) != null) {
            errors.add(locationXValidator(location));
        }

        if (locationYValidator(location) != null) {
            errors.add(locationYValidator(location));
        }

        return errors;
    }

    public List<String> findNearestAvailableDriversValidator(Location location) {
        List<String> errors = new ArrayList<>();

        if (locationXValidator(location) != null) {
            errors.add(locationXValidator(location));
        }

        if (locationYValidator(location) != null) {
            errors.add(locationYValidator(location));
        }

        return errors;
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
}
