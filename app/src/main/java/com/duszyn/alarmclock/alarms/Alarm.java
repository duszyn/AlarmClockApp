package com.duszyn.alarmclock.alarms;

import android.app.PendingIntent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Alarm {
    private String hour;
    private String[] days;
    private boolean enabled;
    private PendingIntent pendingIntent;
    private String ringtoneUri;
    private int uniqueID;
    private Set<Integer> uniqueIdSet = new HashSet<>();

    public Alarm(String hour, String[] days, boolean enabled, String alarmSoundUri, PendingIntent pendingIntent) {
        this.hour = hour;
        this.days = days;
        this.enabled = enabled;
        this.pendingIntent = pendingIntent;
        this.ringtoneUri = alarmSoundUri;
        this.uniqueID = generateUniqueId();
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
    public String getRingtoneUri() {
        return ringtoneUri;
    }
    public void setRingtoneUri(String ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

//     Generate a unique ID for this alarm
    public int generateUniqueId() {
        Random random = new Random();
        uniqueID = random.nextInt(1000000000);
        while(uniqueIdSet.contains(uniqueID)) {
            uniqueID = random.nextInt(1000000000);
        }
        uniqueIdSet.add(uniqueID);
        return uniqueID;
    }

    // Release the unique ID when the alarm is canceled
    public void releaseUniqueId() {
        uniqueIdSet.remove(uniqueID);
    }

    public Set<Integer> getUniqueIdSet() {
        return uniqueIdSet;
    }

    public int getUniqueID() {
        return uniqueID;
    }




}
