package com.example.thanh.instagram.models;

public class User {

    // == constants ==
    String email, username, image;
    int id;

    // == constructor ==
    public User( int id, String email, String username, String image) {
        this.email = email;
        this.username = username;
        this.id = id;
        this.image = image;
    }

    // == setter and getters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
