package com.example.notetaking;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.sarnava.textwriter.TextWriter;

import java.util.Locale;

public class Setting extends AppCompatActivity  {

    SwitchCompat toggleButton;

    TextView changeLang;

    Button restart;

    Toolbar toolbar;
    TextView enableFingerprint;






    public static  final  String shared_pref = "sharedPrefs";
    public static final String toggleBtn = "toggleBtn";


    SharedPreferences sharedPreferences;

    boolean result;
    Intent intent = new Intent();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        getSupportActionBar().setTitle(R.string.setting);
        setContentView(R.layout.activity_setting);


        setStatusBarColor();









        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);




        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));

        enableFingerprint = findViewById(R.id.enableFingerprint);

        toggleButton = findViewById(R.id.fingerprintBtn);

        restart = findViewById(R.id.restart);

        loadData();

//        toggleButton.setChecked(false);






        changeLang = findViewById(R.id.changeLanguage);

        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguages();
            }
        });



        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this,SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    saveData(true);
                    toggleButton.setChecked(true);
                    intent.putExtra("enable","yes");
                    setResult(1,intent);
                    finish();

                }
                else
                {
                    saveData(false);
                    toggleButton.setChecked(false);
                    Toast.makeText(Setting.this, "Please assign a fingerprint to keep secure app", Toast.LENGTH_SHORT).show();
                    intent.putExtra("disable","no");
                    setResult(7,intent);
                    finish();
                }
            }
        });



        int nightModeFlags =
                Setting.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = Setting.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = Setting.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.toolBackgroundColor));
                break;


        }
    }
















    public void saveData(boolean answer)
    {
        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(toggleBtn,answer);
        editor.apply();


    }





    public void loadData()
    {
        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        result = sharedPreferences.getBoolean(toggleBtn,false);
        toggleButton.setChecked(result);


    }
















        private void changeLanguages() {
            final String [] languages = {"English","Hindi"};
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting.this);
            alertDialog.setTitle("Select Language");
            alertDialog.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0)
                    {
                        setLocal("");
                        recreate();
                    }
                    else if (i == 1)
                    {
                        setLocal("hi");
                        recreate();
                    }
                }
            });

            alertDialog.create();
            alertDialog.show();
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
        Window window = Setting.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.toolBackgroundColor));
    }




















}