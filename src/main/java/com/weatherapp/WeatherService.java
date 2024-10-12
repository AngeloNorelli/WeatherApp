package com.weatherapp;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherService {
    private static final String OPEN_METEO_API_URL_TEMPLATE = "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true";

    public List<String> getWeatherForLocation(double latitide, double longitude) throws Exception {
        List<String> weatherInfoList = new ArrayList<>();

        String lat = String.format(Locale.US, "%.6f", latitide);
        String lon = String.format(Locale.US, "%.6f", longitude);
        String apiURL = String.format(OPEN_METEO_API_URL_TEMPLATE, lat, lon);

        String jsonResponse = sendHttpRequest(apiURL);
        if(jsonResponse != null) {
            String weatherInfo = getWeatherInfo(jsonResponse);
            weatherInfoList.add(weatherInfo);
        }
        return weatherInfoList;
    }

    private static String getWeatherInfo(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject currentWeather = jsonObject.getJSONObject("current_weather");

        String temperature = currentWeather.getBigDecimal("temperature").toString();
        String windspeed = currentWeather.getBigDecimal("windspeed").toString();
        String weatherCode = currentWeather.getBigDecimal("weathercode").toString();

        return String.format("Temperature: %s°C, Wind speed: %s km/h, Weather code: %s",
                temperature, windspeed, weatherCode);
    }

    public static String sendHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        StringBuilder result = new StringBuilder();

        if(connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } else {
            throw new Exception("Failed to fetch data from OpenMeteo API.");
        }
        connection.disconnect();
        return result.toString();
    }
}
