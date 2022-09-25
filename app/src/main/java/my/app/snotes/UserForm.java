package my.app.snotes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class UserForm extends AppCompatActivity {

    EditText firstName,userDOB,lastName;
    Button started;

    ConstraintLayout constraintLayout;

    private final String ChannelId = "1";

    DatePickerDialog.OnDateSetListener listener;

    TextView heading;

    ActivityResultLauncher<Intent> activityResultLauncherUserFormFinger;



    private static final String shared_pref_name = "myPref";
    private static final String userFirstName = "firstName";
    private static final String userLastName = "lastName";


    String nameFirst;
    String nameLast;




    boolean res;




    SharedPreferences prefs;

    int mDay,mYear,mMonth;


    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;



    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_user_form);


        setStatusBarColor();
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));



        firstName = findViewById(R.id.uFname);
        lastName = findViewById(R.id.uLname);
        heading = findViewById(R.id.heading);

        started = findViewById(R.id.screenLockBtn);
        prefs = getSharedPreferences(shared_pref_name,MODE_PRIVATE);
        started.setTextColor(getResources().getColor(R.color.headerTextColor));




































        nameFirst = firstName.getText().toString();
        nameLast = lastName.getText().toString();





            started.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()){
                        Toast.makeText(UserForm.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    } else {
                        nameFirst = firstName.getText().toString();
                        nameLast = lastName.getText().toString();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(userFirstName, nameFirst);
                        editor.putString(userLastName, nameLast);
                        editor.apply();


                        startActivity(new Intent(UserForm.this, MainActivity.class));
                        finish();


                    }
                }
            });

        int nightModeFlags =
                UserForm.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = UserForm.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(UserForm.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = UserForm.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(UserForm.this,R.color.toolBackgroundColor));
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
        Window window = UserForm.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(UserForm.this,R.color.toolBackgroundColor));
    }



}