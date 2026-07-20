/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapeo_proyecto_integrador;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GestorClima {

    public static class ResultadoClima {
        public double temperatura;
        public double humedad;

        public ResultadoClima(double temperatura, double humedad) {
            this.temperatura = temperatura;
            this.humedad = humedad;
        }
    }

    public static ResultadoClima obtenerClimaActual(double latitud, double longitud) throws Exception {
        String urlString = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m", 
            latitud, longitud
        );
        
        URL url = new URL(urlString);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("GET");
        
        if (conexion.getResponseCode() != 200) {
            throw new RuntimeException("HTTP " + conexion.getResponseCode());
        }
        
        BufferedReader lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
        StringBuilder respuestaJSON = new StringBuilder();
        String linea;
        while ((linea = lector.readLine()) != null) {
            respuestaJSON.append(linea);
        }
        lector.close();
        conexion.disconnect();
        
        JSONObject jsonObject = new JSONObject(respuestaJSON.toString());
        JSONObject bloqueCurrent = jsonObject.getJSONObject("current");
        
        double temp = bloqueCurrent.getDouble("temperature_2m");
        double hum = bloqueCurrent.getDouble("relative_humidity_2m");
        
        return new ResultadoClima(temp, hum);
    }
}