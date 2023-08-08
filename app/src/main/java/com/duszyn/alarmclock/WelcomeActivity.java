package com.duszyn.alarmclock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class WelcomeActivity extends AppCompatActivity {
    Button button;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

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

        //TODO zrobić tutaj tytuł apki
        textViewAB.setText("ALARM CLOCK APP");


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        button = findViewById(R.id.nextActivity);
        button.setOnClickListener(v -> {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean("welcome",true);
            preferencesEditor.putBoolean("pop",true);
            preferencesEditor.apply();
            Intent intent = new Intent(this, AlarmCreationActivity.class);
            overridePendingTransition(0,0);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
