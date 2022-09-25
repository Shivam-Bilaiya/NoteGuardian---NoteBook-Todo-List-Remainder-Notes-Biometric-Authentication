package my.app.snotes;

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

import java.util.Locale;

public class Setting extends AppCompatActivity  {

    SwitchCompat toggleButton;



    TextView changeLang;


    ImageView down;

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





        loadData();







        down = findViewById(R.id.down);
        changeLang = findViewById(R.id.changeLanguage);

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
                            Toast.makeText(Setting.this, "No biometric features available on this device.", Toast.LENGTH_SHORT).show();
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                            Toast.makeText(Setting.this, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show();
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                            // Prompts the user to create credentials that your app accepts.
                            Toast.makeText(Setting.this, "Enroll a fingerprint to make secure your app", Toast.LENGTH_SHORT).show();

                            break;
                    }

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
                            startActivity(new Intent(Setting.this,SplashScreen.class));
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
                            setLocal("ur");
                            startActivity(new Intent(Setting.this,SplashScreen.class));
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

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(Setting.this,R.color.toolBackgroundColor));
    }




















}