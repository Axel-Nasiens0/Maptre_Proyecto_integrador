/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

/**
 * 
 * @author axelr
 */
public class Calculation {
    private String landAlias;
    private double[] lats;
    private double[] lons;

    public Calculation(String landAlias, double[] lats, double[] lons) {
        this.landAlias = (landAlias != null && !landAlias.trim().isEmpty()) ? landAlias : "Land N/A";
        this.lats = lats;
        this.lons = lons;
    }

    // Analytical algorithm for numerical integral calculation of area (Shoelace formula)
    public double calculateIntegralArea() {
        if (lats == null || lons == null || lats.length != lons.length || lats.length < 3) return 0.0;

        double integralSum = 0.0;
        int n = lats.length;

        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            integralSum += lats[i] * lons[next];
            integralSum -= lons[i] * lats[next];
        }

        // Metric scale conversion factor (1 degree ~ 111,320 meters)
        double scaleFactor = 111320.0 * 111320.0;
        return Math.abs(integralSum / 2.0) * scaleFactor; 
    }

    // Perimeter calculation using the metric scale factor
    public double calculatePerimeter() {
        if (lats == null || lons == null || lats.length != lons.length || lats.length < 2) return 0.0;

        double perimeterSum = 0.0;
        int n = lats.length;

        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            double dLat = lats[next] - lats[i];
            double dLon = lons[next] - lons[i];
            perimeterSum += Math.sqrt(dLat * dLat + dLon * dLon);
        }

        return perimeterSum * 111320.0;
    }

    public String getLandAlias() { 
        return landAlias; 
    }
}