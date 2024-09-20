package com.weatherapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Scanner;

public class WeatherApp extends Application {

    private WeatherService weatherService = new WeatherService();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label cityLabel = new Label("Enter City:");
        TextField cityTextField = new TextField();
        Button getWeatherButton = new Button("Get Weather");
        Label weatherResultLabel = new Label();

        getWeatherButton.setOnAction(event -> {
            String cityName = cityTextField.getText();
            if (!cityName.isEmpty()) {
                try {
                    weatherService.getWeather(cityName, weatherResultLabel);
                } catch (Exception e) {
                    weatherResultLabel.setText("Error fetching weather: " + e.getMessage());
                }
            } else {
                weatherResultLabel.setText("Please enter a city name.");
            }
        });

        VBox vbox = new VBox(10, cityLabel, cityTextField, getWeatherButton, weatherResultLabel);
        vbox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Weather App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}