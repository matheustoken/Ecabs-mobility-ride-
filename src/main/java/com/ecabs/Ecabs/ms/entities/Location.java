package com.ecabs.Ecabs.ms.entities;

public class Location {
    private final double currentLocationX;
    private final double currentLocationY;

    public Location(double currentLocationX, double currentLocationY) {
        this.currentLocationX = currentLocationX;
        this.currentLocationY = currentLocationY;
    }

    public double getCurrentLocationX() {
        return currentLocationX;
    }

    public double getCurrentLocationY() {
        return currentLocationY;
    }

    public double calculateEuclideanDistance(double targetX, double targetY){
        double deltaX = targetX-this.currentLocationX;
        double deltaY = targetY-this.currentLocationY;
        return Math.hypot(deltaX,deltaY);
    }


}
