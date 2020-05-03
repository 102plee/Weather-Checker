package com.example.testt;

import java.util.List;

class weatherHourlyTemplate {
    static class hourly {
        String summary;
        String icon;
        List<data> Data;
    }
    static class data {
        double time;
        String summary;
        String icon;
        double precipIntensity;
        double precipProbability;
        String precipType;
        double temperature;
        double apparentTemperature;
        double dewPoint;
        double humidity;
        double pressure;
        double windSpeed;
        double windGust;
        double windBearing;
        double cloudCover;
        int uvIndex;
        double visibility;
        double ozone;
    }
}
