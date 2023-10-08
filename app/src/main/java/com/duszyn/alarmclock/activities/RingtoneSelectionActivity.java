package com.duszyn.alarmclock.activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duszyn.alarmclock.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RingtoneSelectionActivity extends AppCompatActivity {

    private RingtoneAdapter ringtoneAdapter;
    private RingtoneItem selectedRingtone;
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringtone_selection);

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
        TextView textViewAB = actionBar.getCustomView().findViewById(R.id.actionbar_title);
        textViewAB.setText(R.string.ringtoneSelection);

        RecyclerView ringtoneRecyclerView = findViewById(R.id.ringtoneRecyclerView);
        Button setAlarmButton = findViewById(R.id.setAlarmButton);

        preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        setAlarmButton.setOnClickListener(v -> {
            if (selectedRingtone != null) {
                String selectedRingtoneTitle = selectedRingtone.getRingtoneTitle();

                // Save the selected ringtone in SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selectedRingtoneTitle", selectedRingtoneTitle);
                editor.putString("selectedRingtoneUri", selectedRingtone.getRingtoneUri().toString());
                editor.apply();

                finish();
            } else {
                Toast.makeText(this, "Please select a ringtone", Toast.LENGTH_SHORT).show();
            }
        });

        ringtoneRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ringtoneAdapter = new RingtoneAdapter();
        ringtoneRecyclerView.setAdapter(ringtoneAdapter);

        populateRingtoneOptions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve the saved selected ringtone from SharedPreferences
        String selectedRingtoneTitle = preferences.getString("selectedRingtoneTitle", "Default Ringtone");

        // Find the selected ringtone in the list
        for (RingtoneItem ringtoneItem : ringtoneAdapter.getRingtoneList()) {
            if (ringtoneItem.getRingtoneTitle().equals(selectedRingtoneTitle)) {
                selectedRingtone = ringtoneItem;
                break;
            }
        }

        ringtoneAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private void populateRingtoneOptions() {
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = ringtoneManager.getCursor();

        List<RingtoneItem> ringtoneList = new ArrayList<>();

        String defaultRingtoneTitle = "Default Ringtone";
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtoneList.add(new RingtoneItem(defaultRingtoneTitle, defaultRingtoneUri));

        while (cursor.moveToNext()) {
            int currentPosition = cursor.getPosition();
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            Uri uri = ringtoneManager.getRingtoneUri(currentPosition);
            ringtoneList.add(new RingtoneItem(title, uri));
        }

        ringtoneAdapter.setRingtoneList(ringtoneList);
        ringtoneAdapter.notifyDataSetChanged();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private static class RingtoneItem {
        private final String ringtoneTitle;
        private final Uri ringtoneUri;

        public RingtoneItem(String ringtoneTitle, Uri ringtoneUri) {
            this.ringtoneTitle = ringtoneTitle;
            this.ringtoneUri = ringtoneUri;
        }

        public String getRingtoneTitle() {
            return ringtoneTitle;
        }

        public Uri getRingtoneUri() {
            return ringtoneUri;
        }
    }

    private class RingtoneAdapter extends RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder> {

        private List<RingtoneItem> ringtoneList;

        public void setRingtoneList(List<RingtoneItem> ringtoneList) {
            this.ringtoneList = ringtoneList;
            selectedRingtone = null;
        }

        public List<RingtoneItem> getRingtoneList() {
            return ringtoneList;
        }

        @NonNull
        @Override
        public RingtoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ringtone_item, parent, false);
            return new RingtoneViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RingtoneViewHolder holder, int position) {
            RingtoneItem ringtoneItem = ringtoneList.get(position);

            holder.ringtoneTitleTextView.setText(ringtoneItem.getRingtoneTitle());

            if (selectedRingtone != null && selectedRingtone == ringtoneItem) {
                holder.emptyRingtoneIndicator.setImageResource(R.drawable.selected_ringtone_background);
            } else {
                holder.emptyRingtoneIndicator.setImageResource(R.drawable.empty_circle);
            }

            holder.itemView.setOnClickListener(v -> {
                selectedRingtone = ringtoneItem;
                notifyDataSetChanged();

                releaseMediaPlayer();

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getApplicationContext(), selectedRingtone.getRingtoneUri());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(RingtoneSelectionActivity.this, "Failed to play ringtone", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return ringtoneList.size();
        }

        public class RingtoneViewHolder extends RecyclerView.ViewHolder {
            TextView ringtoneTitleTextView;
            ImageView emptyRingtoneIndicator;

            public RingtoneViewHolder(@NonNull View itemView) {
                super(itemView);
                ringtoneTitleTextView = itemView.findViewById(R.id.ringtoneTitleTextView);
                emptyRingtoneIndicator = itemView.findViewById(R.id.emptyRingtoneIndicator);
            }
        }
    }
}

