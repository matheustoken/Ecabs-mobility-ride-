package com.ecabs.Ecabs.ms.service;

import com.ecabs.Ecabs.ms.dto.Response.ResponseCompleteRideDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import com.ecabs.Ecabs.ms.service.exceptions.ValidationException;
import com.ecabs.Ecabs.ms.service.validators.RideServiceValidators;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RideService {

    private final Map<Long, Ride> rides = new ConcurrentHashMap<>();
    private final DriverService driverService;
    private final AtomicLong rideIdCounter = new AtomicLong(1);
    private final RideServiceValidators validator = new RideServiceValidators();

    public RideService(DriverService driverService) {
        this.driverService = driverService;
    }

    public Ride requestRide(Location location) {
        throwIfHasErrors(validator.requestRideValidator(location));

        List<Driver> nearestDriver = driverService.findNearestAvailableDrivers(location);
        validator.validateNearestDriver(nearestDriver);

        Driver driver = nearestDriver.get(0);
        driver.setStatus(DriverStatus.UNAVAILABLE);

        Long rideId = rideIdCounter.getAndIncrement();
        Ride ride = new Ride(rideId, driver, location);
        rides.put(rideId, ride);

        return ride;
    }

    public ResponseCompleteRideDTO completeRide(Long rideId) {
        Ride ride = rides.get(rideId);

        validator.validateRideExists(ride);

        if (ride.isCompleted()) {
            return new ResponseCompleteRideDTO(
                    "Ride is already completed.",
                    ride.getDriver().getStatus().name()
            );
        }

        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.AVAILABLE);
        ride.setCompleted(true);

        return new ResponseCompleteRideDTO(
                "Ride completed successfully.",
                driver.getStatus().name()
        );
    }

    private void throwIfHasErrors(List<String> errors) {
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

}
