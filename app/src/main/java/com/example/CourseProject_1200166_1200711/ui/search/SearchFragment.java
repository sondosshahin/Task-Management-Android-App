package com.example.CourseProject_1200166_1200711.ui.search;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.CourseProject_1200166_1200711.DataBaseHelper;
import com.example.CourseProject_1200166_1200711.ModifyTaskDialogFragment;
import com.example.CourseProject_1200166_1200711.R;
import com.example.CourseProject_1200166_1200711.SharedPrefManager;

import java.util.Calendar;

public class SearchFragment extends Fragment {

    private ViewGroup container;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String currentDay = "";
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Button searchButton;
    private LinearLayout verticalLayout2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.container = view.findViewById(R.id.searchLayout);
        verticalLayout2 = createVerticalButtonLayout();
        addButtonsToVerticalLayout(verticalLayout2, "Button 3", "Button 4");



        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        searchButton = view.findViewById(R.id.searchButton);

        dataBaseHelper = new DataBaseHelper(requireContext(), "DB_PROJECT", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText));

        searchButton.setOnClickListener(v -> searchTasks());
    }
    private void addButtonsToVerticalLayout(LinearLayout layout, String btnText1, String btnText2) {
        Button button1 = createButton(btnText1);
        Button button2 = createButton(btnText2);

        layout.addView(button1);
        layout.addView(button2);
    }

    private Button createButton(String text) {
        Button button = new Button(getActivity());
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(Color.BLUE);
        button.setPadding(16, 16, 16, 16);

        button.setOnClickListener(v -> {
            // Handle button click action
            System.out.println(text + " Clicked!");
        });

        return button;
    }
    private LinearLayout createVerticalButtonLayout() {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        return layout;
    }

    private void showDatePickerDialog(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedMonth += 1;  // Month is zero-based
            String date = selectedYear + "-" + (selectedMonth < 10 ? "0" + selectedMonth : selectedMonth) + "-" + (selectedDay < 10 ? "0" + selectedDay : selectedDay);
            editText.setText(date);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void addDayHeader(String day) {
        TextView dayHeader = new TextView(getActivity());
        dayHeader.setText(day);
        dayHeader.setTextSize(20);

        int padding = 20;
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

    private void addTaskCard(String taskTitle, String taskDescription, String dueTime, String dueDate,
                             int priorityLevel, int completionStat, String reminderDate, String reminderTime) {

        View cardView = getLayoutInflater().inflate(R.layout.rounded, container, false);
        LinearLayout taskContainer = cardView.findViewById(R.id.taskContainer);

        String[] texts = new String[]{
                taskTitle,
                taskDescription,
                dueTime,
                dueDate,
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

        // Adding buttons to modify tasks
        LinearLayout buttonLayout = new LinearLayout(getActivity());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button modifyButton = createButton("Modify");
        modifyButton.setOnClickListener(v -> modifyTask(taskTitle, cardView,taskContainer));

        Button deleteButton = createButton("Delete");
        deleteButton.setOnClickListener(v -> deleteTask(taskTitle, cardView));

        Button shareButton = createButton("Share");
        shareButton.setOnClickListener(v -> shareTask(taskTitle,texts));

        buttonLayout.addView(modifyButton);
        buttonLayout.addView(deleteButton);
        buttonLayout.addView(shareButton);

        taskContainer.addView(buttonLayout);

        container.addView(cardView);
    }

    private void modifyTask(String taskTitle, View cardView, LinearLayout taskContainer) {
        // Extract current task details
        String currentDescription = ((TextView) taskContainer.getChildAt(1)).getText().toString();
        String currentDueTime = ((TextView) taskContainer.getChildAt(2)).getText().toString();
        String currentDueDate = ((TextView) taskContainer.getChildAt(3)).getText().toString();
        String currentPriority = ((TextView) taskContainer.getChildAt(4)).getText().toString();
        String currentStatus = ((TextView) taskContainer.getChildAt(5)).getText().toString();
        String currentReminderDate = ((TextView) taskContainer.getChildAt(6)).getText().toString();
        String currentReminderTime = ((TextView) taskContainer.getChildAt(7)).getText().toString();

        // Initialize the dialog and pass current details
        ModifyTaskDialogFragment modifyTaskDialog = new ModifyTaskDialogFragment();
        modifyTaskDialog.setTaskDetails(taskTitle, currentDescription, currentDueTime, currentDueDate, currentPriority, currentStatus, currentReminderDate, currentReminderTime);

        // Define listener for dialog result
        modifyTaskDialog.setListener((newTitle, newDescription, newDueTime, newDueDate, priority, status, newReminderDate, newReminderTime) -> {
            // Update UI
            updateTaskCard(cardView, newTitle, newDescription, newDueTime, newDueDate, priority, status, newReminderDate, newReminderTime);

            // Convert priority and status for database storage
            int intPriority = priority.equalsIgnoreCase("High") ? 2 : priority.equalsIgnoreCase("Medium") ? 1 : 0;
            int intStatus = status.equalsIgnoreCase("Completed") ? 1 : 0;

            // Update task in the database
            dataBaseHelper.updateTask(taskTitle, newTitle, newDescription, newDueTime, newDueDate, intPriority, intStatus, newReminderDate, newReminderTime);
        });

        // Show the dialog
        modifyTaskDialog.show(getActivity().getSupportFragmentManager(), "modifyTaskDialog");
    }




    private void updateTaskCard(View cardView, String newTitle, String newDescription, String newDueTime, String newDueDate, String priority, String status,
                                String newReminderDate, String newReminderTime) {

        View cardsView = getLayoutInflater().inflate(R.layout.rounded, container, false);
        LinearLayout card = cardView.findViewById(R.id.taskContainer);
        // Update the TextViews with the new task information

        ((TextView) card.getChildAt(0)).setText(newTitle);
        ((TextView) card.getChildAt(1)).setText(newDescription);
        ((TextView) card.getChildAt(2)).setText(newDueTime);
        ((TextView) card.getChildAt(3)).setText(newDueDate);
        ((TextView) card.getChildAt(4)).setText(priority);
        ((TextView) card.getChildAt(5)).setText(status);
        ((TextView) card.getChildAt(6)).setText(newReminderDate);
        ((TextView) card.getChildAt(7)).setText(newReminderTime);


    }

    private void deleteTask(String title,  View cardView) {
        Toast.makeText(getActivity(), "Deleted " + title, Toast.LENGTH_SHORT).show();
        boolean deleted = dataBaseHelper.delete_task(title);
        if (deleted) {
            // If the task was successfully deleted from the database, remove the card view
            container.removeView(cardView);
        } else {
            Toast.makeText(getActivity(), "Failed to delete " + title, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareTask(String title,String[] info) {
        String data = "Title :  " + info[0] + "\nDescription: " + info[1] + "\nDue time:" + info[2] + "\nPriority: " + info[3] +
                "\nCompletion state: " + info[4] + "\nRemainder date: " + info[5] + "\nReminder time: " + info[6] ;
        Toast.makeText(getActivity(), "Sharing task: " + title, Toast.LENGTH_SHORT).show();
        Intent gmailIntent =new Intent();
        gmailIntent.setAction(Intent.ACTION_SENDTO);
        gmailIntent.setType("message/rfc822");
        gmailIntent.setData(Uri.parse("mailto:"));
        gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String []{"sondosshahin1@gmail.com"});
        gmailIntent.putExtra(Intent.EXTRA_SUBJECT,title);
        gmailIntent.putExtra(Intent.EXTRA_TEXT,data);
        startActivity(gmailIntent);
    }


    private void searchTasks() {
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        String sstartDate = reverseDateFormat(startDate);
        String eendDate = reverseDateFormat(endDate);
        if (sstartDate.isEmpty() || eendDate.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter both start and end dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sstartDate.compareTo(eendDate) > 0) {
            Toast.makeText(getActivity(), "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
            return;
        }

        container.removeAllViews();

        Cursor cursor = dataBaseHelper.getTasksWithinDateRange(sharedPrefManager.readString("email", "No Val"), sstartDate, eendDate);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String taskTitle = cursor.getString(cursor.getColumnIndexOrThrow("TaskTitle"));
                String taskDescription = cursor.getString(cursor.getColumnIndexOrThrow("TaskDescription"));
                String dueTime = cursor.getString(cursor.getColumnIndexOrThrow("DueDate"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("DueTime"));
                int priorityLevel = cursor.getInt(cursor.getColumnIndexOrThrow("PriorityLevel"));
                int completionStat = cursor.getInt(cursor.getColumnIndexOrThrow("CompletionStat"));
                String reminderDate = cursor.getString(cursor.getColumnIndexOrThrow("ReminderDate"));
                String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow("ReminderTime"));

                if (!currentDay.equals(dueDate)) {
                    currentDay = dueDate;
                    addDayHeader(currentDay);
                }


                addTaskCard(taskTitle, taskDescription, dueTime, dueDate,priorityLevel, completionStat, reminderDate, reminderTime);

            } while (cursor.moveToNext());

            cursor.close();

        } else {
            Toast.makeText(getActivity(), "No tasks found within the specified date range", Toast.LENGTH_SHORT).show();
        }
    }
    public static String reverseDateFormat(String date) {
        String[] parts = date.split("-");

        if (parts.length == 3) {
            return parts[2] + "-" + parts[1] + "-" + parts[0];
        } else {
            return "Invalid date format";
        }
    }
}
