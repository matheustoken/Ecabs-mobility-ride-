package com.ecabs.Ecabs.ms.dto.Request;

import com.ecabs.Ecabs.ms.entities.DriverStatus;
import com.ecabs.Ecabs.ms.entities.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


    public class RegisterDriverRequestDTO {


        private String name;


        private String car;


        private Location currentLocation;


        public RegisterDriverRequestDTO(String name,String car,Location currentLocation) {
            this.currentLocation = currentLocation;
            this.car = car;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Location getCurrentLocation() {
            return currentLocation;
        }

        public void setCurrentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
        }

        public String getCar() {
            return car;
        }

        public void setCar(String car) {
            this.car = car;
        }
    }

