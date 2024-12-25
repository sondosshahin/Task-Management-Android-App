package com.example.CourseProject_1200166_1200711;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskJsonParser {
    public static List<Tasks> getObjectFromJson(String json) {
        List<Tasks> tasks;
        try {
            JSONArray jsonArray = new JSONArray(json);
            tasks = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject = (JSONObject) jsonArray.get(i);
                Tasks task = new Tasks();
                task.setEmail(jsonObject.getString("EMAIL"));
                task.setTitle(jsonObject.getString("TaskTitle"));
                task.setDescription(jsonObject.getString("TaskDescription"));
                task.setDate(jsonObject.getString("DueDate"));
                task.setTime(jsonObject.getString("DueTime"));
                task.setPriority(jsonObject.getInt("PriorityLevel"));
                task.setCompletion(jsonObject.getInt("CompletionStat"));
                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tasks;
    }
}
