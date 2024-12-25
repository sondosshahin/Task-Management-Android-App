package com.example.CourseProject_1200166_1200711.ui.newTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewTaskViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NewTaskViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is new task fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}