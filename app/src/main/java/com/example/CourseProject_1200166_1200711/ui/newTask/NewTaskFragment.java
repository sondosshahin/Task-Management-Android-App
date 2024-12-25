package com.example.CourseProject_1200166_1200711.ui.newTask;



import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.CourseProject_1200166_1200711.ConnectionAsyncTask;
import com.example.CourseProject_1200166_1200711.DataBaseHelper;
import com.example.CourseProject_1200166_1200711.NotificationReceiver;
import com.example.CourseProject_1200166_1200711.R;
import com.example.CourseProject_1200166_1200711.SharedPrefManager;
import com.example.CourseProject_1200166_1200711.Tasks;
import com.example.CourseProject_1200166_1200711.databinding.FragmentNewTaskBinding;

import java.util.Calendar;
import java.util.List;


public class NewTaskFragment extends Fragment {

    private static final String MY_CHANNEL_ID = "my_chanel_1";
    private static final String MY_CHANNEL_NAME = "My channel";
    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_TITLE = "Notification Title";
    private static final String NOTIFICATION_BODY = "This is the body of my notification";

    private FragmentNewTaskBinding binding;
    private EditText titleEditText, descriptionEditText, dateEditText, timeEditText, dateEditTextRem, timeEditTextRem;
    private LinearLayout dateTimeContainer;
    private CheckBox reminderCheckBox;
    private CheckBox[] priorites;
    private CheckBox[] completion;
    private TextView create;
    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager shared;
    private TextView t;
    private Button import_api;
    int priority = 1;
    int completed = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        titleEditText = root.findViewById(R.id.task_title);
        descriptionEditText = root.findViewById(R.id.description);
        dateEditText = root.findViewById(R.id.date);
        timeEditText = root.findViewById(R.id.time);
        dateEditTextRem = root.findViewById(R.id.reminder_date);
        timeEditTextRem = root.findViewById(R.id.reminder_time);
        dateTimeContainer = root.findViewById(R.id.date_time_container);
        reminderCheckBox = root.findViewById(R.id.reminder_checkbox);
        create = root.findViewById(R.id.create);
        t = root.findViewById(R.id.test);
        import_api = root.findViewById(R.id.importApi);
        priorites = new CheckBox[3];
        completion = new CheckBox[2];
        priorites[0] = root.findViewById(R.id.high);
        priorites[1] = root.findViewById(R.id.medium);
        priorites[2] = root.findViewById(R.id.low);
        completion[0] = root.findViewById(R.id.not_completed);
        completion[1] = root.findViewById(R.id.completed);

        dataBaseHelper = new DataBaseHelper(requireContext(), "DB_PROJECT", null, 1);
        shared = SharedPrefManager.getInstance(requireContext());

       // createNotificationChannel(requireContext());

        dateEditText.setOnClickListener(v -> showDatePickerDialog(dateEditText));
        dateEditTextRem.setOnClickListener(v -> showDatePickerDialog(dateEditTextRem));
        timeEditText.setOnClickListener(v -> showTimePickerDialog(timeEditText));
        timeEditTextRem.setOnClickListener(v -> showTimePickerDialog(timeEditTextRem));

        reminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                dateTimeContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE));

        // Set up reminder checkbox behavior
        reminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                dateTimeContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        if (reminderCheckBox.isChecked()){
            scheduleNotification(requireContext(),System.currentTimeMillis() + 6000, NOTIFICATION_TITLE, NOTIFICATION_BODY );
        }

        create.setOnClickListener(view -> saveTask());
        import_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectionAsyncTask connectionAsyncTask = new ConnectionAsyncTask(getActivity(), NewTaskFragment.this);
                connectionAsyncTask.execute("https://mocki.io/v1/d58eab9a-8f47-4a81-bd83-d3cffcf256a6");
                Toast.makeText(getActivity(), "Tasks imported from API", Toast.LENGTH_SHORT).show();
            }
        });

        priorites[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorites[1].setChecked(false);
                    priorites[2].setChecked(false);
                }
                priority = 0;
            }
        });
        priorites[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorites[0].setChecked(false);
                    priorites[2].setChecked(false);
                }
                priority = 1;
            }
        });
        priorites[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    priorites[0].setChecked(false);
                    priorites[1].setChecked(false);
                }
                priority = 2;
            }
        });

        completion[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    completion[1].setChecked(false);
                }
                completed = 0;
            }
        });

        completion[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    completion[0].setChecked(false);
                }
                completed = 1;
            }
        });
        return root;
    }

    private void showDatePickerDialog(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    month1 += 1;
                    String date = dayOfMonth + "/" + month1 + "/" + year1;
                    targetEditText.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
    private void showTimePickerDialog(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                R.style.CustomTimePickerTheme,
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    targetEditText.setText(time);
                },
                hour, minute, true
        );

        timePickerDialog.setOnShowListener(dialogInterface -> {
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6E92C8"));
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
        });

        timePickerDialog.show();
    }
    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String dueToDate = dateEditText.getText().toString().trim();
        String dueToTime = timeEditText.getText().toString().trim();
        String remDate = dateEditTextRem.getText().toString().trim();
        String remTime = timeEditTextRem.getText().toString().trim();

        // Save task to database
        dataBaseHelper.add_task(shared.readString("email" , "No Val"),  title, description, dueToDate, priority , completed , dueToTime, remDate, remTime);
        String value = shared.readString("email", null);
        Cursor user = dataBaseHelper.getTasks(value);

        t.setText(Integer.toString(dataBaseHelper.getTaskCount(shared.readString("email" , "No Val"))));
        Toast.makeText(getActivity(), "Task created", Toast.LENGTH_SHORT).show();
    }

    public void saveImportedTasks(List<Tasks> tasks){
        for (int i = 0; i < tasks.size(); i++) {
            Tasks t = tasks.get(i);
            dataBaseHelper.add_task(t.getEmail(), t.getTitle(), t.getDescription(), t.getTime(), t.getPriority(), t.getCompletion(), "0","0",t.getDate());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void createNotification(Context context, String title, String body) {
        createNotificationChannel(context); // Ensure channel is created

        Intent intent = new Intent(context, context.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }




    public void scheduleNotification(Context context, long triggerTime, String title, String body) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("body", body);

        int requestCode = (int) System.currentTimeMillis(); // Unique request code
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }


    public void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel for my notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(MY_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

















//public class NewTaskFragment extends Fragment {
//
//    private FragmentNewTaskBinding binding;
//    private EditText titleEditText, descriptionEditText, dateEditText, timeEditText, dateEditTextRem, timeEditTextRem;
//    private LinearLayout dateTimeContainer;
//    private CheckBox reminderCheckBox;
//    private TextView create;
//    private DataBaseHelper dataBaseHelper;
//
//public class NewTaskFragment extends Fragment {
//
//    private FragmentNewTaskBinding binding;
//    private EditText dateEditText;
//    private EditText titleEditText;
//    private EditText descriptionEditText;
//    private EditText timeEditText;
//    private EditText dateEditTextRem;
//    private EditText timeEditTextRem;
//    private CheckBox reminderCheckBox;
//    private Button create;
//    private LinearLayout dateTimeContainer ;
//    String title, description , priority, completion;
//    String remTime, dueToTime;
//    String dueToDate, remDate;
//    DataBaseHelper dataBaseHelper;
//    SharedPrefManager sharedPrefManager;
//
//
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//
//        NewTaskViewModel newTaskViewModel =
//                new ViewModelProvider(this).get(NewTaskViewModel.class);
//
//        binding = FragmentNewTaskBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        titleEditText = root.findViewById(R.id.task_title);
//        descriptionEditText = root.findViewById(R.id.description);
//        dateEditText = root.findViewById(R.id.date);
//        dateEditTextRem = root.findViewById(R.id.reminder_date);
//        timeEditTextRem = root.findViewById(R.id.reminder_time);
//        create = root.findViewById(R.id.create);
//        timeEditText = root.findViewById(R.id.time);
//        reminderCheckBox = root.findViewById(R.id.reminder_checkbox);
//        dateTimeContainer = root.findViewById(R.id.date_time_container);
//
//        dataBaseHelper =new DataBaseHelper(NewTaskFragment.this,"DB_PROJECT",null,1);
//
//        sharedPrefManager =SharedPrefManager.getInstance(this);
//
//        dateEditText.setOnClickListener(v -> showDatePickerDialog());
//        dateEditTextRem.setOnClickListener(v -> showDatePickerDialog());
//        timeEditText.setOnClickListener(v -> showTimePickerDialog());
//        timeEditTextRem.setOnClickListener(v -> showTimePickerDialog());
//        reminderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                dateTimeContainer.setVisibility(View.VISIBLE);
//            } else {
//                dateTimeContainer.setVisibility(View.GONE);
//            }
//        });
//        create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                title = titleEditText.getText().toString();
//                description = descriptionEditText.getText().toString();
//                dueToDate = dateEditText.getText().toString();
//                dueToTime = timeEditText.getText().toString();
//                remTime = timeEditTextRem.getText().toString();
//                remDate = dateEditTextRem.getText().toString();
//                dataBaseHelper.add_user(sharedPref title , description , dueToDate, dueToDate, );
//            }
//
//        });
//
////        Spinner spinner = binding.spinner;
////
////        String[] spinnerValues = {"High", "Medium", "Low"};
////
////
////        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerValues);
////        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
////
////        spinner.setAdapter(adapter);
////        spinner = binding.completionSpinner;
////        spinnerValues = new String[]{"Completed", "Not Completed"};
////
////        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerValues);
////        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
////        spinner.setAdapter(adapter);
//        return root;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//    private void showDatePickerDialog() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                getContext(),
//                (view, year1, month1, dayOfMonth) -> {
//                    month1 = month1 + 1;
//                    String date = dayOfMonth + "/" + month1 + "/" + year1;
//                    dateEditText.setText(date);
//                },
//                year, month, day);
//
//        datePickerDialog.show();
//    }
//
//    private void showTimePickerDialog() {
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog dialog = new TimePickerDialog(
//                getContext(),
//                R.style.CustomTimePickerTheme,
//                (view, selectedHour, selectedMinute) -> {
//                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
//                    timeEditText.setText(time);
//                },
//                hour,
//                minute,
//                true);
//
//        dialog.show();
//
//        // Access dialog buttons
//        dialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#6E92C8"));
//        dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
//    }
//
//
//}
