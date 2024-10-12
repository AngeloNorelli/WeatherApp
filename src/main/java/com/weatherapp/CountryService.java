package com.weatherapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CountryService {
    private static final String COUNTRY_API_URL = "https://restcountries.com/v3.1/name/";

    public static String getISOCode(String countryName) throws Exception {
        String urlString = COUNTRY_API_URL + countryName;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONArray countryArray = new JSONArray(response.toString());
        if (countryArray.length() > 0) {
            JSONObject countryData = countryArray.getJSONObject(0);
            return countryData.getString("cca2");
        } else {
            throw new Exception("Country not found.");
        }
    }
}
