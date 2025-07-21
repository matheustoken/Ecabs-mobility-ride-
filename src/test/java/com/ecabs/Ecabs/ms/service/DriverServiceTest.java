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
    void shouldThrowValidationExceptionWhenLocationIsNull() {
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
    void shouldThrowValidationExceptionWhenLocationXIsNull() {
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
    void shouldThrowValidationExceptionWhenLocationYIsNull() {
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
    void shouldThrowValidationExceptionWhenStatusIsNull() {
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
}

