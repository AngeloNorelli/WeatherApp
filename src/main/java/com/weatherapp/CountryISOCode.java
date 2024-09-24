package com.weatherapp;

import java.util.Locale;

public class CountryISOCode {
    public static String getISOCodeFromCountryName(String countryName) {
        String[] isoCountries = Locale.getISOCountries();

        for(String countryCode: isoCountries) {
            Locale locale = new Locale("", countryCode);
            if(locale.getDisplayCountry().equalsIgnoreCase(countryName)) {
                return countryCode;
            }
        }

        return null;
    }
}
