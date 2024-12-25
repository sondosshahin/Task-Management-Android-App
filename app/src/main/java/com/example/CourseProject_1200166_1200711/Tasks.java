package com.example.CourseProject_1200166_1200711;

public class Tasks {
    private String email;
    private String title;
    private String description;
    private String date;
    private String time;
    private int priority;
    private int completion;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getCompletion() {
        return completion;
    }
    public void setCompletion(int completion) {
        this.completion = completion;
    }
    @Override
    public String toString() {
        return "Student{" +
                "\nemail= " + email +
                "\ntitle= " + title +
                +'\n'+'}'+'\n';
    }
}
