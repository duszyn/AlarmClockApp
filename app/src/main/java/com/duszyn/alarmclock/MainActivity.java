package com.duszyn.alarmclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AlarmRecyclerViewAdapter.OnOptionsClickListener {

    FloatingActionButton addButton;
    private List<Alarm> alarms;
    private AlarmRecyclerViewAdapter adapter;
    boolean alarmAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the system bar color to the system default
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.mainColor));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.custom_actionbar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
        }

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
        adapter = new AlarmRecyclerViewAdapter(alarms, this);
        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        alarmAdded = preferences.getBoolean("added", false);

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

        printAlarmsToLog(alarms);
    }

    private void printAlarmsToLog(List<Alarm> alarms) {
        for (int i = 0; i < alarms.size(); i++) {
            Log.d("DUPLICATE", "Alarm " + i + ": " + alarms.get(i).getHour() + ", Enabled: " + alarms.get(i).isEnabled());
        }
    }

    @Override
    public void onDuplicateClick(int position) {
        // Duplicate the selected alarm
        Alarm originalAlarm = alarms.get(position);
        Alarm duplicatedAlarm = new Alarm(originalAlarm.getHour(), originalAlarm.getDays(), originalAlarm.isEnabled());
        alarms.add(position + 1, duplicatedAlarm);
        saveAlarmsToPreferences();
        adapter.notifyDataSetChanged();
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
