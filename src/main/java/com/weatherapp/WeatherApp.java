package com.weatherapp;

import java.util.Scanner;

public class WeatherApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WeatherService weatherService = new WeatherService();

        System.out.println("Welcome to the WeatherApp!");
        System.out.print("Enter a city name: ");
        String cityName = scanner.nextLine();

        try {
            if (!cityName.isEmpty()) {
                weatherService.getWeather(cityName);
            } else {
                System.out.print("Please enter a city name!");
            }
        } catch (Exception e) {
            System.out.println("Error fetching weather data: " + e.getMessage());
        }
    }
}