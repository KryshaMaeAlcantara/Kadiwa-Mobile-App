package com.example.appkadiwa.util;


public class Image {
    private String imageUrl;

    public Image() {
        // Required empty constructor for Firestore
    }

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

