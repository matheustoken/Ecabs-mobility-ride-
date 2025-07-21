package com.ecabs.Ecabs.ms.controller;

import com.ecabs.Ecabs.ms.dto.Request.UpdateDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.*;
import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.entities.Location;
import com.ecabs.Ecabs.ms.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDriverResponseWrapperDTO> registerDriver(@RequestBody RegisterDriverRequestDTO requestDTO) {
        RegisterDriverResponseDTO response = driverService.registerDriver(requestDTO);
        RegisterDriverResponseWrapperDTO wrapper = new RegisterDriverResponseWrapperDTO(
                "Driver successfully registered!",response
        );

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getDriverId())
                .toUri();

        return ResponseEntity.created(uri).body(wrapper);
    }

    @PutMapping("/{driverId}/update")
    public ResponseEntity<UpdateDriverResponseWrapperDTO> updateDriver(
            @Valid @PathVariable Long driverId,
            @RequestBody UpdateDriverRequestDTO request) {

        UpdateDriverResponseDTO response = driverService.updateDriver(driverId, request);
        UpdateDriverResponseWrapperDTO wrapper = new UpdateDriverResponseWrapperDTO(
                "Driver information updated successfully!",response
        );
        return ResponseEntity.ok(wrapper);
    }

    @GetMapping("/nearest")
    public ResponseEntity<List<DriverResponseDTO>> getNearestAvailableDrivers(
            @RequestParam Double locationX,
            @RequestParam Double locationY) {

        Location pickupLocation = new Location(locationX, locationY);
        List<DriverResponseDTO> nearestDrivers = driverService.getNearestAvailableDrivers(pickupLocation);

        return ResponseEntity.ok(nearestDrivers);
    }
}

