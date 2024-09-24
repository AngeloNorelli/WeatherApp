package com.weatherapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class CountryCitiesService {
    private static final String GEO_DB_API_URL = "https://wft-geo-db.p.rapidapi.com/v1/geo/cities?countryIds=";
    private static final String FILTERS = "&sort=-population&limit=10";
    private static final String RAPIDAPI_HOST = "wft-geo-db.p.rapidapi.com";
    private static final String RAPIDAPI_KEY = "ce5288f8b1msh5031dc6423317d2p18535bjsnc10989195035";
    private static final String MAP_BOX_URL = "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/";
    private static final String MAPBOXAPI_TOKEN = "pk.eyJ1IjoiYW5nZWxvbm9yZWxsaSIsImEiOiJjbTFnOXM1ZzkwMWwzMmtzYTh5ZjVtcW5xIn0.rht4G2vuYbfkHpH0WiJGjQ";

    public static List<CityWeatherInfo> getCityWeatherInfo(String countryName) throws Exception {
        List<CityWeatherInfo> cityWeatherInfoList = new ArrayList<>();
        List<String[]> cities = getPopulousCities(countryName);

        for(String[] city: cities) {
            String cityName = city[0];
            double latitude = Double.parseDouble(city[1]);
            double longitude = Double.parseDouble(city[2]);

            String weatherInfo = WeatherService.getWeather(latitude, longitude);

            CityWeatherInfo cityWeather = new CityWeatherInfo(cityName, latitude, longitude, weatherInfo);
            cityWeatherInfoList.add(cityWeather);
        }

        return cityWeatherInfoList;
    }

    public static List<String[]> getPopulousCities(String countryName) {
        String countryCode = CountryISOCode.getISOCodeFromCountryName(countryName);
        List<String[]> cities = new ArrayList<>();

        try {
            String url = GEO_DB_API_URL + countryCode + FILTERS;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", RAPIDAPI_KEY)
                    .header("x-rapidapi-host", RAPIDAPI_HOST)
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());
                JsonNode data = root.get("data");

                if(data != null && data.isArray()) {
                    for(JsonNode city : data) {
                        String cityName = city.get("name").asText();
                        String latitude = city.get("latitude").asText();
                        String longitude = city.get("longitude").asText();
                        cities.add(new String[]{cityName, latitude, longitude});
                    }
                }
            } else {
                System.out.println("Error: " + response.body());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return cities;
    }

    public String getMapWithCities(List<CityWeatherInfo> cityWeatherInfoList) {
        StringJoiner pinMarkers = new StringJoiner(",");
        //StringBuilder geoJson = new StringBuilder();
        //geoJson.append("{\"type\":\"FeatureCollection\",\"features\":[");
        //StringJoiner citiesJson = new StringJoiner(",");

        for(CityWeatherInfo cityWeather: cityWeatherInfoList) {
            String lat = String.format(Locale.US, "%.8f", cityWeather.getLatitude());
            String lon = String.format(Locale.US, "%.8f", cityWeather.getLongitude());
            //citiesJson.add(cityWeather.getGeoJson());
            pinMarkers.add(String.format("pin-s+000(%s,%s)", lon, lat));
        }

        //geoJson.append(citiesJson.toString() + "]}");

        String mapUrl = String.format("%s%s/auto/700x500?access_token=%s",
                MAP_BOX_URL, pinMarkers.toString(), MAPBOXAPI_TOKEN);
        System.out.println(mapUrl);

        return mapUrl.toString();
    }
}
