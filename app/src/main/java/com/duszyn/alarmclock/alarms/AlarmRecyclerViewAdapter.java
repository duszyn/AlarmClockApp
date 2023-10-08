package com.duszyn.alarmclock.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.duszyn.alarmclock.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    private final List<Alarm> alarms;
    private final OnOptionsClickListener optionsClickListener;
    private final Context context;
    private final SharedPreferences preferences;

    public interface OnOptionsClickListener {
        void onEditClick(int position);

        void onDeleteClick(int position);

        void onDuplicateClick(int position);
    }

    public AlarmRecyclerViewAdapter(List<Alarm> alarms, OnOptionsClickListener optionsClickListener, Context context, SharedPreferences preferences) {
        this.preferences = preferences;
        this.context = context;
        this.alarms = alarms;
        this.optionsClickListener = optionsClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textHour;
        TextView textDays;
        TextView textCounter;
        SwitchCompat switchAlarm;
        ImageButton btnOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textHour = itemView.findViewById(R.id.text_hour);
            textDays = itemView.findViewById(R.id.text_days);
            textCounter = itemView.findViewById(R.id.text_counter);
            switchAlarm = itemView.findViewById(R.id.switch_alarm);
            btnOptions = itemView.findViewById(R.id.btn_options);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarms.get(position);

        holder.textHour.setText(alarm.getHour());
        holder.textDays.setText(getFormattedDays(alarm.getDays()));
        holder.textCounter.setText("tasks");
        holder.switchAlarm.setChecked(alarm.isEnabled());
        Log.d("SwitchState", "Position: " + position + ", Enabled: " + alarm.isEnabled());

        holder.btnOptions.setOnClickListener(v -> showPopupMenu(v, position));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_alarm, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        viewHolder.switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Alarm alarm = alarms.get(position);
                alarm.setEnabled(isChecked);
                saveAlarmsToPreferences();
                if (isChecked) {
                    // Call the setAlarm method here
                    onAlarmSet(position);
                    Log.e("Zsetowany", "zsetowany ponownie " + alarm.getPendingIntent());
                } else {
                    // Call the cancelAlarm method here
                    System.out.println("to jest pendingintent przed cancelem " +  alarm.getPendingIntent());
                    cancelAlarm(position);
                }
            }
        });


        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return alarms.size();
    }

    private void showPopupMenu(View anchorView, final int position) {
        PopupMenu popupMenu = new PopupMenu(anchorView.getContext(), anchorView);
        popupMenu.inflate(R.menu.dropdown);

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    if (optionsClickListener != null) {
                        optionsClickListener.onEditClick(position);
                    }
                    return true;
                case R.id.menu_delete:
                    if (optionsClickListener != null) {
                        optionsClickListener.onDeleteClick(position);
                    }
                    return true;
                case R.id.menu_duplicate:
                    if (optionsClickListener != null) {
                        optionsClickListener.onDuplicateClick(position);
                    }
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private String getFormattedDays(String[] days) {
        StringBuilder formattedDays = new StringBuilder();
        for (String day : days) {
            formattedDays.append(day).append(", ");
        }
        // Remove the trailing comma and space
        if (formattedDays.length() > 2) {
            formattedDays.delete(formattedDays.length() - 2, formattedDays.length());
        }
        return formattedDays.toString();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public void onAlarmSet(int position) {
        Alarm alarm = alarms.get(position);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar currentCalendar = Calendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        String[] alarmHourInParts = alarm.getHour().split(":");
        int alarmHour = Integer.parseInt(alarmHourInParts[0]);
        int alarmMinute = Integer.parseInt(alarmHourInParts[1]);

        // Adjust the alarm time if it's before the current time
        if (alarmHour < currentHour || (alarmHour == currentHour && alarmMinute <= currentMinute)) {
            // Add 24 hours to the alarm time
            alarmHour += 24;
        }

        // Set the alarm time
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, alarmHour);
        alarmCalendar.set(Calendar.MINUTE, alarmMinute);
        alarmCalendar.set(Calendar.SECOND, 0);

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        // Check if the selected ringtone URI is not null in the alarm object
        if (alarm.getRingtoneUri() != null) {
            alarmIntent.putExtra("selectedRingtoneUri", alarm.getRingtoneUri());
        }

        // Create a unique request code for each alarm
        //TODO implement uniqueId logic (imo the best would be to save the list of uniqueid's in sharedpreferences and just check if its there, if true - reroll

        PendingIntent pendingIntent = alarm.getPendingIntent();


        // Set the alarm using AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            } else {
                // Handle case when exact alarms cannot be scheduled
                Toast.makeText(context, "Cannot schedule exact alarms on this device", Toast.LENGTH_SHORT).show();
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        }
        alarm.setPendingIntent(pendingIntent);

        // Display a success message
        Toast.makeText(context, "Alarm set successfully!", Toast.LENGTH_SHORT).show();
    }

public void cancelAlarm(int position) {
    Alarm alarm = alarms.get(position);

    PendingIntent pendingIntent = alarm.getPendingIntent();
    if (pendingIntent != null) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(context, "Alarm cancelled successfully!", Toast.LENGTH_SHORT).show();
//        alarm.releaseUniqueId(); // Release the unique ID
//        alarm.setPendingIntent(null); // Clear the PendingIntent reference
    }
}

    private void saveAlarmsToPreferences() {
        Gson gson = new Gson();
        String json = gson.toJson(alarms);
        preferences.edit().putString("alarms", json).apply();
    }

    private List<Alarm> loadAlarmsFromPreferences() {
        List<Alarm> loadedAlarms = new ArrayList<>();
        Gson gson = new Gson();
        String json = preferences.getString("alarms", null);
        if (json != null) {
            Type type = new TypeToken<List<Alarm>>() {}.getType();
            loadedAlarms = gson.fromJson(json, type);
            Log.d("DEBUG", "Loaded alarms: " + loadedAlarms.toString());
        }
        return loadedAlarms;
    }

}


