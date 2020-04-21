package com.example.myapplication.API;

public class Post {

    private String id;
    private String serialnumber;
    private String model;
    private String latitude;
    private String longitude;

    public Post(String id, String serialnumber, String model, String latitude, String longitude) {
        this.id = id;
        this.serialnumber = serialnumber;
        this.model = model;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}