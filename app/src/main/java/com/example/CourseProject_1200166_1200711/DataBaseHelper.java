package com.example.CourseProject_1200166_1200711;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE USERS(EMAIL TEXT PRIMARY KEY, FNAME TEXT, LNAME TEXT, PASSWORD TEXT)");
        sqLiteDatabase.execSQL(
                "CREATE TABLE Tasks(" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "EMAIL TEXT, " +
                        "TaskTitle TEXT, " +
                        "TaskDescription TEXT, " +
                        "DueDate TEXT, " +
                        "DueTime TEXT, " +
                        "PriorityLevel INTEGER, " +
                        "CompletionStat INTEGER, " +
                        "ReminderDate TEXT, " +
                        "ReminderTime TEXT)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public Cursor getTasksDueToday(String email, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM Tasks WHERE EMAIL = ? AND DueTime = ?", new String[]{email, date}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }

    public Cursor getTasksSorted(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT * FROM Tasks WHERE EMAIL = ? ORDER BY DueTime ASC, DueDate ASC",
                    new String[]{email}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }
    public Cursor getTasksWithinDateRange(String email, String startDate, String endDate) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.rawQuery(
                    "SELECT * FROM Tasks " +
                            "WHERE EMAIL = ? " +
                            "AND DueTime BETWEEN ? AND ? " +
                            "ORDER BY DueTime ASC, DueDate ASC",
                    new String[]{email, startDate, endDate}
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cursor;
    }





    public Cursor getCompletedTasks(String email) {
        return getReadableDatabase().query("Tasks",
                null,
                "email = ? AND CompletionStat = ?",
                new String[]{email, "1"},
                null, null, "DueTime ASC");
    }

    public boolean user_exist(String email){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("SELECT * FROM USERS WHERE EMAIL = ?", new String[]{email}, null);
        return (cursor.getCount() >0);
    }

    public String get_pass(String email){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("SELECT PASSWORD FROM USERS WHERE EMAIL = ?", new String[]{email}, null);
        cursor.moveToFirst();
        return (cursor.getString(0));
    }


    public void add_user(String email, String fname, String lname , String pass){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", email);
        contentValues.put("FNAME", fname);
        contentValues.put("LNAME", lname);
        contentValues.put("PASSWORD", pass);
        sqLiteDatabase.insert("USERS", null, contentValues);
    }
    public void add_task(String email, String TaskTitle, String TaskDescription , String DueTime, int priority, int CompletionStat, String ReminderDate, String ReminderTime, String dueToDate){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", email);
        contentValues.put("TaskTitle", TaskTitle);
        contentValues.put("TaskDescription", TaskDescription);
        contentValues.put("DueTime", DueTime);
        contentValues.put("DueDate", dueToDate);
        contentValues.put("PriorityLevel", priority);
        contentValues.put("CompletionStat", CompletionStat);
        contentValues.put("ReminderDate", ReminderDate);
        contentValues.put("ReminderTime", ReminderTime);

        sqLiteDatabase.insert("Tasks", null, contentValues);
    }

    public Cursor getProfile(String email){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("SELECT * FROM USERS WHERE EMAIL = ?", new String[]{email}, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getTasks(String email){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor= sqLiteDatabase.rawQuery("SELECT * FROM Tasks WHERE EMAIL = ?", new String[]{email}, null);
        cursor.moveToFirst();
        return cursor;
    }

    public boolean delete_task(String title){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deleted = sqLiteDatabase.delete("Tasks", "TaskTitle = ?", new String[]{title});
        sqLiteDatabase.close();
        return deleted > 0;
    }

    public boolean updateProfile(String oldEmail, String email, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", email);        // Column name and new value
        contentValues.put("PASSWORD", pass);
        int rowsUpdated = db.update("USERS", contentValues, "EMAIL = ?", new String[]{oldEmail});
        db.close();
        return rowsUpdated > 0; // Returns true if at least one row was updated
    }

    public int getTaskCount(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        int count = 0;

        // SQL Query to count rows where EMAIL matches
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM Tasks WHERE EMAIL = ?", new String[]{email});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0); // Get the count result from the first column
            }
            cursor.close();
        }

        return count; // Return the number of rows
    }


    public void updateTask(String oldTitle, String title, String descrioption, String duetime, String duedate, int priority, int status, String remtime, String remdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("TaskTitle", title);
        contentValues.put("TaskDescription", descrioption);
        contentValues.put("DueDate", duedate);
        contentValues.put("DueTime", duetime);
        contentValues.put("PriorityLevel", priority);
        contentValues.put("CompletionStat", status);
        contentValues.put("ReminderDate", remdate);
        contentValues.put("ReminderTime", remtime);
        int rowsUpdated = db.update("Tasks", contentValues, "TaskTitle = ?", new String[]{oldTitle});
        db.close();
    }



}
