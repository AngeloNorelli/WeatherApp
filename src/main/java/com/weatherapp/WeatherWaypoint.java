package com.weatherapp;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class WeatherWaypoint extends DefaultWaypoint {
    private String weatherInfo;

    public WeatherWaypoint(GeoPosition coordinates, String weatherInfo) {
        super(coordinates);
        this.weatherInfo = weatherInfo;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    @Override
    public String toString() {
        return weatherInfo;
    }
}
