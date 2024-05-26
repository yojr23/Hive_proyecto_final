package com.vicenterincon.hive_proyectofinal.model;

public class UserSession {
    private String authToken;
    private String userId;

    public UserSession() {
        this.authToken = null;
        this.userId = null;
    }

    public UserSession(String authToken, String userId) {
        this.authToken = authToken;
        this.userId = userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
