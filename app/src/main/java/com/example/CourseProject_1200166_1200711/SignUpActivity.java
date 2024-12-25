package com.example.CourseProject_1200166_1200711;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {
    EditText email, fname, lname, pass, confirm;
    DataBaseHelper dataBaseHelper;
    TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dataBaseHelper =new DataBaseHelper(SignUpActivity.this,"DB_PROJECT",null,1);

        email = (EditText) findViewById(R.id.email);
        fname = (EditText) findViewById(R.id.first_name);
        lname = (EditText) findViewById(R.id.second_name);
        pass = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.confirm_password);
        Button register = (Button) findViewById(R.id.register_button);
        signIn = (TextView) findViewById(R.id.signin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate_info() == true){
                    //add to database
                    dataBaseHelper.add_user(email.getText().toString(), fname.getText().toString(), lname.getText().toString(), pass.getText().toString());


                    //intent to go to the program main page

                    Intent intent=new Intent(SignUpActivity.this,HomeActivity.class);
                    SignUpActivity.this.startActivity(intent);
                    finish();
                }



            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }



    // Email validation method
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean validate_info(){
        if (!isValidEmail(String.valueOf(email.getText()))) {
            email.setError("Invalid email format");
            return false;
        }
        if (fname.length() < 5 || fname.length() > 20){
            fname.setError("name should be more than 5 characters and less than 20");
            return false;
        }
        if (lname.length() < 5 || lname.length() > 20){
            lname.setError("name should be more than 5 characters and less than 20");
            return false;
        }
        String pass_pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$";
        if (!pass.getText().toString().matches(pass_pattern)) {
            pass.setError("password must be 6-12 chars, include a number, a lowercase, and an uppercase letter.");
            return false;
        }
        if (!confirm.getText().toString().equals(pass.getText().toString())){
            confirm.setError("doesn't match your password");
            return false;
        }
        return true;
    }
}