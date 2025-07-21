package com.ecabs.Ecabs.ms.controller;

import com.ecabs.Ecabs.ms.dto.Response.ResponseCompleteRideDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import com.ecabs.Ecabs.ms.service.DriverService;
import com.ecabs.Ecabs.ms.service.RideService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DriverService driverService;

    @MockitoBean
    private RideService rideService;

    @Test
    void shouldRequestRideSuccessfully() throws Exception {
        Location pickupLocation = new Location(10.0, 20.0);

        Driver driver = new Driver(
                123L, "Lazaro", "HONDA-FIT",
                new Location(15.0, 25.0));
        Ride ride = new Ride(123L, driver, pickupLocation);

        Mockito.when(rideService.requestRide(Mockito.any(Location.class)))
                .thenReturn(ride);

        String jsonRequest = objectMapper.writeValueAsString(pickupLocation);

        mockMvc.perform(MockMvcRequestBuilders.post("/ride/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Ride found successfully."))
                .andExpect(jsonPath("$.ride.rideId").value(123L))
                .andExpect(jsonPath("$.ride.driverName").value("Lazaro"))
                .andExpect(jsonPath("$.ride.carName").value("HONDA-FIT"))
                .andExpect(jsonPath("$.ride.driverLocation.currentLocationX").value(15.0))
                .andExpect(jsonPath("$.ride.driverLocation.currentLocationY").value(25.0));
    }

    @Test
    void shouldReturnBadRequestWhenLocationXisInvalid() throws Exception {
        String invalidJson = """
                {
                    "currentLocationX": "X",
                    "currentLocationY": 40.0
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/ride/request")
                        .content(invalidJson) // passa a string JSON direto
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid value for field 'location'"))
                .andExpect(jsonPath("$.error.details[0]").value("Location must be a numeric value"));
    }

    @Test
    void shouldReturnBadRequestWhenLocationYisInvalid() throws Exception {
        String invalidJson = """
                {
                    "currentLocationX": "10",
                    "currentLocationY": "Y"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/ride/request")
                        .content(invalidJson) // passa a string JSON direto
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid value for field 'location'"))
                .andExpect(jsonPath("$.error.details[0]").value("Location must be a numeric value"));
    }

    @Test
    void shouldCompleteRideSuccessfully() throws Exception {
        Long rideId = 123L;

        ResponseCompleteRideDTO responseDTO = new ResponseCompleteRideDTO("Ride completed successfully", "AVAILABLE");

        Mockito.when(rideService.completeRide(rideId)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/ride/" + rideId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.message").value("Ride completed successfully"));
    }

    @Test
    void shouldReturnBadRequestWhenRideIdIsNotValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/ride/null/complete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.message").value("Invalid input data"))
                .andExpect(jsonPath("$.error.details[0]").value("Invalid argument type"));
    }
}
