package com.duszyn.alarmclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlarmListAdapter.OnOptionsClickListener {

    SharedPreferences preferences;
    FloatingActionButton addButton;
    private List<Alarm> alarms;
    private AlarmListAdapter adapter;
    boolean alarmAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        backButton.setVisibility(View.GONE);
        TextView textViewAB = actionBar.getCustomView().findViewById(R.id.actionbar_title);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if (!preferences.getBoolean("welcome", false)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
            finish();
        }
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            preferences.edit().putBoolean("pop", true).apply();
            Intent intent = new Intent(this, AlarmCreationActivity.class);
            overridePendingTransition(0, 0);
            startActivity(intent);
        });

        // Initialize the alarms list
        alarms = new ArrayList<>();

        // Initialize adapter
        adapter = new AlarmListAdapter(this, alarms, this);
        // Set up the ListView
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // Update selected task count (for example)
        int selectedTaskCount = 5; // Example value
        adapter.setSelectedTaskCount(selectedTaskCount);

        alarmAdded = preferences.getBoolean("added", false);
        System.out.println(alarmAdded);

        if (alarmAdded) {
            // Add an alarm
            Intent intent = getIntent();
            ArrayList<String> receivedDaysList = intent.getStringArrayListExtra("days");
            Alarm newAlarm = new Alarm(intent.getStringExtra("hour"), receivedDaysList.toArray(new String[0]), true);
            preferences.edit().putBoolean("added", false).apply();
            alarms.add(newAlarm);
            adapter.notifyDataSetChanged();
        }

        List<Alarm> loadedAlarms = loadAlarmsFromPreferences();
        if (!loadedAlarms.isEmpty()) {
            alarms.addAll(loadedAlarms);
            adapter.notifyDataSetChanged();
        }

        saveAlarmsToPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onEditClick(int position) {
        // Open AlarmCreationActivity to edit the selected alarm
        Intent intent = new Intent(this, AlarmCreationActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        // Delete the selected alarm
        alarms.remove(position);
        adapter.notifyDataSetChanged();
        saveAlarmsToPreferences();
    }

    @Override
    public void onDuplicateClick(int position) {
        // Duplicate the selected alarm
        Alarm originalAlarm = alarms.get(position);
        Alarm duplicatedAlarm = new Alarm(originalAlarm.getTime(), originalAlarm.getDays(), originalAlarm.isActive());
        alarms.add(position + 1, duplicatedAlarm);
        adapter.notifyDataSetChanged();
        saveAlarmsToPreferences();
    }

    private List<Alarm> loadAlarmsFromPreferences() {
        List<Alarm> loadedAlarms = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); // Retrieve the shared preferences
        Gson gson = new Gson();
        String json = preferences.getString("alarms", null);
        if (json != null) {
            Type type = new TypeToken<List<Alarm>>() {}.getType();
            loadedAlarms = gson.fromJson(json, type);
            Log.d("DEBUG", "Loaded alarms: " + loadedAlarms.toString());
        }
        return loadedAlarms;
    }

    private void saveAlarmsToPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); // Retrieve the shared preferences
        Gson gson = new Gson();
        String json = gson.toJson(alarms);
        preferences.edit().putString("alarms", json).apply();
    }
}
