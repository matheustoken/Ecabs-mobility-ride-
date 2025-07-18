package com.ecabs.Ecabs.ms.service;

import com.ecabs.Ecabs.ms.dto.Response.ResponseCompleteRideDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import com.ecabs.Ecabs.ms.service.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RideService {
    private final Map<Long, Ride> rides = new ConcurrentHashMap<>();
    private final DriverService driverService;
    private final AtomicLong rideIdCounter = new AtomicLong(1);

    public RideService(DriverService driverService) {
        this.driverService = driverService;
    }

    public Optional<Ride> requestRide(Location pickupLocation){
        List<String> errors = new ArrayList<>();

        if (pickupLocation == null) {
            errors.add("Location is required");
        } else {
            if (pickupLocation.currentLocationX() == null) {
                errors.add("Location X is required");
            }
            if (pickupLocation.currentLocationY() == null) {
                errors.add("Location Y is required");
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        List<Driver> nearestDriver = driverService.findNearestAvailableDrivers(pickupLocation);
        if(nearestDriver.isEmpty()){
            return Optional.empty();
        }
        Driver driver = nearestDriver.get(0);
        driver.setStatus(DriverStatus.UNAVAILABLE);
        long rideId = rideIdCounter.getAndIncrement();
        Ride ride = new Ride(rideId,driver,pickupLocation);
        rides.put(rideId,ride);
        return Optional.of(ride);
    }

    public ResponseCompleteRideDTO completeRide(Long rideId){
        Ride ride = rides.get(rideId);
        if(ride == null){
            return new ResponseCompleteRideDTO("Ride not found.", "N/A");
        }
        if (ride.isCompleted()) {
            return new ResponseCompleteRideDTO("Ride is already completed.",ride.getDriver().getStatus().name());
        }
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        return new ResponseCompleteRideDTO("Ride completed successfully.", driver.getStatus().name());
    }




}
