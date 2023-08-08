package com.duszyn.alarmclock;

import java.util.List;

public class AlarmItem {
    private int hour;
    private int minute;
    private List<String> selectedDays;

    public AlarmItem(int hour, int minute, List<String> selectedDays) {
        this.hour = hour;
        this.minute = minute;
        this.selectedDays = selectedDays;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public List<String> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<String> selectedDays) {
        this.selectedDays = selectedDays;
    }
}

