package com.duszyn.alarmclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import java.util.List;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {

    private int selectedTaskCount;
    private OnOptionsClickListener optionsClickListener;

    public interface OnOptionsClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onDuplicateClick(int position);
    }

    public AlarmListAdapter(Context context, List<Alarm> alarms, OnOptionsClickListener optionsClickListener) {
        super(context, 0, alarms);
        this.optionsClickListener = optionsClickListener;
    }

    public void setSelectedTaskCount(int count) {
        selectedTaskCount = count;
    }

    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final Alarm alarm = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_alarm, parent, false);
        }

        TextView textHour = convertView.findViewById(R.id.text_hour);
        TextView textDays = convertView.findViewById(R.id.text_days);
        TextView textCounter = convertView.findViewById(R.id.text_counter);
        SwitchCompat switchAlarm = convertView.findViewById(R.id.switch_alarm);
        ImageButton btnOptions = convertView.findViewById(R.id.btn_options);

        assert alarm != null;
        textHour.setText(alarm.getHour());
        textDays.setText(getFormattedDays(alarm.getDays()));
        textCounter.setText(selectedTaskCount + " tasks");
        switchAlarm.setChecked(alarm.isEnabled());

        switchAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setEnabled(isChecked);

            // Handle alarm enable/disable logic here
            if (isChecked) {
                // Enable the alarm
                // Your code to enable the alarm
            } else {
                // Disable the alarm
                // Your code to disable the alarm
            }
        });

        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
            }
        });

        return convertView;
    }

    private void showPopupMenu(View anchorView, final int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), anchorView);
        popupMenu.inflate(R.menu.dropdown);

        // Set a listener to handle menu item selection
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
}