// dto/ApiError.java
package com.example.admin_service.dto;

public class ApiError {
    private String message;
    private int status;
    private long timestamp;

    // Constructors
    public ApiError(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public ApiError(String message, int status, long timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public ApiError(int value, String badRequest, String message, String requestURI) {
        this.status = value;
        this.message = badRequest + ": " + message + " at " + requestURI;
        this.timestamp = System.currentTimeMillis();
    }

    // Getter methods
    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setter methods
    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}