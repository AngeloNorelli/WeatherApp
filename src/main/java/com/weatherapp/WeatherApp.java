package com.weatherapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        Button switchModeButton = new Button("Dark Mode");
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

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(cityLabel, 0, 0);
        gridPane.add(cityTextField, 1, 0);
        gridPane.add(switchModeButton, 2, 1);
        gridPane.add(getWeatherButton, 1, 1);
        gridPane.add(weatherResultLabel, 0, 2, 2, 1);



        Scene scene = new Scene(gridPane, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/default.css").toExternalForm());

        switchModeButton.setOnAction(event -> {
            if(switchModeButton.getText().equals("Dark Mode")) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("/darkmode.css").toExternalForm());
                switchModeButton.setText("Light Mode");
            } else {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("/default.css").toExternalForm());
                switchModeButton.setText("Dark Mode");
            }
        });

        primaryStage.setTitle("Weather App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}