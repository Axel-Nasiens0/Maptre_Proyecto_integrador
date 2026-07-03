/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapeo_proyecto_integrador;

public class calculo {
    private String aliasTerreno;
    private double[] lats;
    private double[] lons;

    public calculo(String aliasTerreno, double[] lats, double[] lons) {
        this.aliasTerreno = (aliasTerreno != null && !aliasTerreno.trim().isEmpty()) ? aliasTerreno : "Terreno S/N";
        this.lats = lats;
        this.lons = lons;
    }

    // Algoritmo analítico de cálculo integral numérico de área para figuras irregulares (Fórmula Gauss-Green)
    public double calcularAreaIntegral() {
        if (lats == null || lons == null || lats.length != lons.length || lats.length < 3) return 0.0;
        
        double sumaIntegral = 0.0;
        int n = lats.length;

        for (int i = 0; i < n; i++) {
            int sig = (i + 1) % n;
            sumaIntegral += lats[i] * lons[sig];
            sumaIntegral -= lons[i] * lats[sig];
        }

        // Factor de conversión escalar métrico (Aproximación estándar plana para coordenadas cartesianas/geográficas)
        double factorEscala = 111320.0 * 111320.0;
        return Math.abs(sumaIntegral / 2.0) * factorEscala * 0.00001;
    }

    public String getAliasTerreno() { 
        return aliasTerreno; 
    }
}