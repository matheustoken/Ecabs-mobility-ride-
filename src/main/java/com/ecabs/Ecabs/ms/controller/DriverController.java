package com.ecabs.Ecabs.ms.controller;

import com.ecabs.Ecabs.ms.dto.RegisterDriverResponseDTO;
import com.ecabs.Ecabs.ms.dto.UpdateDriverResponseDTO;
import com.ecabs.Ecabs.ms.entities.Driver;
import com.ecabs.Ecabs.ms.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    DriverService driverService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDriverResponseDTO> registerDriver(@RequestBody Driver request) {
        RegisterDriverResponseDTO response = driverService.registerDriver(
                request.getName(),
                request.getCar(),
                request.getCurrentLocation()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{driverId}/update")
    public ResponseEntity<UpdateDriverResponseDTO>updateDriver(@PathVariable Long driverId, @RequestBody Driver request){
        UpdateDriverResponseDTO response = driverService.updateDriver(
                driverId,
                request.getCurrentLocation(),
                request.getStatus()
        );
        return ResponseEntity.ok(response);

    }



}
