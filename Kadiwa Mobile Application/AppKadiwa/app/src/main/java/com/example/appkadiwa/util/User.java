package com.example.appkadiwa.util;

public class User {
    private String id;
    private String name;
    private String email;
    private String profileImageUrl;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String userId, String name, String email, String profileImageUrl) {
        this.id = userId;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
