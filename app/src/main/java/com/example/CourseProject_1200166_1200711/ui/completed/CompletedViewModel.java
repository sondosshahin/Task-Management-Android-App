package com.example.CourseProject_1200166_1200711.ui.completed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CompletedViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public CompletedViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is completed fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
