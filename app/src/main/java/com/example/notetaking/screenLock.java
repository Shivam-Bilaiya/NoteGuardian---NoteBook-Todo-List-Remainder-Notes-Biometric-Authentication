package com.example.notetaking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;

public class screenLock extends AppCompatActivity {

    private static final String shared_pref_name = "myPref";
    private static final String userFirstName = "firstName";
    private static final String userLastName = "lastName";
    private static final String userDateOfBirth = "dateOfBirth";

    SharedPreferences prefs;

    EditText fName,lName;

    Button button;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_screen_lock);

        setStatusBarColor();



        getSupportActionBar().setTitle(R.string.user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fName = findViewById(R.id.screenLockFirstName);
        lName = findViewById(R.id.screenLockLastName);


        button = findViewById(R.id.screenLockBtn);

        fName.setKeyListener(null);
        lName.setKeyListener(null);


        prefs = getSharedPreferences(shared_pref_name,MODE_PRIVATE);

        String firstName = prefs.getString(userFirstName,null);
        String lastName = prefs.getString(userLastName,null);


        fName.setText(firstName);
        lName.setText(lastName);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int nightModeFlags =
                screenLock.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = screenLock.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(screenLock.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = screenLock.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(screenLock.this,R.color.toolBackgroundColor));
                break;


        }


    }

    private void setLocal(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());


        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("app_lang",language);

        editor.apply();
    }



    private void loadLocale()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = sharedPreferences.getString("app_lang","");
        setLocal(language);
    }

    public  void setStatusBarColor ()
    {
        Window window = screenLock.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(screenLock.this,R.color.toolBackgroundColor));
    }
}