package com.example.yomna.symptomate;

public class HistoryContent {
    private String Name;
    private String Date;
    private String Symptoms;
    private String Diagnosis;
    private String Latitude;
    private String Longitude;

    public HistoryContent() {
    }

    public HistoryContent(String name, String date, String symptoms, String diagnosis, String latitude, String longitude) {
        Name = name;
        Date = date;
        Symptoms = symptoms;
        Diagnosis = diagnosis;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getSymptoms() {
        return Symptoms;
    }

    public void setSymptoms(String symptoms) {
        Symptoms = symptoms;
    }

    public String getDiagnosis() {
        return Diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        Diagnosis = diagnosis;
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
