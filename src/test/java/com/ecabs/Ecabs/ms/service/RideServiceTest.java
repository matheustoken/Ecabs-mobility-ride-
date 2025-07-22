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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


import static org.junit.jupiter.api.Assertions.*;

class RideServiceTest {

    private RideService rideService;
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        driverService = new DriverService();
        ;
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
        assertEquals(1, ride.getRideId());
        assertEquals("Matheus", ride.getDriver().getName());
        assertEquals("HONDA-HRV", ride.getDriver().getCar());
        assertEquals(DriverStatus.UNAVAILABLE, ride.getDriver().getStatus());

    }

    @Test
    void shouldChangeDriverStatusIfRideAlreadyCompleted() {
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
        driver.setStatus(DriverStatus.AVAILABLE);

        ResponseCompleteRideDTO response = rideService.completeRide(ride.getRideId());

        assertEquals("Ride is already completed.", response.getMessage());
        assertEquals("AVAILABLE", response.getStatus());
        assertEquals(DriverStatus.AVAILABLE, driver.getStatus());
        assertTrue(ride.isCompleted());

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
    void shouldTestConcurrentDriverAllocation() throws InterruptedException {

        List<String> allocatedDrivers = Collections.synchronizedList(new ArrayList<>());

        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver1",
                        "Car1",
                        new Location(100.0, 100.0)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver2",
                        "Car2",
                        new Location(2.0, 2.0)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver3",
                        "Car3",
                        new Location(3.3, 3.3)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver4",
                        "Car4",
                        new Location(4.0, 4.0)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver5",
                        "Car5",
                        new Location(5.5, 5.5)
                )
        );

        Runnable allocate = () -> {
            while (true) {
                List<Driver> available = driverService.findNearestAvailableDrivers
                        (new Location(0.0, 0.0));
                if (available.isEmpty()) break;

                Driver driver = available.get(0);
                Long driverId = driver.getDriverId();

                if (driverId == null) return;

                synchronized (driverService.getDrivers().get(driverId)) {
                    Driver d = driverService.getDrivers().get(driverId);

                    if (d != null && d.getStatus() == DriverStatus.AVAILABLE) {
                        rideService.requestRide(new Location(0.0, 0.0));
                        allocatedDrivers.add(d.getName());
                        break;
                    }
                }
            }
        };

        Thread t1 = new Thread(allocate);
        Thread t2 = new Thread(allocate);
        Thread t3 = new Thread(allocate);
        Thread t4 = new Thread(allocate);
        Thread t5 = new Thread(allocate);
        Thread t6 = new Thread(allocate);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();

        assertEquals(5, allocatedDrivers.stream().distinct().count());

        long unavailableCount = driverService.getDrivers().values().stream()
                .filter(driver -> driver.getStatus() == DriverStatus.UNAVAILABLE)
                .count();

        assertEquals(5, unavailableCount);
    }

    @Test
    void  shouldAllocateAllDriversConcurrentlyWithThreads() throws InterruptedException, BrokenBarrierException {

        List<String> allocatedDrivers = Collections.synchronizedList(new ArrayList<>());

        driverService.registerDriver(new RegisterDriverRequestDTO("Driver1", "Car1", new Location(100.0, 100.0)));
        driverService.registerDriver(new RegisterDriverRequestDTO("Driver2", "Car2", new Location(2.0, 2.0)));
        driverService.registerDriver(new RegisterDriverRequestDTO("Driver3", "Car3", new Location(3.3, 3.3)));
        driverService.registerDriver(new RegisterDriverRequestDTO("Driver4", "Car4", new Location(4.0, 4.0)));
        driverService.registerDriver(new RegisterDriverRequestDTO("Driver5", "Car5", new Location(5.5, 5.5)));

        final CyclicBarrier gate = new CyclicBarrier(7); // 6 threads + main thread

        Runnable allocate = () -> {
            try {
                gate.await();

                while (true) {
                    List<Driver> available = driverService.findNearestAvailableDrivers(new Location(0.0, 0.0));
                    if (available.isEmpty()) break;

                    Driver driver = available.get(0);
                    Long driverId = driver.getDriverId();

                    if (driverId == null) return;

                    synchronized (driverService.getDrivers().get(driverId)) {
                        Driver d = driverService.getDrivers().get(driverId);

                        if (d != null && d.getStatus() == DriverStatus.AVAILABLE) {
                            rideService.requestRide(new Location(0.0, 0.0));
                            allocatedDrivers.add(d.getName());
                            break;
                        }
                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(allocate);
        Thread t2 = new Thread(allocate);
        Thread t3 = new Thread(allocate);
        Thread t4 = new Thread(allocate);
        Thread t5 = new Thread(allocate);
        Thread t6 = new Thread(allocate);

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();

        gate.await();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
        t6.join();

        assertEquals(5, allocatedDrivers.stream().distinct().count());

        long unavailableCount = driverService.getDrivers().values().stream()
                .filter(driver -> driver.getStatus() == DriverStatus.UNAVAILABLE)
                .count();

        assertEquals(5, unavailableCount);
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
    void shouldThrowNotFoundExceptionWhenRideNotFound() {

        Long invalidRideId = 999L;

        RideNotFoundException exception = assertThrows(RideNotFoundException.class, () -> {
            rideService.completeRide(invalidRideId);
        });

        assertTrue(exception.getMessage().contains("Ride Not Found"));
    }

}