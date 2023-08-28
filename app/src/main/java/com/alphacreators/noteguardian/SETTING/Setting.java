package com.alphacreators.noteguardian.SETTING;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;
import androidx.core.content.ContextCompat;

import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.SPLASHSCREEN.SplashScreen;

import java.util.Locale;
import java.util.Objects;

public class Setting extends AppCompatActivity  {

    SwitchCompat toggleButton;
    SwitchCompat switchCompat;
    TextView googleCalendar;
    TextView changeLang;
    ImageView down;
    TextView enableFingerprint;
    public static  final  String shared_pref = "sharedPrefs";
    public static final String toggleBtn = "toggleBtn";
    public static final String google_calendar_shared_pref = "googleCalendarSharedPref";
    public static final String google_calendar_toggleBtn = "googleCalendarToggleBtn";
    SharedPreferences sharedPreferences;
    SharedPreferences calendarSharedPreferences;
    boolean result;
    boolean check;
    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.setting);
        setContentView(R.layout.activity_setting);


        setStatusBarColor();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));

        enableFingerprint = findViewById(R.id.enableFingerprint);

        toggleButton = findViewById(R.id.fingerprintBtn);

        loadData();



        down = findViewById(R.id.down);
        changeLang = findViewById(R.id.changeLanguage);
        googleCalendar = findViewById(R.id.addToGoogleCalendar);
        switchCompat = findViewById(R.id.switchCompat);

        loadGoogleCalendarData();

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguages();
            }
        });






        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    BiometricManager biometricManager = BiometricManager.from(Setting.this);
                    switch (biometricManager.canAuthenticate()) {
                        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                            String message = getString(R.string.no_biometric_features_available_on_this_device);
                            Toast.makeText(Setting.this, message, Toast.LENGTH_SHORT).show();
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                            String m1 = getString(R.string.biometric_features_are_currently_unavailable);
                            Toast.makeText(Setting.this, m1, Toast.LENGTH_SHORT).show();
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                           String m2 = getString(R.string.enroll_a_fingerprint_to_make_secure_your_app);
                            Toast.makeText(Setting.this, m2, Toast.LENGTH_SHORT).show();
                            break;
                    }

                    saveData(true);
                    toggleButton.setChecked(true);
                    intent.putExtra("enable","yes");
                    setResult(1,intent);


                }
                else
                {
                    saveData(false);
                    toggleButton.setChecked(false);
                    String m3 = getString(R.string.please_assign_a_fingerprint_to_keep_secure_app);
                    Toast.makeText(Setting.this, m3, Toast.LENGTH_SHORT).show();
                    intent.putExtra("disable","no");
                    setResult(7,intent);
                }
            }
        });



        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    addToGoogleCalendar(true);
                    switchCompat.setChecked(true);
                }else{
                    addToGoogleCalendar(false);
                    switchCompat.setChecked(false);
                }
            }
        });



        int nightModeFlags =
                Setting.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = Setting.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.background_color));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = Setting.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.background_color));
                break;


        }
    }

    private void addToGoogleCalendar(boolean b) {
        calendarSharedPreferences = getSharedPreferences(google_calendar_shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = calendarSharedPreferences.edit();
        editor.putBoolean(google_calendar_toggleBtn,b);
        editor.apply();

    }


    public void saveData(boolean answer)
    {
        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(toggleBtn,answer);
        editor.apply();


    }

    public void loadGoogleCalendarData(){
        calendarSharedPreferences = getSharedPreferences(google_calendar_shared_pref,MODE_PRIVATE);
        check = calendarSharedPreferences.getBoolean(google_calendar_toggleBtn,false);
        switchCompat.setChecked(check);
    }





    public void loadData()
    {
        sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        result = sharedPreferences.getBoolean(toggleBtn,false);
        toggleButton.setChecked(result);


    }
















        private void changeLanguages() {
//            final String[] languages = {"English","Hindi"};
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting.this);
//            alertDialog.setTitle("Select Language");
//            alertDialog.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if (i == 0)
//                    {
//                        setLocal("");
//
//                    }
//                    else if (i == 1)
//                    {
//                        setLocal("hi");
//
//                    }
//                }
//            });
//            alertDialog.create();
//            alertDialog.show();


            PopupMenu popupMenu = new PopupMenu(getApplicationContext(),changeLang);
            popupMenu.inflate(R.menu.language_pop_menu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.item1:
                            setLocal("en");
                            startActivity(new Intent(Setting.this, SplashScreen.class));
                            return true;
                        case R.id.item2:
                            setLocal("hi");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item3:
                            setLocal("mr");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item4:
                            setLocal("bn");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item5:
                            setLocal("pa");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item6:
                            setLocal("ta");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item7:
                            setLocal("te");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item8:
                            setLocal("ml");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item9:
                            setLocal("as");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item10:
                            setLocal("tr");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item11:
                            setLocal("ru");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item12:
                            setLocal("ja");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        case R.id.item13:
                            setLocal("ur");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
                            return true;
                        default:
                            return false;
                    }

                }
            });



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
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.background_color));
    }




















}