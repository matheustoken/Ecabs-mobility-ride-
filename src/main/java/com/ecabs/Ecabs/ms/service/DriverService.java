package com.ecabs.Ecabs.ms.service;


import com.ecabs.Ecabs.ms.dto.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.UpdateDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import org.springframework.stereotype.Service;

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

    public RegisterDriverResponseDTO registerDriver(String name, String car, Location location) {
        long driverId = driverIdCounter.getAndIncrement();
        Driver driver = new Driver(driverId,name,car,location);
        drivers.put(driver.getDriverId(), driver);
        return new RegisterDriverResponseDTO(
                driver.getDriverId(),
                driver.getName(),
                driver.getCar(),
                driver.getCurrentLocation(),
                driver.getStatus()
        );
    }
    public UpdateDriverResponseDTO updateDriver(Long driverId, Location location, DriverStatus status){
        Driver driver= drivers.get(driverId);
        if(driver == null){
            throw new IllegalArgumentException("Driver Not Found" + driverId);
        }
        driver.setCurrentLocation(location);
        driver.setStatus(status);

        return new UpdateDriverResponseDTO(
                driver.getName(),
                driver.getCar(),
                driver.getCurrentLocation(),
                driver.getStatus()
        );
    }

    public List<Driver> findNearestAvailableDrivers(Location pickupLocation) {
       return drivers.values().stream()
                .filter(driver->driver.getStatus()==DriverStatus.AVAILABLE)
                .sorted(Comparator.comparingDouble(d ->
                        d.getCurrentLocation().calculateEuclideanDistance(
                                pickupLocation.getCurrentLocationX(),
                                pickupLocation.getCurrentLocationY()
                        )
                )).collect(Collectors.toList());

    }



}
