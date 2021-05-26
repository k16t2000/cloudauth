package com.example.testcloudauth;

public class Users {
    private String name, position, email, imageurl;

    public Users() {}

    public Users(String name, String position, String email, String imageurl) {
        this.name = name;
        this.position = position;
        this.email = email;
        this.imageurl = imageurl;
    }

    public String getName() { return name; }

    public String getPosition() { return position; }

    public String getEmail() { return email; }

    public String getImageurl() { return imageurl; }

    public void setName(String name) { this.name = name; }

    public void setPosition(String position) { this.position = position; }

    public void setEmail(String email) { this.email = email; }

    public void setImageurl(String imageurl) { this.imageurl = imageurl; }
}
