package com.example.CourseProject_1200166_1200711.ui.today;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TodayFragment extends Fragment {

    private LinearLayout todayLayout;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private ViewGroup container;
    private ImageView imageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        imageView = view.findViewById(R.id.imageView); // Use 'view' to find the ImageView in the fragment's layout
        if (imageView != null) {
            imageView.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide)); // Apply animation
        }

 */
        todayLayout = view.findViewById(R.id.todayLayout);
        this.container = view.findViewById(R.id.todayLayout);




        dataBaseHelper = new DataBaseHelper(requireContext(), "DB_PROJECT", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());

        displayTodayTasks();
    }

    private void displayTodayTasks() {
        todayLayout.removeAllViews();  // Clear previous views

        String todayDate = getCurrentDate();
        todayDate = reverseDateFormat(todayDate);
        String email = sharedPrefManager.readString("email", "No Val");
        Cursor cursor = dataBaseHelper.getTasksDueToday(email, todayDate);

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

                addTaskCard(taskTitle, taskDescription, dueTime,dueDate, priorityLevel, completionStat, reminderDate, reminderTime);


            } while (cursor.moveToNext());

            cursor.close();

        } else {
            Toast.makeText(getActivity(), "No tasks found for today", Toast.LENGTH_SHORT).show();
        }

        // Check if all tasks are completed
        checkAllTasksCompleted(todayDate);
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendar.getTime());
    }

    public static String reverseDateFormat(String date) {
        String[] parts = date.split("-");

        if (parts.length == 3) {
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } else {
            return "Invalid date format";
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
            System.out.println(text + " Clicked!");
        });

        return button;
    }

    private void checkAllTasksCompleted(String date) {
        String email = sharedPrefManager.readString("email", "No Val");
        Cursor cursor = dataBaseHelper.getTasksDueToday(email, date);

        boolean allCompleted = false;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int completionStat = cursor.getInt(cursor.getColumnIndexOrThrow("CompletionStat"));
                if (completionStat != 1) { // 1 means "Completed"
                    allCompleted = false;
                    break;
                }
                else {
                    allCompleted = true;
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        if (allCompleted) {
            showCongratsAnimation();

        }
    }

    private void showCongratsAnimation() {
        if (imageView != null) {
            Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.congrats_anim);
            imageView.startAnimation(animation);
        }
        TextView congratsTextView = new TextView(getActivity());
        congratsTextView.setText("🎉 Congratulations! All tasks are completed! 🎉");
        congratsTextView.setTextSize(20);
        congratsTextView.setTextColor(Color.BLUE);
        congratsTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        congratsTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        congratsTextView.setSingleLine(false); // Allow multi-line text
        congratsTextView.setMaxLines(3);
        congratsTextView.setPadding(16, 10, 16, 30);

        // Layout parameters to ensure the TextView takes up enough space
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 30); // Add some margins
        congratsTextView.setLayoutParams(layoutParams);

        // Set initial position of the TextView off-screen
        congratsTextView.setTranslationY(-200f); // Start above the visible area
        congratsTextView.setAlpha(0f); // Initially transparent

        // Add the TextView to layout
        todayLayout.addView(congratsTextView);

        // Create an animation to move the TextView and fade it in
        congratsTextView.animate()
                .translationYBy(300f) // Move it 300 pixels down
                .alpha(1f) // Fade it in
                .setDuration(2000) // 2 seconds
                .withEndAction(() -> {
                    // After the movement, animate it to bounce back a little
                    congratsTextView.animate()
                            .translationYBy(-50f) // Slight bounce back
                            .setDuration(500) // Shorter duration for bounce
                            .start();
                })
                .start();


        ImageView imageView = new ImageView(requireContext());
        ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        imageView.setLayoutParams(layoutParams2);

        // Set the image resource
        imageView.setImageResource(R.drawable.congrats);
        // Add the ImageView to the parent layout
        container.addView(imageView);
        // start an animation
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.congrats_anim);
        imageView.startAnimation(animation);

    }





}
