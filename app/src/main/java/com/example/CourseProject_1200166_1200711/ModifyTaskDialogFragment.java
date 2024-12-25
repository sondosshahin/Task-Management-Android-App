package com.example.CourseProject_1200166_1200711;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ModifyTaskDialogFragment extends DialogFragment {

    private String taskTitle, taskDescription, dueTime,dueDate, reminderDate, reminderTime;
    private String  priorityLevel, completionStat;

    private ModifyTaskListener listener;

    // Interface to send data back to parent activity
    public interface ModifyTaskListener {
        void onTaskModified(String newTitle, String newDescription, String newDueTime, String newDueDate,
                            String  newPriorityLevel, String   newCompletionStat, String newReminderDate, String newReminderTime);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the dialog layout
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_modify_task, null);

        // Find EditTexts for task fields
        EditText titleEditText = view.findViewById(R.id.task_title_edit_text);
        EditText descriptionEditText = view.findViewById(R.id.task_description_edit_text);
        EditText dueTimeEditText = view.findViewById(R.id.task_due_time_edit_text);
        EditText dueDateEditText = view.findViewById(R.id.task_due_date_edit_text);
        EditText priorityEditText = view.findViewById(R.id.task_priority_edit_text);
        EditText statusEditText = view.findViewById(R.id.task_status_edit_text);
        EditText reminderDateEditText = view.findViewById(R.id.task_reminder_date_edit_text);
        EditText reminderTimeEditText = view.findViewById(R.id.task_reminder_time_edit_text);

        // Set existing task details
        titleEditText.setText(taskTitle);
        descriptionEditText.setText(taskDescription);
        dueTimeEditText.setText(dueTime);
        dueDateEditText.setText(dueDate);
        priorityEditText.setText(priorityLevel);
        statusEditText.setText(completionStat);
        reminderDateEditText.setText(reminderDate);
        reminderTimeEditText.setText(reminderTime);

        builder.setView(view)
                .setTitle("Modify Task")
                .setPositiveButton("Save", (dialog, id) -> {
                    // Collect updated information
                    String newTitle = titleEditText.getText().toString();
                    String newDescription = descriptionEditText.getText().toString();
                    String newDueTime = dueTimeEditText.getText().toString();
                    String newDueDate = dueDateEditText.getText().toString();
                    String newPriority = priorityEditText.getText().toString();
                    String newStatus = statusEditText.getText().toString();
                    String newReminderDate = reminderDateEditText.getText().toString();
                    String newReminderTime = reminderTimeEditText.getText().toString();

                    // Notify listener with updated task info
                    listener.onTaskModified(newTitle, newDescription, newDueTime, newDueDate,newPriority, newStatus,
                             newReminderDate, newReminderTime);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());

        return builder.create();
    }

    // Set listener to pass back modified data to the parent
    public void setListener(ModifyTaskListener listener) {
        this.listener = listener;
    }

    // Set the current task details before displaying the dialog
    public void setTaskDetails(String title, String description, String dueTime, String dueDate,
                               String priority, String completionStatus, String reminderDate, String reminderTime) {
        this.taskTitle = title;
        this.taskDescription = description;
        this.dueTime = dueTime;
        this.dueDate = dueDate;
        this.priorityLevel = priority;
        this.completionStat = completionStatus;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
    }
}
