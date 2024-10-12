package com.weatherapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;

public class GeoService {
    private static final String GEO_DB_API_URL_TEMPLATE = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?countryIds=%s&limit=1&sort=-population";
    private static final String RAPID_API_KEY = "ce5288f8b1msh5031dc6423317d2p18535bjsnc10989195035";
    private static final String RAPID_API_HOST = "wft-geo-db.p.rapidapi.com";

    public GeoPosition getCountryCoordinates(String countryCode) throws Exception {
        String apiURL = String.format(GEO_DB_API_URL_TEMPLATE, countryCode);


        String jsonResponse = sendHttpRequest(apiURL);
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray cityArray = jsonObject.getJSONArray("data");
        JSONObject city = cityArray.getJSONObject(0);

        double latitude = city.getDouble("latitude");
        double longitude = city.getDouble("longitude");

        return new GeoPosition(latitude, longitude);
    }

    private static String sendHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("x-rapidapi-key", RAPID_API_KEY);
        connection.setRequestProperty("x-rapidapi-host", RAPID_API_HOST);

        StringBuilder result = new StringBuilder();
        if(connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } else {
            throw new Exception("Failed to fetch data from GeoDB API");
        }
        connection.disconnect();
        return result.toString();
    }
}
