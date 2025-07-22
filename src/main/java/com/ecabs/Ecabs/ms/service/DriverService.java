package com.ecabs.Ecabs.ms.service;

import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Request.UpdateDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.DriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.UpdateDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.service.exceptions.NotFoundException;
import com.ecabs.Ecabs.ms.service.exceptions.ValidationException;
import com.ecabs.Ecabs.ms.service.validators.DriveServiceValidators;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


@Service
public class DriverService {
    private final Map<Long, Driver> drivers = new ConcurrentHashMap<>();
    private final AtomicLong driverIdCounter = new AtomicLong(1);
    private final DriveServiceValidators validator = new DriveServiceValidators();

    public RegisterDriverResponseDTO registerDriver(RegisterDriverRequestDTO requestDriver) {
        throwIfHasErrors(validator.registerDriverValidator(requestDriver));

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
        if(validator.updateDriverIdValidator(driverId, drivers)){
            throw new NotFoundException("Driver Not Found");
        }

        throwIfHasErrors(validator.updateDriverValidator(updateDriver));

        Driver driver = drivers.get(driverId);
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
        throwIfHasErrors(validator.getNearestAvailableDriversValidator(pickupLocation));

        List<Driver> nearestDrivers = findNearestAvailableDrivers(pickupLocation);

        return nearestDrivers.stream()
                .map(driver -> {
                    double dist = driver.getCurrentLocation().calculateEuclideanDistance( pickupLocation.currentLocationX(), pickupLocation.currentLocationY());
                    return new DriverResponseDTO(driver.getName(), driver.getCar(), dist);
                })
                .collect(Collectors.toList());
    }

    public List<Driver> findNearestAvailableDrivers(Location pickupLocation) {
        throwIfHasErrors(validator.findNearestAvailableDriversValidator(pickupLocation));

        return drivers.values().stream()
                .filter(driver->driver.getStatus()==DriverStatus.AVAILABLE)
                .sorted(Comparator.comparingDouble(d ->
                        d.getCurrentLocation().calculateEuclideanDistance(
                                pickupLocation.currentLocationX(),
                                pickupLocation.currentLocationY()
                        )
                )).collect(Collectors.toList());
    }

    private void throwIfHasErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

    }
    public Map<Long, Driver> getDrivers() {
        return Collections.unmodifiableMap(drivers);
    }

}