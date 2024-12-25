package com.example.CourseProject_1200166_1200711.ui.all;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.CourseProject_1200166_1200711.DataBaseHelper;
import com.example.CourseProject_1200166_1200711.R;
import com.example.CourseProject_1200166_1200711.SharedPrefManager;
import com.example.CourseProject_1200166_1200711.databinding.FragmentAllBinding;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends Fragment {

    public class Task {
        public String title, description, dueTime, dueDate, reminderDate, reminderTime;
        public int priority, completionStatus;

        public Task(String title, String description, String dueTime, String dueDate,
                    int priority, int completionStatus, String reminderDate, String reminderTime) {
            this.title = title;
            this.description = description;
            this.dueTime = dueTime;
            this.dueDate = dueDate;
            this.priority = priority;
            this.completionStatus = completionStatus;
            this.reminderDate = reminderDate;
            this.reminderTime = reminderTime;
        }
    }

    private List<Task> allTasks = new ArrayList<>();
    private FragmentAllBinding binding;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private LinearLayout container;

    View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search when user submits (optional)
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    fillFlights();
                } else {
                    filterTasks(s);
                }
                return false;
            }
        });

        Button sort = (Button) root.findViewById(R.id.sort_button);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterTasksOnPriority();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataBaseHelper = new DataBaseHelper(requireContext(), "DB_PROJECT", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        container = view.findViewById(R.id.add_tasks);

        fillFlights();

    }

    public void fillFlights() {
        Cursor cursor = dataBaseHelper.getTasksSorted(sharedPrefManager.readString("email", "No Val"));
        if (cursor != null && cursor.moveToFirst()) {
            String currentDay = "";
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow("TaskTitle"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("TaskDescription"));
                String dueTime = cursor.getString(cursor.getColumnIndexOrThrow("DueDate"));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("DueTime"));
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow("PriorityLevel"));
                int completionStatus = cursor.getInt(cursor.getColumnIndexOrThrow("CompletionStat"));
                String reminderDate = cursor.getString(cursor.getColumnIndexOrThrow("ReminderDate"));
                String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow("ReminderTime"));

                Task task = new Task(title, description, dueTime, dueDate, priority, completionStatus, reminderDate, reminderTime);
                allTasks.add(task);

                if (!currentDay.equals(dueDate.split(" ")[0])) {
                    currentDay = dueDate.split(" ")[0];
                    addDayHeader(currentDay);
                }

                addTaskCard(title, description, dueTime, priority, completionStatus, reminderDate, reminderTime, dueDate);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void addDayHeader(String day) {
        TextView dayHeader = new TextView(getActivity());
        dayHeader.setText(day);
        dayHeader.setTextSize(20);
        dayHeader.setPadding(16, 16, 16, 16);

        GradientDrawable roundedBackground = new GradientDrawable();
        roundedBackground.setColor(Color.parseColor("#6E92C8"));
        roundedBackground.setCornerRadius(32);

        dayHeader.setBackground(roundedBackground);
        dayHeader.setTextColor(Color.BLACK);

        HorizontalScrollView scrollView = new HorizontalScrollView(getActivity());
        LinearLayout horizontalLayout = new LinearLayout(getActivity());
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setPadding(16, 16, 16, 16);
        scrollView.addView(horizontalLayout);

        if (container != null) {
            container.addView(dayHeader);
            container.addView(scrollView);
        }

        horizontalLayout.setTag(day);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addTaskCard(String title, String description, String dueTime, int priority, int completionStatus,
                             String reminderDate, String reminderTime, String dueDate) {
        View cardView = getLayoutInflater().inflate(R.layout.rounded, container, false);
        LinearLayout taskContainer = cardView.findViewById(R.id.taskContainer);


        taskContainer.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    taskContainer.setBackgroundColor(Color.parseColor("#252F92"));
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    taskContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    break;
            }
            return true;
        });

        TextView TaskTitle = new TextView(getActivity());
        TaskTitle.setTextColor(Color.parseColor("#000000"));
        TaskTitle.setText("    " + title);
        TaskTitle.setTextSize(20);
        TaskTitle.setTypeface(null, Typeface.BOLD);
        taskContainer.addView(TaskTitle);

        TextView descriptionForTask2 = new TextView(getActivity());
        descriptionForTask2.setTextColor(Color.parseColor("#000000"));
        descriptionForTask2.setText("    " + description);
        descriptionForTask2.setTextSize(16);
        taskContainer.addView(descriptionForTask2);

        String[] taskDetails = {
                "Due Time: " + dueTime,
                "Due Date: " + dueDate,
                "Priority: " + (priority == 1 ? "Medium" : priority == 2 ? "High" : "Low"),
                "Status: " + (completionStatus == 1 ? "Completed" : "Not Completed")
        };

        for (String detail : taskDetails) {
            TextView textView = new TextView(getActivity());
            textView.setText(detail);
            textView.setTextSize(16);
            textView.setPadding(0, 4, 0, 4);
            taskContainer.addView(textView);
        }

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof HorizontalScrollView) {
                LinearLayout horizontalLayout = (LinearLayout) ((HorizontalScrollView) child).getChildAt(0);
                if (horizontalLayout.getTag().equals(dueDate.split(" ")[0])) {
                    horizontalLayout.addView(cardView);
                    return;
                }
            }
        }
    }

    private void filterTasks(String query) {
        container.removeAllViews();
        String currentDay = "";

        for (Task task : allTasks) {
            if (task.title.toLowerCase().contains(query.toLowerCase()) ||
                    task.description.toLowerCase().contains(query.toLowerCase())) {

                if (!currentDay.equals(task.dueDate.split(" ")[0])) {
                    currentDay = task.dueDate.split(" ")[0];
                    addDayHeader(currentDay);
                }

                addTaskCard(task.title, task.description, task.dueTime, task.priority,
                        task.completionStatus, task.reminderDate, task.reminderTime, task.dueDate);
            }
        }
    }


    private void filterTasksOnPriority() {
        container.removeAllViews();
        String currentDay = "";

        for (Task task : allTasks) {
            // Check if we are on a new day
            if (!currentDay.equals(task.dueDate.split(" ")[0])) {
                currentDay = task.dueDate.split(" ")[0];
                addDayHeader(currentDay);

                // Add tasks with priority 2 (High Priority)
                for (Task highPriorityTask : allTasks) {
                    if (highPriorityTask.dueDate.split(" ")[0].equals(currentDay) && highPriorityTask.priority == 2) {
                        addTaskCard(highPriorityTask.title, highPriorityTask.description, highPriorityTask.dueTime,
                                highPriorityTask.priority, highPriorityTask.completionStatus, highPriorityTask.reminderDate,
                                highPriorityTask.reminderTime, highPriorityTask.dueDate);
                    }
                }

                // Add tasks with priority 1 (Medium Priority)
                for (Task mediumPriorityTask : allTasks) {
                    if (mediumPriorityTask.dueDate.split(" ")[0].equals(currentDay) && mediumPriorityTask.priority == 1) {
                        addTaskCard(mediumPriorityTask.title, mediumPriorityTask.description, mediumPriorityTask.dueTime,
                                mediumPriorityTask.priority, mediumPriorityTask.completionStatus, mediumPriorityTask.reminderDate,
                                mediumPriorityTask.reminderTime, mediumPriorityTask.dueDate);
                    }
                }

                // Add tasks with priority 0 (Low Priority)
                for (Task lowPriorityTask : allTasks) {
                    if (lowPriorityTask.dueDate.split(" ")[0].equals(currentDay) && lowPriorityTask.priority == 0) {
                        addTaskCard(lowPriorityTask.title, lowPriorityTask.description, lowPriorityTask.dueTime,
                                lowPriorityTask.priority, lowPriorityTask.completionStatus, lowPriorityTask.reminderDate,
                                lowPriorityTask.reminderTime, lowPriorityTask.dueDate);
                    }
                }
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
