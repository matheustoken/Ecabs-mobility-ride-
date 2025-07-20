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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    private DriverService driverService;

    @BeforeEach
    void setUp() {
        driverService = new DriverService();
    }

    @Test
    void shouldRegisterDriverSuccessfully() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus",
                "HONDA-HRV",
                new Location(10.0, 20.0)
        );

        RegisterDriverResponseDTO response = driverService.registerDriver(request);

        assertNotNull(response);
        assertEquals("Matheus", response.getName());
        assertEquals("HONDA-HRV", response.getCar());
        assertEquals(10.0, response.getCurrentLocation().currentLocationX());
        assertEquals(20.0, response.getCurrentLocation().currentLocationY());
        assertEquals(DriverStatus.AVAILABLE, response.getStatus());

    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsMissing() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("",
                "HONDA-HRV",
                new Location(10.0, 20.0)
        );
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.registerDriver(request);
        });
        assertTrue(exception.getErrors().contains("Driver name is required"));

    }

    @Test
    void shouldThrowValidationExceptionWhenCarIsMissing() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus",
                "",
                new Location(10.0, 20.0)
        );
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.registerDriver(request);
        });
        assertTrue(exception.getErrors().contains("Car is required"));

    }

    @Test
    void shouldThrowValidationExceptionWhenLocationIsMissing() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus",
                "HONDA-HRV",
                null
        );
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.registerDriver(request);
        });
        assertTrue(exception.getErrors().contains("Location is required"));

    }

    @Test
    void shouldThrowValidationExceptionWhenLocationXIsMissing() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus",
                "HONDA-HRV", new Location(null, 10.0)

        );
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.registerDriver(request);
        });
        assertTrue(exception.getErrors().contains("Location X is required"));

    }

    @Test
    void shouldThrowValidationExceptionWhenLocationYIsMissing() {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus",
                "HONDA-HRV", new Location(20.0, null)

        );
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.registerDriver(request);
        });
        assertTrue(exception.getErrors().contains("Location Y is required"));

    }

    @Test
    void shouldUpdateDriverSuccessfully() {
        RegisterDriverRequestDTO registerRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "HONDA-HRV",
                new Location(5.0, 5.0)
        );
        RegisterDriverResponseDTO registerResponse = driverService.registerDriver(registerRequest);
        Long driverId = registerResponse.getDriverId();


        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(new Location(10.0, 20.0), DriverStatus.UNAVAILABLE);

        UpdateDriverResponseDTO updateResponse = driverService.updateDriver(driverId, updateRequest);


        assertNotNull(updateResponse);
        assertEquals("Matheus", updateResponse.getName());
        assertEquals("HONDA-HRV", updateResponse.getCar());
        assertEquals(10.0, updateResponse.getCurrentLocation().currentLocationX());
        assertEquals(20.0, updateResponse.getCurrentLocation().currentLocationY());
        assertEquals(DriverStatus.UNAVAILABLE, updateResponse.getStatus());

    }

    @Test
    void shouldThrowNotFoundExceptionWhenDriverIdDoesNotExist() {

        Long nonExistentDriverId = 999L;
        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(
                new Location(10.0, 10.0),
                DriverStatus.AVAILABLE
        );

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            driverService.updateDriver(nonExistentDriverId, updateRequest);
        });
        assertTrue(exception.getErrors().contains("Driver Not Found"));

    }

    @Test
    void shouldThrowWhenLocationIsNull() {
        RegisterDriverRequestDTO registerRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "HONDA-HRV",
                new Location(5.0, 5.0)
        );
        RegisterDriverResponseDTO registerResponse = driverService.registerDriver(registerRequest);
        Long driverId = registerResponse.getDriverId();


        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(null, DriverStatus.UNAVAILABLE);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.updateDriver(driverId, updateRequest);
        });
        assertTrue(exception.getErrors().contains("Location is required"));

    }

    @Test
    void shouldThrowWhenLocationXIsNull() {
        RegisterDriverRequestDTO registerRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "HONDA-HRV",
                new Location(5.0, 5.0)
        );
        RegisterDriverResponseDTO registerResponse = driverService.registerDriver(registerRequest);
        Long driverId = registerResponse.getDriverId();


        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(
                new Location(null,
                        10.0),
                DriverStatus.UNAVAILABLE);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.updateDriver(driverId, updateRequest);
        });
        assertTrue(exception.getErrors().contains("Location X is required"));

    }

    @Test
    void shouldThrowWhenLocationYIsNull() {
        RegisterDriverRequestDTO registerRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "HONDA-HRV",
                new Location(5.0, 5.0)
        );
        RegisterDriverResponseDTO registerResponse = driverService.registerDriver(registerRequest);
        Long driverId = registerResponse.getDriverId();


        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(new Location(10.0, null), DriverStatus.UNAVAILABLE);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.updateDriver(driverId, updateRequest);
        });
        assertTrue(exception.getErrors().contains("Location Y is required"));

    }

    @Test
    void shouldThrowWhenStatusIsNull() {
        RegisterDriverRequestDTO registerRequest = new RegisterDriverRequestDTO(
                "Matheus",
                "HONDA-HRV",
                new Location(5.0, 5.0)
        );
        RegisterDriverResponseDTO registerResponse = driverService.registerDriver(registerRequest);
        Long driverId = registerResponse.getDriverId();


        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(new Location(10.0, 10.0), null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            driverService.updateDriver(driverId, updateRequest);
        });
        assertTrue(exception.getErrors().contains("Driver status must be AVAILABLE or UNAVAILABLE"));

    }

    @Test
    void shouldReturnNearestAvailableDriversOrderedByDistance() {
        driverService.registerDriver
                (new RegisterDriverRequestDTO("Matheus", "HONDA-HRV",
                        new Location(1.0, 2.0)));
        driverService.registerDriver
                (new RegisterDriverRequestDTO("Julia", "HONDA-FIT",
                        new Location(10.0, 20.0)));
        driverService.registerDriver
                (new RegisterDriverRequestDTO("Elizabeth", "HONDA-CIVIC",
                        new Location(100.0, 200.0)));

        Location pickupLocation = new Location(0.0, 0.0);

        List<Driver> nearestDrivers = driverService.findNearestAvailableDrivers(pickupLocation);

        assertEquals(3, nearestDrivers.size());
        assertEquals("Matheus", nearestDrivers.get(0).getName());      // Mais perto
        assertEquals("Julia", nearestDrivers.get(1).getName());        // Intermediário
        assertEquals("Elizabeth", nearestDrivers.get(2).getName());

    }

    @Test
    void shouldReturnOnlyAvailableDriversOrderedByDistance() {
        RegisterDriverResponseDTO d1 = driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Matheus", "HONDA-HRV", new Location(1.0, 2.0))
        );
        RegisterDriverResponseDTO d2 = driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Julia", "HONDA-FIT", new Location(10.0, 20.0))
        );
        RegisterDriverResponseDTO d3 = driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Elizabeth", "HONDA-CIVIC", new Location(100.0, 200.0))
        );

        UpdateDriverRequestDTO updateRequest = new UpdateDriverRequestDTO(new Location(10.0, 20.0), DriverStatus.UNAVAILABLE);
        driverService.updateDriver(d2.getDriverId(), updateRequest);

        Location pickupLocation = new Location(0.0, 0.0);

        List<Driver> nearestDrivers = driverService.findNearestAvailableDrivers(pickupLocation);

        assertEquals(2, nearestDrivers.size());
        assertEquals("Matheus", nearestDrivers.get(0).getName());
        assertEquals("Elizabeth", nearestDrivers.get(1).getName());

    }

    @Test
    void shouldReturnNearestAvailableDriversWithDistances() {
        driverService.registerDriver(new RegisterDriverRequestDTO
                ("Matheus", "HRV", new Location(1.0, 2.0)));
        driverService.registerDriver(new RegisterDriverRequestDTO
                ("Julia", "FIT", new Location(10.0, 20.0)));
        driverService.registerDriver(new RegisterDriverRequestDTO
                ("Elizabeth", "Civic", new Location(100.0, 200.0)));

        Location pickup = new Location(0.0, 0.0);

        List<DriverResponseDTO> result = driverService.getNearestAvailableDrivers(pickup);

        assertEquals(3, result.size());

        assertEquals("Matheus", result.get(0).getName());
        assertEquals("Julia", result.get(1).getName());
        assertEquals("Elizabeth", result.get(2).getName());

        assertTrue(result.get(0).getDistance() < result.get(1).getDistance());
        assertTrue(result.get(1).getDistance() < result.get(2).getDistance());
    }

    @Test
    void shouldReturnEmptyListWhenNoDriversAvailable() {
        Location pickup = new Location(0.0, 0.0);
        List<Driver> result = driverService.findNearestAvailableDrivers(pickup);

        assertTrue(result.isEmpty());
    }

    @Test
    void testConcurrentDriverAllocation() throws InterruptedException {

        List<String> allocatedDrivers = Collections.synchronizedList(new ArrayList<>());

        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver1",
                        "Car1",
                        new Location(1.0, 1.0)
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
                        new Location(3.0, 3.0)
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
                        new Location(5.0, 5.0)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver6",
                        "Car6",
                        new Location(5.1, 5.1)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver7",
                        "Car7",
                        new Location(6.1, 6.1)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver8",
                        "Car8",
                        new Location(8.0, 8.0)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver9",
                        "Car9",
                        new Location(9.0, 9.0)
                )
        );
        driverService.registerDriver(
                new RegisterDriverRequestDTO(
                        "Driver10",
                        "Car10",
                        new Location(10.0, 10.0)
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
                        try {
                            driverService.updateDriver(d.getDriverId(),
                                    new UpdateDriverRequestDTO(d.getCurrentLocation(),
                                            DriverStatus.UNAVAILABLE));
                            allocatedDrivers.add(d.getName());
                        } catch (Exception e) {
                        }
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
        Thread t7 = new Thread(allocate);
        Thread t8 = new Thread(allocate);
        Thread t9 = new Thread(allocate);
        Thread t10 = new Thread(allocate);
        Thread t11 = new Thread(allocate);
        Thread t12 = new Thread(allocate);

        t1.start();t2.start();t3.start();t4.start();
        t1.join();t2.join();t3.join();t4.join();

        t5.start();t6.start();t7.start();t8.start();
        t5.join();t6.join();t7.join();t8.join();

        t9.start();t10.start();t11.start();t12.start();
        t9.join();t10.join();t11.join();t12.join();

        assertEquals(10, allocatedDrivers.stream().distinct().count());

        long unavailableCount = driverService.getDrivers().values().stream()
                .filter(driver -> driver.getStatus() == DriverStatus.UNAVAILABLE)
                .count();

        assertEquals(10, unavailableCount);
    }
}

