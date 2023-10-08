package com.duszyn.alarmclock.settings;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.duszyn.alarmclock.R;

public class StepsSettings extends AppCompatActivity {

    SeekBar lvlSelector;
    TextView lvlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_settings);

        // Set the system bar color to the system default
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mainColor));
        }

        lvlSelector =  findViewById(R.id.lvlSelector);
        lvlText = findViewById(R.id.lvlSelectionText);

        lvlSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update TextView based on the selected level
                if(progress == 0) {
                    lvlText.setText(R.string.easy_30_steps);
                } else if(progress == 1) {
                    lvlText.setText(R.string.medium_70_steps);
                } else {
                    lvlText.setText(R.string.hard_100_steps);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not used in this example
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not used in this example
            }
        });

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
        textViewAB.setText(R.string.stepsSettings);
    }
}