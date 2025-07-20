package com.ecabs.Ecabs.ms.service;

import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.ResponseCompleteRideDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import com.ecabs.Ecabs.ms.service.exceptions.DriversNotFoundException;
import com.ecabs.Ecabs.ms.service.exceptions.RideNotFoundException;
import com.ecabs.Ecabs.ms.service.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RideServiceTest {

    private RideService rideService;
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        driverService = new DriverService();;
        rideService = new RideService(driverService);
    }
    @Test
    void shouldRequestRideSuccessfully() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus",
                "HONDA-HRV",
                new Location(10.0, 20.0)
        );

        RegisterDriverResponseDTO response = driverService.registerDriver(request);

        Location pickup = new Location(30.0, 10.0);

        Ride ride = rideService.requestRide(pickup);
        assertEquals(pickup, ride.getPickupLocation());
        assertEquals(1,ride.getRideId());
        assertEquals("Matheus", ride.getDriver().getName());
        assertEquals("HONDA-HRV", ride.getDriver().getCar());
        assertEquals(DriverStatus.UNAVAILABLE, ride.getDriver().getStatus());

    }

    @Test
    void shouldThrowWhenLocationXIsNull() {
        Location pickup = new Location(null, 10.0);

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            rideService.requestRide(pickup);
        });

        assertTrue(ex.getErrors().contains("Location X is required"));
    }

    @Test
    void shouldThrowWhenLocationYIsNull() {
        Location pickup = new Location(10.0, null);

        ValidationException ex = assertThrows(ValidationException.class, () -> {
            rideService.requestRide(pickup);
        });

        assertTrue(ex.getErrors().contains("Location Y is required"));
    }
    @Test
    void shouldThrowDriversNotFoundExceptionWhenNoDriversAvailable() {
        Location pickup = new Location(30.0, 10.0);

        DriversNotFoundException ex = assertThrows(DriversNotFoundException.class, () -> {
            rideService.requestRide(pickup);
        });

        assertTrue(ex.getErrors().contains("No drivers available at the moment"));
    }
    @Test
    void shouldReturnAlreadyCompletedMessageIfRideAlreadyCompleted() {

        RegisterDriverRequestDTO driverRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "Honda-HRV",
                new Location(5.0, 5.0)
        );
        driverService.registerDriver(driverRequest);

        Location pickupLocation = new Location(5.1, 5.2);

        Ride ride = rideService.requestRide(pickupLocation);
        Driver driver = ride.getDriver();

        assertFalse(ride.isCompleted());
        assertEquals(DriverStatus.UNAVAILABLE, driver.getStatus());

        ResponseCompleteRideDTO response = rideService.completeRide(ride.getRideId());

        assertEquals("Ride completed successfully.", response.getMessage());
        assertEquals("AVAILABLE", response.getStatus());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
    }
    @Test
    void shouldNotChangeDriverStatusIfRideAlreadyCompleted() {
        RegisterDriverRequestDTO driverRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "Honda-HRV",
                new Location(5.0, 5.0)
        );
        driverService.registerDriver(driverRequest);

        Location pickupLocation = new Location(5.1, 5.2);

        Ride ride = rideService.requestRide(pickupLocation);
        Driver driver = ride.getDriver();

        ride.setCompleted(true);
        driver.setStatus(DriverStatus.UNAVAILABLE);

        ResponseCompleteRideDTO response = rideService.completeRide(ride.getRideId());

        assertEquals("Ride is already completed.", response.getMessage());
        assertEquals("UNAVAILABLE", response.getStatus());
        assertEquals(DriverStatus.UNAVAILABLE, driver.getStatus());
        assertTrue(ride.isCompleted());

    }



    @Test
    void shouldThrowNotFoundExceptionWhenRideNotFound() {

        Long invalidRideId = 999L;

        RideNotFoundException exception = assertThrows(RideNotFoundException.class, () -> {
            rideService.completeRide(invalidRideId);
        });

        assertTrue(exception.getMessage().contains("Ride Not Found"));
    }
    @Test
    void testConcurrentDriverAllocation2() throws InterruptedException {
        int numberOfDrivers = 10;
        List<String> allocatedDrivers = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger rideCounter = new AtomicInteger(0);

        // Register 10 drivers
        for (int i = 1; i <= numberOfDrivers; i++) {
            driverService.registerDriver(
                    new RegisterDriverRequestDTO(
                            "Driver" + i,
                            "Car" + i,
                            new Location(0.0, 0.0)
                    )
            );
        }

        Runnable allocate = () -> {
            while (true) {
                if (rideCounter.get() >= numberOfDrivers) break;

                List<Driver> available = driverService.findNearestAvailableDrivers(new Location(0.0, 0.0));

                if (available.isEmpty()) break;

                Driver driver = available.get(0);
                Long driverId = driver.getDriverId();

                synchronized (driverService.getDrivers().get(driverId)) {
                    Driver d = driverService.getDrivers().get(driverId);

                    if (d != null && d.getStatus() == DriverStatus.AVAILABLE) {
                        try {
                            rideService.requestRide(new Location(0.0, 0.0));
                            allocatedDrivers.add(d.getName());
                            rideCounter.incrementAndGet();
                        } catch (Exception e) {
                            // log if needed
                        }
                        break;
                    }
                }
            }
        };

        // Start 12 threads
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Thread t = new Thread(allocate);
            threads.add(t);
            t.start();
        }

        // Wait for all to complete
        for (Thread t : threads) {
            t.join();
        }

        // Validations
        assertEquals(numberOfDrivers, allocatedDrivers.stream().distinct().count());

        long unavailableCount = driverService.getDrivers().values().stream()
                .filter(driver -> driver.getStatus() == DriverStatus.UNAVAILABLE)
                .count();

        assertEquals(numberOfDrivers, unavailableCount);
    }



}