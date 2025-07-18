package com.ecabs.Ecabs.ms.entities;

public record Location(Double currentLocationX, Double currentLocationY) {

    public double calculateEuclideanDistance(double targetX, double targetY) {
        double deltaX = targetX - this.currentLocationX;
        double deltaY = targetY - this.currentLocationY;
        return Math.hypot(deltaX, deltaY);
    }

    @Override
    public Double currentLocationX() {
        return currentLocationX;
    }

    @Override
    public Double currentLocationY() {
        return currentLocationY;
    }

}
