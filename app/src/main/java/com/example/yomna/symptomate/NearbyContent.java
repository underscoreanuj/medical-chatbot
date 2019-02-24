package com.example.yomna.symptomate;

public class NearbyContent {

    private String Symptoms;
    private String Latitude;
    private String Longitude;

    public NearbyContent() {
    }

    public NearbyContent(String symptoms, String latitude, String longitude) {
        Symptoms = symptoms;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getSymptoms() {
        return Symptoms;
    }

    public void setSymptoms(String symptoms) {
        Symptoms = symptoms;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
