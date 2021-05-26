package com.example.testcloudauth;

public class WorkingHoursList {
    private String userId, date, duration;

    public WorkingHoursList() {}

    public WorkingHoursList(String userId, String date, String duration) {
        this.userId = userId;
        this.date = date;
        this.duration = duration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
