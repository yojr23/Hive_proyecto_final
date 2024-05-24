package com.vicenterincon.hive_proyectofinal.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    private String id;
    private String name;
    private String login;
    private String email;
    private String password;
    private String address;
    private boolean is_active;
    private String role;
    private String career;
    @ServerTimestamp
    private Date birthdate;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    // Constructor
    public User(String id, String name, String login, String email, String password, String address, boolean is_active,
                String role, String career, Date birthdate) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.email = email;
        this.password = password;
        this.address = address;
        this.is_active = is_active;
        this.role = role;
        this.career = career;
        this.birthdate = birthdate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return is_active;
    }

    public void setActive(boolean is_active) {
        this.is_active = is_active;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }
}
