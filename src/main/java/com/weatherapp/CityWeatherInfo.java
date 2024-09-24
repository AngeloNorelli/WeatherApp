package com.weatherapp;

import org.json.JSONObject;

public class CityWeatherInfo {
    private String cityName;
    private double latitude;
    private double longitude;
    private JSONObject weatherInfo;

    public CityWeatherInfo(String cityName, double latitude, double longitude, String weatherInfo) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;

        JSONObject json = new JSONObject(weatherInfo);
        if(json.has("current_weather")) {
            System.out.println(weatherInfo);
            this.weatherInfo = json.getJSONObject("current_weather");
        }
    }

    public String getCityName() {
        return cityName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getWeatherInfo() {
        return weatherInfo.toString();
    }

    public double getTemperature() {
        return weatherInfo.getDouble("temperature");
    }

    public double getWindSpeed() {
        return weatherInfo.getDouble("windspeed");
    }

    public String getWeatherDescription() {
        return WeatherService.getWeatherDescription(weatherInfo.getInt("weathercode"));
    }

    public String getGeoJson() {
        return String.format("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[%s, %s]},\"properties\":{\"title\":\"%s\",\"temperature\":\"%s °C\",\"wind speed\":\"%s km/h\",\"weather description\":\"%s\"}}",
                longitude, latitude, cityName, getTemperature(), getWindSpeed(), getWeatherDescription());
    }
}
