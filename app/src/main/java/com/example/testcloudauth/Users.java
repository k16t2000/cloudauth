package com.example.testcloudauth;

public class Users {
    private String name;
    private String position;
    private String email;
    private String imageurl;
    //for calendar
    private String EVENT="event";
    private String TIME="time";
    private String DATE="date";
    private String MONTH="month";
    private String YEAR="year";

    public Users(){
    }

    public Users(String name, String position, String email, String imageurl) {
        this.name = name;
        this.position = position;
        this.email=email;
        this.imageurl=imageurl;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
