package com.weatherapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class WeatherApp extends Application {

    private CountryCitiesService cityService = new CountryCitiesService();
    private ImageView mapView;

    public static void main(String[] args) {
        launch(args);
    }

    private void displayMapWithWeather(String countryName) throws Exception{
        List<CityWeatherInfo> cityWeatherInfoList = CountryCitiesService.getCityWeatherInfo(countryName);

        String mapImageUrl = cityService.getMapWithCities(cityWeatherInfoList);

        Image mapImage = new Image(mapImageUrl);
        mapView.setImage(mapImage);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        HBox inputSection = new HBox(10);
        Label countryLabel = new Label("Enter Country:");
        TextField countryTextField = new TextField();
        Button getWeatherButton = new Button("Check Weather");
        CheckBox darkModeCheckBox = new CheckBox("Dark Mode");

        inputSection.getChildren().addAll(countryLabel, countryTextField, getWeatherButton, darkModeCheckBox);

        VBox mapSection = new VBox(10);
        mapView = new ImageView();
        mapView.setFitWidth(700);
        mapView.setFitHeight(500);
        mapSection.getChildren().add(mapView);

        root.setTop(inputSection);
        root.setCenter(mapSection);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/default.css").toExternalForm());

        getWeatherButton.setOnAction(event -> {
            String countryName = countryTextField.getText();
            if (!countryName.isEmpty()) {
                try {
                    displayMapWithWeather(countryName);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        darkModeCheckBox.setOnAction(event -> {
            if(darkModeCheckBox.isSelected()) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("/darkmode.css").toExternalForm());
            } else {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(getClass().getResource("/default.css").toExternalForm());
                darkModeCheckBox.setText("Dark Mode");
            }
        });

        primaryStage.setTitle("Weather App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}