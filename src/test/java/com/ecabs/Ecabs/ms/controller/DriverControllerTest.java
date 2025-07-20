package com.ecabs.Ecabs.ms.controller;

import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Request.UpdateDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.DriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.UpdateDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DriverService driverService;

    @Test
    void shouldRegisterDriverSuccessfully() throws Exception {
        RegisterDriverRequestDTO request = new RegisterDriverRequestDTO("Matheus", "HONDA-HRV",
                new Location(10.0, 20.0));
        RegisterDriverResponseDTO response = new RegisterDriverResponseDTO(
                1L,
                "Matheus",
                "HONDA-HRV",
                new Location(10.0, 20.0),
                DriverStatus.AVAILABLE
        );

        Mockito.when(driverService.registerDriver(Mockito.any(RegisterDriverRequestDTO.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/driver/register")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverId").value(1L))
                .andExpect(jsonPath("$.name").value("Matheus"))
                .andExpect(jsonPath("$.car").value("HONDA-HRV"))
                .andExpect(jsonPath("$.currentLocation.currentLocationX").value(10.0))
                .andExpect(jsonPath("$.currentLocation.currentLocationY").value(20.0))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturnBadRequestWhenLocationXisInvalid() throws Exception {
        String invalidJson = """
                {
                  "name": "Lazaro",
                  "car": "HONDA-FIT",
                  "currentLocation": {
                    "currentLocationX": "X",
                    "currentLocationY": 20
                  },
                  "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/driver/register")
                        .content(invalidJson) // passa a string JSON direto
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(
                        "Invalid value for field 'location'"))
                .andExpect(jsonPath("$.error.details[0]").value(
                        "Location must be a numeric value"));
    }

    @Test
    void shouldReturnBadRequestWhenLocationYisInvalid() throws Exception {
        String invalidJson = """
                {
                  "name": "Lazaro",
                  "car": "HONDA-FIT",
                  "currentLocation": {
                    "currentLocationX": "20",
                    "currentLocationY": "Y"
                  },
                  "status": "AVAILABLE"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/driver/register")
                        .content(invalidJson) // passa a string JSON direto
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value(
                        "Invalid value for field 'location'"))
                .andExpect(jsonPath("$.error.details[0]").value(
                        "Location must be a numeric value"));
    }

    @Test
    void shouldUpdateDriverSuccessfully() throws Exception {
        Long driverId = 1L;

        UpdateDriverRequestDTO request = new UpdateDriverRequestDTO(
                new Location(10.0, 20.0),
                DriverStatus.AVAILABLE);
        UpdateDriverResponseDTO response = new UpdateDriverResponseDTO(
                "Matheus", // já existente
                "HONDA-HRV", // já existente
                new Location(10.0, 20.0),
                DriverStatus.AVAILABLE
        );

        Mockito.when(driverService.updateDriver(
                Mockito.eq(driverId),
                Mockito.any(UpdateDriverRequestDTO.class)
        )).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.put("/driver/{driverId}/update", driverId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matheus"))
                .andExpect(jsonPath("$.car").value("HONDA-HRV"))
                .andExpect(jsonPath("$.currentLocation.currentLocationX").value(10.0))
                .andExpect(jsonPath("$.currentLocation.currentLocationY").value(20.0))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturnBadRequestWhenDriverIdIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/driver/{driverId}/update", "null")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateDriverRequestDTO(
                                new Location(10.0, 20.0),
                                DriverStatus.AVAILABLE
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid input data"))
                .andExpect(jsonPath("$.error.details[0]").value("Invalid argument type"));
    }

    @Test
    void shouldReturnNearestAvailableDrivers() throws Exception {
        double locationX = 10.0;
        double locationY = 20.0;

        List<DriverResponseDTO> drivers = List.of(
                new DriverResponseDTO("Lazaro", "HONDA-FIT", 0.0),
                new DriverResponseDTO("Matheus", "HONDA-HRV", 10.0)
        );

        Mockito.when(driverService.getNearestAvailableDrivers(Mockito.any(Location.class)))
                .thenReturn(drivers);

        mockMvc.perform(MockMvcRequestBuilders.get("/driver/nearest")
                        .param("locationX", String.valueOf(locationX))
                        .param("locationY", String.valueOf(locationY)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Lazaro"))
                .andExpect(jsonPath("$[0].car").value("HONDA-FIT"))
                .andExpect(jsonPath("$[0].distance").value(0.0))
                .andExpect(jsonPath("$[1].name").value("Matheus"))
                .andExpect(jsonPath("$[1].car").value("HONDA-HRV"))
                .andExpect(jsonPath("$[1].distance").value(10.0));
    }

    @Test
    void shouldReturnBadRequestWhenDistanceFromLocationXisInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/driver/nearest")
                        .param("locationX", "X")
                        .param("locationY", "20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid input data"))
                .andExpect(jsonPath("$.error.details[0]").value("Invalid argument type"));
    }

    @Test
    void shouldReturnBadRequestWhenDistanceFromLocationYisInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/driver/nearest")
                        .param("locationX", "20")
                        .param("locationY", "Y"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid input data"))
                .andExpect(jsonPath("$.error.details[0]").value("Invalid argument type"));
    }
}

