/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapping_integrating_project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherManager {

    public static class WeatherResult {
        public double temperature;
        public double humidity;

        public WeatherResult(double temperature, double humidity) {
            this.temperature = temperature;
            this.humidity = humidity;
        }
    }

    public static WeatherResult getCurrentWeather(double latitude, double longitude) throws Exception {
        String urlString = String.format(
            "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m", 
            latitude, longitude
        );
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("HTTP " + connection.getResponseCode());
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder jsonResponse = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonResponse.append(line);
        }
        reader.close();
        connection.disconnect();
        
        JSONObject jsonObject = new JSONObject(jsonResponse.toString());
        JSONObject currentBlock = jsonObject.getJSONObject("current");
        
        double temp = currentBlock.getDouble("temperature_2m");
        double hum = currentBlock.getDouble("relative_humidity_2m");
        
        return new WeatherResult(temp, hum);
    }
}