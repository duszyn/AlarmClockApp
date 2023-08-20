package com.duszyn.alarmclock;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.ViewHolder> {

    private List<Alarm> alarms;
    private OnOptionsClickListener optionsClickListener;

    public interface OnOptionsClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
        void onDuplicateClick(int position);
    }

    public AlarmRecyclerViewAdapter(List<Alarm> alarms, OnOptionsClickListener optionsClickListener) {
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
        holder.textCounter.setText("tasks"); // You can set the actual task count here
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
                Log.d("SwitchState", "Position: " + position + ", Enabled: " + alarm.isEnabled());
                // Handle alarm enable/disable logic here
                if (isChecked) {
                    // Enable the alarm
                    // Your code to enable the alarm
                } else {
                    // Disable the alarm
                    // Your code to disable the alarm
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
}
