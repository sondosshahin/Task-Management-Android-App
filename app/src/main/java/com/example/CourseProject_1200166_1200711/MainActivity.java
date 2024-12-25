package com.example.CourseProject_1200166_1200711;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DataBaseHelper dataBaseHelper;
    SharedPrefManager sharedPrefManager;
    CheckBox darkmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBaseHelper =new DataBaseHelper(MainActivity.this,"DB_PROJECT",null,1);

        sharedPrefManager =SharedPrefManager.getInstance(this);

        EditText email = (EditText) findViewById(R.id.editTextEmail);
        EditText password = (EditText) findViewById(R.id.editTextPassword);
        Button sign_in = (Button) findViewById(R.id.buttonLogin);
        Button sign_up = (Button) findViewById(R.id.buttonSignUp);
        CheckBox remember = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        //darkmode = (CheckBox) findViewById(R.id.dark_mode);

        if (sharedPrefManager.readboolean("remember",false)){       //user checked the remember me checkbox
            email.setText(sharedPrefManager.readString("email", null));
            password.setText(sharedPrefManager.readString("password", null));
            remember.setChecked(true);
        }

//        boolean dark = sharedPrefManager.readboolean("enable", false);
//        apply_darkmode(dark);
//
//        darkmode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sharedPrefManager.writeboolean("enable", darkmode.isChecked());
//                boolean isdark = sharedPrefManager.readboolean("enable", false);
//                apply_darkmode(isdark);
//                darkmode.setChecked(isdark);
//            }
//        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataBaseHelper.user_exist(email.getText().toString())){
                    if (dataBaseHelper.get_pass(email.getText().toString()).equals(password.getText().toString())){     //pass matches the one in the database
                        //if (remember.isChecked()){      //save in shared pref
                            sharedPrefManager.writeString("email", email.getText().toString());
                            sharedPrefManager.writeString("password", password.getText().toString());
                            sharedPrefManager.writeboolean("remember", true);
                        //}
                        //else{
                          //  sharedPrefManager.writeboolean("remember", false);
                        //}

                        Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                        MainActivity.this.startActivity(intent);
                        finish();


                    }
                    else {
                        Toast toast =Toast.makeText(MainActivity.this, "wrong password, try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                else{
                    Toast toast =Toast.makeText(MainActivity.this, "this email is not registered, please sign up first", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                MainActivity.this.startActivity(intent);
                finish();
            }
        });

    }
    private void apply_darkmode(boolean enable){
        if (enable){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            darkmode.setChecked(true);
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            darkmode.setChecked(false);
        }


    }
}