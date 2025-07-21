# 🚗 Backend Coding Task - Ride Matching Service

## Overview

This project is a Java-based ride matching service for a mobility platform. The service is designed to efficiently allocate available drivers to incoming ride requests using an in-memory data structure, with a strong focus on correctness and thread safety under concurrent load.

---

## 🧩 Features

### ✅ Driver Management
- Register new drivers with name, vehicle, and current location.
- Update driver availability status.
- Automatically mark driver as unavailable once matched with a ride.
- Make driver available again once a ride is completed.

### 🚕 Ride Matching
- Request a ride by providing a pickup location.
- Allocate the nearest available driver using straight-line (Euclidean) distance.
- Return matched driver and ride details to the rider.
- Complete ride and update driver availability.

### 📍 Driver Search
- Find the X nearest available drivers from a given location.
- Results returned in ascending order of distance.

---

## 🛠️ Technical Requirements

- **Language:** Java 17+
- **Framework:** Spring Boot
- **Persistence:** In-memory (no database)
- **Distance Calculation:** Straight-line Euclidean distance
- **Thread Safety:** Synchronized data structures and concurrent design
- **Exception Handling:** Centralized using `@ControllerAdvice` and custom exception classes
- **Tests:** Unit tests covering service and controller layers

---
## 🚀 How to Use the API

### 1. Register a Driver

- **Endpoint:** `POST /driver/register`
- **Request JSON:**

```json
{
  "name": "Driver1",
  "car": "Car1",
  "currentLocation": {
    "currentLocationX": 10.0,
    "currentLocationY": 20.0
  }
}
### Success Response (201 Created) - Register Driver

### 2. Update Driver Information

- **Endpoint:** `PUT /driver/{driverId}/update`
- **Request JSON:**

```json
{
  "currentLocation": {
    "currentLocationX": 10.0,
    "currentLocationY": 20.0
  },
  "status": "AVAILABLE"
}
### Success Response (200 OK) - Update Driver Information

### 3. Request a Ride

- **Endpoint:** `POST /ride/request`
- **Request JSON:**

```json
{
  "currentLocationX": 10,
  "currentLocationY": 400.0
}

### Success Response (201 Created) - Request Ride

## 4.Get Nearest Available Drivers

- **Endpoint:** `GET /driver/nearest`
- **Query Parameters:**
  - `locationX` (Double) - X coordinate of the pickup location (required)
  - `locationY` (Double) - Y coordinate of the pickup location (required)

- **Description:**  
  Returns a list of nearest available drivers sorted by their Euclidean distance to the given pickup location.

- **Success Response (200 OK):** -Nearest Drivers

## 🧪 Testing

Unit tests are implemented using JUnit and Mockito and cover:

- ✅ **DriverService**:
  - Registering a new driver
  - Updating driver availability
  - Updating location
- ✅ **RideService**:
  - Requesting a ride
  - Matching with nearest available driver
  - Completing a ride and releasing driver
- ✅ **DriverController** and **RideController**:
  - Validating endpoints behavior
  - Integration between controller and service logic
- ✅ **Exception Handling**:
  - Validation errors (400)
  - Not found exceptions (404)
  - No available driver scenarios


