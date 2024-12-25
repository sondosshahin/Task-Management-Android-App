package com.example.CourseProject_1200166_1200711.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.CourseProject_1200166_1200711.DataBaseHelper;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> mName;
    private final MutableLiveData<String> mEmail;
    private final MutableLiveData<String> mPass;
    private final MutableLiveData<String> mSharedPrefValue;

    public ProfileViewModel(Context context) {
        mName = new MutableLiveData<>();
        mEmail = new MutableLiveData<>();
        mPass = new MutableLiveData<>();
        mSharedPrefValue = new MutableLiveData<>();
        loadProfile(context);
    }

    public LiveData<String> getName() {
        return mName;
    }
    public LiveData<String> getEmail() {
        return mEmail;
    }
    public LiveData<String> getPass() {
        return mPass;
    }

    public void loadProfile (Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("My Shared Preference", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("email", null);
        mSharedPrefValue.setValue(value);
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context,"DB_PROJECT",null,1);
        Cursor user = dataBaseHelper.getProfile(value);
        if (user != null && user.moveToFirst()) {
            mEmail.setValue(user.getString(0));
            mName.setValue(user.getString(1) + " " + user.getString(2));
            mPass.setValue(user.getString(3));
            user.close();
        }

    }
}
