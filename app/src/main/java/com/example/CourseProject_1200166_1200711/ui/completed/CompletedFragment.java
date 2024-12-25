package com.example.CourseProject_1200166_1200711.ui.completed;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.CourseProject_1200166_1200711.DataBaseHelper;
import com.example.CourseProject_1200166_1200711.R;
import com.example.CourseProject_1200166_1200711.SharedPrefManager;

public class CompletedFragment extends Fragment {

    private ViewGroup container;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String currentDay = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        container = view.findViewById(R.id.completedLayout);

        dataBaseHelper = new DataBaseHelper(requireContext(), "DB_PROJECT", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        fillCompletedTasks();
    }

    private void addDayHeader(String day) {
        TextView dayHeader = new TextView(getActivity());
        dayHeader.setText(day);
        dayHeader.setTextSize(20);

        int padding = 20;  // Padding in pixels
        dayHeader.setPadding(padding, padding, padding, padding);

        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.parseColor("#6E92C8"));
        roundedBackground.setCornerRadius(32);

        dayHeader.setBackground(roundedBackground);
        dayHeader.setTextColor(Color.BLACK);

        if (container != null) {
            container.addView(dayHeader);
        }
    }

    private void addTaskCard(String taskTitle, String taskDescription, String dueTime,
                             int priorityLevel, int completionStat, String reminderDate, String reminderTime) {

        View cardView = getLayoutInflater().inflate(R.layout.rounded, container, false);
        LinearLayout taskContainer = cardView.findViewById(R.id.taskContainer);

        int padding = 24;  // Padding for task layout
        taskContainer.setPadding(padding, padding, padding, padding);

        String[] texts = new String[]{
                taskTitle,
                taskDescription,
                dueTime,
                priorityLevel == 1 ? "Medium" : priorityLevel == 2 ? "High" : "Low",
                completionStat == 1 ? "Completed" : "Not Completed",
                reminderDate,
                reminderTime
        };

        for (String text : texts) {
            TextView textView = new TextView(getActivity());
            textView.setText(text);
            textView.setTextSize(16);
            textView.setPadding(0, 8, 0, 8);
            taskContainer.addView(textView);
        }

        if (container != null) {
            container.addView(cardView);
        }
    }

    private void fillCompletedTasks() {
        String email = sharedPrefManager.readString("email", "No Val");
        Cursor cursor = dataBaseHelper.getCompletedTasks(email);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve all relevant task details from the database
                String taskTitle = cursor.getString(cursor.getColumnIndexOrThrow("TaskTitle"));
                String taskDescription = cursor.getString(cursor.getColumnIndexOrThrow("TaskDescription"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("DueTime"));
                String dueTime = cursor.getString(cursor.getColumnIndexOrThrow("DueDate"));
                int priorityLevel = cursor.getInt(cursor.getColumnIndexOrThrow("PriorityLevel"));
                int completionStat = cursor.getInt(cursor.getColumnIndexOrThrow("CompletionStat"));
                String reminderDate = cursor.getString(cursor.getColumnIndexOrThrow("ReminderDate"));
                String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow("ReminderTime"));

                if (!currentDay.equals(dueDate)) {
                    currentDay = dueDate;
                    addDayHeader(currentDay);
                }

                addTaskCard(taskTitle, taskDescription, dueTime, priorityLevel, completionStat, reminderDate, reminderTime);

            } while (cursor.moveToNext());

            cursor.close();
        }
    }
}
