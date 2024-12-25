package com.example.CourseProject_1200166_1200711;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.CourseProject_1200166_1200711.ui.newTask.NewTaskFragment;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String MY_CHANNEL_ID = "my_chanel_1";

    @Override
    public void onReceive(Context context, Intent intent) {
        NewTaskFragment nf = new NewTaskFragment();
        nf.createNotificationChannel(context); // Ensure channel is created

        nf.createNotification(context, "title", "body");
    }

}