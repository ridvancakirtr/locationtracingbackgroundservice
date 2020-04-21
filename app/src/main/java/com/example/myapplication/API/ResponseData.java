package com.example.myapplication.API;

public class ResponseData {
    private String status;
    private String message;

    public ResponseData(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
