package com.example.CourseProject_1200166_1200711;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.CourseProject_1200166_1200711.databinding.ActivityHomeBinding;


public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    SharedPrefManager sharedPrefManager;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseHelper =new DataBaseHelper(HomeActivity.this,"DB_PROJECT",null,1);

        sharedPrefManager =SharedPrefManager.getInstance(this);
        boolean dark = sharedPrefManager.readboolean("enable", false);
        apply_darkmode(dark);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPrefManager.writeboolean("enable", !dark);
                boolean isdark = sharedPrefManager.readboolean("enable", false);
                apply_darkmode(isdark);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_today, R.id.nav_new_task, R.id.nav_all, R.id.nav_completed, R.id.nav_search, R.id.nav_profile, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        NavigationView navView = findViewById(R.id.nav_view);
        View headerLayout = navView.getHeaderView(0);
        TextView headerEmail = headerLayout.findViewById(R.id.header_email);
        headerEmail.setText(sharedPrefManager.readString("email", "No val"));

        DataBaseHelper dataBaseHelper=new DataBaseHelper(this,"DB_PROJECT",null,1);
        Cursor user = dataBaseHelper.getProfile(headerEmail.getText().toString());
        String name = " ";
        if (user != null && user.moveToFirst()) {
            name = user.getString(1) + " " + user.getString(2);
            user.close();
        }
        TextView name_txt = headerLayout.findViewById(R.id.name_text);
        name_txt.setText(name);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    private void apply_darkmode(boolean enable) {
        if (enable) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}