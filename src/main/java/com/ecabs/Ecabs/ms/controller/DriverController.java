package com.ecabs.Ecabs.ms.controller;

import com.ecabs.Ecabs.ms.dto.Request.UpdateDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.DriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Response.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.Request.RegisterDriverRequestDTO;
import com.ecabs.Ecabs.ms.dto.Response.UpdateDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
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
    DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDriverResponseDTO> registerDriver( @Valid @RequestBody RegisterDriverRequestDTO requestDTO) {
        RegisterDriverResponseDTO response = driverService.registerDriver(requestDTO
        );
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest() // pega o caminho atual: /register
                .path("/{id}")
                .buildAndExpand(response.getDriverId()) // adiciona o id do driver no path
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @PutMapping("/{driverId}/update")
    public ResponseEntity<UpdateDriverResponseDTO>updateDriver(@Valid @PathVariable Long driverId, @RequestBody UpdateDriverRequestDTO request){
        UpdateDriverResponseDTO response = driverService.updateDriver(
                driverId,
                request);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/nearest")
    public ResponseEntity<List<DriverResponseDTO>> getNearestAvailableDrivers(
            @RequestParam Double locationX,
            @RequestParam Double locationY) {

        Location pickupLocation = new Location(locationX,locationY);
        List<DriverResponseDTO> nearestDrivers = driverService.getNearestAvailableDrivers(pickupLocation);

        return ResponseEntity.ok(nearestDrivers);
    }





}
