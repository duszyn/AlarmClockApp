package com.duszyn.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AlarmCreationActivity extends AppCompatActivity {

    TextView displayedHour;
    int hour, minute;
    Button mon, tue, wed, thu, fri, sat, sun;
    MaterialButton tasks, settings, ringtoneSettings;
    FloatingActionButton done;
    private List<String> selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creation);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        if(preferences.getBoolean("pop", true)) {
            popTimePicker();
            preferences.edit().putBoolean("pop", false).apply();
        }

        // Set the system bar color to the system default
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mainColor));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_actionbar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        assert actionBar != null;
        ImageView backButton = actionBar.getCustomView().findViewById(R.id.actionbar_back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        selectedDays = new ArrayList<>();
        mon = findViewById(R.id.mon);
        tue = findViewById(R.id.tue);
        wed = findViewById(R.id.wed);
        thu = findViewById(R.id.thu);
        fri = findViewById(R.id.fri);
        sat = findViewById(R.id.sat);
        sun = findViewById(R.id.sun);

        mon.setOnClickListener(v -> toggleButton(mon, "Mon"));
        tue.setOnClickListener(v -> toggleButton(tue, "Tue"));
        wed.setOnClickListener(v -> toggleButton(wed, "Wed"));
        thu.setOnClickListener(v -> toggleButton(thu, "Thu"));
        fri.setOnClickListener(v -> toggleButton(fri, "Fri"));
        sat.setOnClickListener(v -> toggleButton(sat, "Sat"));
        sun.setOnClickListener(v -> toggleButton(sun, "Sun"));

        displayedHour = findViewById(R.id.hour);
        displayedHour.setOnClickListener(v -> {
            popTimePicker();
        });

        tasks = findViewById(R.id.tasks);
        tasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskSelectionActivity.class);
            startActivity(intent);
        });

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        ringtoneSettings = findViewById(R.id.ringtoneSettings);
        ringtoneSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, RingtoneSelectionActivity.class);
            startActivity(intent);
        });

        /*defining done button, removing previously selected ringtone (to set deafult ringtone as deafult again
        finishing the activity, setting alarm*/
        done = findViewById(R.id.done);
        done.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            String time = (String) displayedHour.getText();
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            setAlarm(hour, minute);
            editor.remove("selectedRingtoneTitle");
            preferences.edit().putBoolean("added", true).apply();
            editor.apply();
            printOrder(selectedDays);
            Intent intent = new Intent(AlarmCreationActivity.this, MainActivity.class);
            intent.putStringArrayListExtra("days", new ArrayList<>(selectedDays)); // Convert the list to an ArrayList
            intent.putExtra("hour", time);
            startActivity(intent);

            finish();
        });
    }

    private void toggleButton(Button button, String day) {
        boolean isSelected = selectedDays.contains(day);
        if (isSelected) {
            selectedDays.remove(day);
            button.setBackgroundResource(R.drawable.unselected_button);
        } else {
            selectedDays.add(day);
            button.setBackgroundResource(R.drawable.selected_button);
        }
    }

    private void printOrder(List<String> selectedDays) {
        StringBuilder sb = new StringBuilder();
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            if (selectedDays.contains(day)) {
                String.valueOf(sb.append(day.substring(0, 3)).append(" ")); // Display the first three letters of the day
            }
        }
    }

    private void setAlarm(int hour, int minute) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar currentCalendar = Calendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        // Adjust the alarm time if it's before the current time
        if (hour < currentHour || (hour == currentHour && minute <= currentMinute)) {
            // Add 24 hours to the alarm time
            hour += 24;
        }

        // Set the alarm time
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);
        alarmCalendar.set(Calendar.SECOND, 0);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);

        // Retrieve the selected ringtone URI from SharedPreferences
        String selectedRingtoneUriString = preferences.getString("selectedRingtoneUri", null);

        // Check if the selected ringtone URI is not null
        if (selectedRingtoneUriString != null) {
            Uri selectedRingtoneUri = Uri.parse(selectedRingtoneUriString);
            alarmIntent.putExtra("selectedRingtoneUri", selectedRingtoneUri.toString());
        }

        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Set the alarm using AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            } else {
                // Handle case when exact alarms cannot be scheduled
                Toast.makeText(this, "Cannot schedule exact alarms on this device", Toast.LENGTH_SHORT).show();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        }

        // Save the selected ringtone URI in SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("welcome", true);
        editor.putBoolean("added", true);
        editor.apply();

        Toast.makeText(this, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
    }



    private void popTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                displayedHour.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);

        timePickerDialog.show();
    }
}
