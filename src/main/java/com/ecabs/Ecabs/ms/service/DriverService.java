package com.ecabs.Ecabs.ms.service;


import com.ecabs.Ecabs.ms.dto.Request.UpdateDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.DriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.UpdateDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.service.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private final Map<Long, Driver> drivers = new ConcurrentHashMap<>();
    private final AtomicLong driverIdCounter = new AtomicLong(1);

    public RegisterDriverResponseDTO registerDriver(RegisterDriverRequestDTO requestDriver) {
        List<String> errors = new ArrayList<>();

        Location location = requestDriver.getCurrentLocation();
        if (location == null) {
            errors.add("Location is required");
        } else {
            if (location.currentLocationX() == null) {
                errors.add("Location X is required");
            }
            if (location.currentLocationY() == null) {
                errors.add("Location Y is required");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        long driverId = driverIdCounter.getAndIncrement();
        Driver driver = new Driver(
                driverId,
                requestDriver.getName(),
                requestDriver.getCar(),
                requestDriver.getCurrentLocation()
        );
        drivers.put(driver.getDriverId(), driver);

        return new RegisterDriverResponseDTO(
                driver.getDriverId(),
                driver.getName(),
                driver.getCar(),
                driver.getCurrentLocation(),
                driver.getStatus()
        );
    }
    public UpdateDriverResponseDTO updateDriver(Long driverId, UpdateDriverRequestDTO updateDriver){
        List<String> errors = new ArrayList<>();
        if (updateDriver.getStatus() == null ||
                !(updateDriver.getStatus() == DriverStatus.AVAILABLE ||
                        updateDriver.getStatus() == DriverStatus.UNAVAILABLE)) {

            errors.add("Driver status must be AVAILABLE or UNAVAILABLE");
        }

        Location location = updateDriver.getCurrentLocation();
        if (location == null) {
            errors.add("Location is required");
        } else {
            if (location.currentLocationX() == null) {
                errors.add("Location X is required");
            }
            if (location.currentLocationY() == null) {
                errors.add("Location Y is required");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Driver driver= drivers.get(driverId);
        driver.setStatus(updateDriver.getStatus());
        driver.setCurrentLocation(updateDriver.getCurrentLocation());

        return new UpdateDriverResponseDTO(
                driver.getName(),
                driver.getCar(),
                driver.getCurrentLocation(),
                driver.getStatus()
        );
    }

    public List<DriverResponseDTO> getNearestAvailableDrivers(Location pickupLocation) {

        List<String> errors = new ArrayList<>();

        if(pickupLocation.currentLocationX() ==null){
            errors.add("Location X is required");
        }
        if (pickupLocation.currentLocationY() == null) {
            errors.add("Location Y is required");
        }
        if (!errors.isEmpty()) {
        throw new ValidationException(errors);
    }
        List<Driver> nearestDrivers = findNearestAvailableDrivers(pickupLocation);

        return nearestDrivers.stream()
                .map(driver -> {
                    double dist = driver.getCurrentLocation().calculateEuclideanDistance( pickupLocation.currentLocationX(), pickupLocation.currentLocationY());
                    return new DriverResponseDTO(driver.getName(), driver.getCar(), dist);
                })
                .collect(Collectors.toList());
    }

    public List<Driver> findNearestAvailableDrivers(Location pickupLocation) {

        return drivers.values().stream()
                .filter(driver->driver.getStatus()==DriverStatus.AVAILABLE)
                .sorted(Comparator.comparingDouble(d ->
                        d.getCurrentLocation().calculateEuclideanDistance(
                                pickupLocation.currentLocationX(),
                                pickupLocation.currentLocationY()
                        )
                )).collect(Collectors.toList());
    }
}
