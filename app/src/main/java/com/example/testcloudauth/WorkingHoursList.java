package com.example.testcloudauth;

public class WorkingHoursList {
    private String userId, date;
    private int duration;

    public WorkingHoursList() {}

    public WorkingHoursList(String userId, String date, int duration) {
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
