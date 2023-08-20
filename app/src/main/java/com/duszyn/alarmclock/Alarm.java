package com.duszyn.alarmclock;

import android.app.PendingIntent;

public class Alarm {
    private String hour;
    private String[] days;
    private boolean enabled;
    private PendingIntent pendingIntent;
    private String time;

    public Alarm(String hour, String[] days, boolean enabled) {
        this.hour = hour;
        this.days = days;
        this.enabled = enabled;
        this.pendingIntent = null;
        this.time = time;
    }

    public String getHour() {
        return hour;
    }

    public String[] getDays() {
        return days;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }
}
