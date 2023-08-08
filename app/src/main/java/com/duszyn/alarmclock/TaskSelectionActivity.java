package com.duszyn.alarmclock;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TaskSelectionActivity extends AppCompatActivity {

    Button none, rewrite, steps, math, qr, barcode;

    FloatingActionButton done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection);

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
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        TextView textViewAB = actionBar.getCustomView().findViewById(R.id.actionbar_title);
        textViewAB.setText(R.string.taskSelection);

        none = findViewById(R.id.none);
        rewrite = findViewById(R.id.rewrite);
        rewrite.setOnClickListener(v -> {
            Intent intent = new Intent(this, RewriteSettings.class);
            startActivity(intent);
        });

        steps = findViewById(R.id.steps);
        steps.setOnClickListener(v -> {
            Intent intent = new Intent(this, StepsSettings.class);
            startActivity(intent);
        });

        math = findViewById(R.id.math);
        math.setOnClickListener(v -> {
            Intent intent = new Intent(this, MathSettings.class);
            startActivity(intent);
        });

        qr = findViewById(R.id.qr);
        qr.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRSettings.class);
            startActivity(intent);
        });

        barcode = findViewById(R.id.barcode);
        barcode.setOnClickListener(v -> {
            Intent intent = new Intent(this, BarcodeSettings.class);
            startActivity(intent);
        });

        done = findViewById(R.id.done);

        done.setOnClickListener(v -> {
            //!TODO tutaj będzie jeszcze przekazywanie tego jakie taski wybrał.
            finish();
        });
    }
}