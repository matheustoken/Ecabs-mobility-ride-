package com.ecabs.Ecabs.ms.controller;

import com.ecabs.Ecabs.ms.dto.Response.ResponseCompleteRideDTO;
import com.ecabs.Ecabs.ms.dto.Response.RideResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.entities.Ride;
import com.ecabs.Ecabs.ms.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponseDTO> requestRide(@RequestBody Location pickupLocation) {
        Optional<Ride> rideRequest = rideService.requestRide(pickupLocation);
        Ride ride = rideRequest.get();
        Driver driver = ride.getDriver();

        RideResponseDTO response = new RideResponseDTO(
                ride.getRideId(),
                driver.getName(),
                driver.getCar(),
                driver.getCurrentLocation()
        );

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getRideId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<ResponseCompleteRideDTO> completeRide(@PathVariable Long rideId) {
        ResponseCompleteRideDTO response = rideService.completeRide(rideId);
        return ResponseEntity.ok(response);
    }
}


