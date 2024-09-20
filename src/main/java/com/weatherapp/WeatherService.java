package com.weatherapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true";
    private static final String GEOCODING_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";

    public void getWeather(String latitude, String longitude, Label resultLabel) throws Exception {
        String url = String.format(BASE_URL, latitude, longitude);
        sendHttpRequest(url, resultLabel);
    }

    public void getWeather(String cityName, Label resultLabel) throws Exception {
        String url = String.format(GEOCODING_URL, cityName.replace(" ", "+"));
        sendHttpRequest(url, resultLabel);
    }

    private void sendHttpRequest(String urlString, Label resultLabel) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if(connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            if(urlString.contains("current_weather")) {
                parseWeatherData(result.toString(), resultLabel);
            } else {
                parseLocationData(result.toString(), resultLabel);
            }
        } else {
            Platform.runLater(() -> resultLabel.setText("Error fetching data. Please check your input."));
        }

        connection.disconnect();
    }

    private void parseLocationData(String json, Label resultLabel) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        if (root.isArray() && !root.isEmpty()) {
            JsonNode location = root.get(0);
            String latitude = location.path("lat").asText();
            String longitude = location.path("lon").asText();

            getWeather(latitude, longitude, resultLabel);
        } else {
            System.out.println("City not found. Please try another city.");
        }
    }

    private void parseWeatherData(String json, Label resultLabel) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode currentWeather = root.path("current_weather");

        double temperature = currentWeather.path("temperature").asDouble();
        double windSpeed = currentWeather.path("windspeed").asDouble();
        String weatherDescription = getWeatherDescription(currentWeather.path("weathercode").asInt());

        String weatherInfo = String.format("Current weather:\nTemperature: %.2f°C\nWind Speed: %.2f km/h\nWeather Description: %s\n",
                temperature, windSpeed, weatherDescription);

        Platform.runLater(() -> resultLabel.setText(weatherInfo));
    }

    private String getWeatherDescription(int weatherCode) {
        switch(weatherCode) {
            case 0: return "Clear sky";
            case 1: return "Mainly clear";
            case 2: return "Partly cloudy";
            case 3: return "Overcast";
            case 45: return "Fog";
            case 48: return "Depositing rime fog";
            case 51: return "Drizzle: Light";
            case 53: return "Drizzle: Moderate";
            case 55: return "Drizzle: Dense";
            case 56: return "Freezing Drizzle: Light";
            case 57: return "Freezing Drizzle: Dense";
            case 61: return "Rain: Slight";
            case 63: return "Rain: Moderate";
            case 65: return "Rain: Heavy";
            case 66: return "Freezing Rain: Light";
            case 67: return "Freezing Rain: Heavy";
            case 71: return "Snow fall: Slight";
            case 73: return "Snow fall: Moderate";
            case 75: return "Snow fall: Heavy";
            case 77: return "Snow grains";
            case 80: return "Rain showers: Slight";
            case 81: return "Rain showers: Moderate";
            case 82: return "Rain showers: Violent";
            case 85: return "Snow showers: Slight";
            case 86: return "Snow showers: Heavy";
            case 95: return "Thunderstorm: Slight or moderate";
            case 96: return "Thunderstorm with slight hail";
            case 99: return "Thunderstorm with heavy hail";
            default: return "Unknown weather condition";
        }
    }
}
