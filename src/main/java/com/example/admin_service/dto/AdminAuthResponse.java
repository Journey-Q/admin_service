
// dto/AdminAuthResponse.java
package com.example.admin_service.dto;

import com.example.admin_service.entity.Admin;

public class AdminAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Admin admin;

    // Constructor with all parameters
    public AdminAuthResponse(String accessToken, String refreshToken, Long expiresIn, Admin admin) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.admin = admin;
    }

    // Default constructor
    public AdminAuthResponse() {
    }

    // Getter methods
    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public Admin getAdmin() {
        return admin;
    }

    // Setter methods
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}