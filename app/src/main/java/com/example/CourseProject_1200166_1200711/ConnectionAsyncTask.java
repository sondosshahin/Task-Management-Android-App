package com.example.CourseProject_1200166_1200711;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.example.CourseProject_1200166_1200711.ui.newTask.NewTaskFragment;

import java.util.List;
public class ConnectionAsyncTask extends AsyncTask<String, String,
        String> {
    Activity activity;
    Fragment frag;
    DataBaseHelper dataBaseHelper = new DataBaseHelper(activity, "DB_PROJECT", null, 1);
    public ConnectionAsyncTask(Activity activity, Fragment frag) {
        this.activity = activity;
        this.frag = frag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... params) {
        String data = HttpManager.getData(params[0]);
        return data;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        List<Tasks> tasks =TaskJsonParser.getObjectFromJson(s);
        ((NewTaskFragment)frag).saveImportedTasks(tasks);
    }
}