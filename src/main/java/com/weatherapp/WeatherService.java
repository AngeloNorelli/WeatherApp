package com.weatherapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WeatherService {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true";
    private static final String GEOCODING_URL = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";

    public void getWeather(String latitude, String longitude) throws Exception {
        String url = String.format(BASE_URL, latitude, longitude);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                parseWeatherData(result.toString());
            } else {
                System.out.println("Error fetching weather data. Please check your input.");
            }
        }
    }

    public void getWeather(String cityName) throws Exception {
        String url = String.format(GEOCODING_URL, cityName.replace(" ", "+"));

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                parseLocationData(result.toString());
            } else {
                System.out.println("Error fetching location data. Please check the city name.");
            }
        }
    }

    private void parseLocationData(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        if (root.isArray() && !root.isEmpty()) {
            JsonNode location = root.get(0);
            String latitude = location.path("lat").asText();
            String longitude = location.path("lon").asText();

            getWeather(latitude, longitude);
        } else {
            System.out.println("City not found. Please try another city.");
        }
    }

    private void parseWeatherData(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode currentWeather = root.path("current_weather");

        double temperature = currentWeather.path("temperature").asDouble();
        double windSpeed = currentWeather.path("windspeed").asDouble();
        String weatherDescription = currentWeather.path("weathercode").asText();

        System.out.printf("Current weather:\nTemperature: %.2f°C\nWind Speed: %.2f km/h\nWeather Code: %s\n",
                temperature, windSpeed, weatherDescription);
    }
}
