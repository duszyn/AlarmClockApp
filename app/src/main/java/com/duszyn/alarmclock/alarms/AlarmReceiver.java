package com.duszyn.alarmclock.alarms;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;



public class AlarmReceiver extends BroadcastReceiver implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
        private MediaPlayer mediaPlayer;

        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieve the selected ringtone URI from SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String selectedRingtoneUriString = preferences.getString("selectedRingtoneUri", null);

            // Check if the selected ringtone URI is not null
            if (selectedRingtoneUriString != null) {
                // Create a Uri object from the selected ringtone URI string
                Uri selectedRingtoneUri = Uri.parse(selectedRingtoneUriString);

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                } else {
                    mediaPlayer.reset();
                }

                try {
                    mediaPlayer.setOnErrorListener(this);
                    mediaPlayer.setOnCompletionListener(this);

                    mediaPlayer.setDataSource(context, selectedRingtoneUri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to play ringtone", Toast.LENGTH_SHORT).show();
                }
            } else {
                playDefaultAlarmSound(context);
            }

            Toast.makeText(context, "Alarm received", Toast.LENGTH_SHORT).show();
        }

    private void playDefaultAlarmSound(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // Error handling for ringtone playback

            mediaPlayer.reset();
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            // Handling for ringtone playback completion
            mediaPlayer.reset();
        }
    }
