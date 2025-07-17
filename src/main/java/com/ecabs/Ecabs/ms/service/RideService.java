package com.ecabs.Ecabs.ms.service;

import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import org.springframework.stereotype.Service;

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

    public boolean completeRide(Long rideId){
        Ride ride = rides.get(rideId);
        if(ride == null|| ride.isCompleted()){
            return false;
        }
        ride.completeRide();
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        return true;
    }




}
