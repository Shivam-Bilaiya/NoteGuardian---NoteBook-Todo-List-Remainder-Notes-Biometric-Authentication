package my.app.snotes;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.Locale;
import java.util.concurrent.Executor;


public class SplashScreen extends AppCompatActivity {



    TextView firstHeading;
    TextView secondHeading;



    String name;


    private static final String shared_pref_name = "myPref";
    private static final String userFirstName = "firstName";

    SharedPreferences prefs;

    public static  final  String shared_pref = "sharedPrefs";
    public static final String toggleBtn = "toggleBtn";


    public static final String pin_shared_pref = "pinSharedPrefs";
    public static final String pinLock1 = "pinLock";


    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;



    ImageView myImage;





    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_splash_screen);


        getSupportActionBar().setTitle(R.string.app_name);

        myImage = findViewById(R.id.myImage);





        firstHeading = findViewById(R.id.firstHeading);
        secondHeading = findViewById(R.id.secondHeading);



//        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressbar);
//        Sprite doubleBounce = new Wave();
//        progressBar.setIndeterminateDrawable(doubleBounce);


        setStatusBarColor();


        prefs = getSharedPreferences(shared_pref_name,MODE_PRIVATE);


         name = prefs.getString(userFirstName,null);


//        YoYo.with(Techniques.SlideInLeft).duration(2000).playOn(firstHeading);
//        YoYo.with(Techniques.SlideInLeft).duration(2000).playOn(secondHeading);
//        YoYo.with(Techniques.SlideInLeft).duration(2000).playOn(myImage);








        getSupportActionBar().hide();





//        myImage = findViewById(R.id.myImage);
//        myImage.startAnimation(AnimationUtils.loadAnimation(SplashScreen.this,R.anim.my_animations));




        if(name == null)
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, UserForm.class));
//                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
            },2000);
        }


        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean(toggleBtn,false);

                if(name!=null)
                {
                    if(result)
                    {
                        Executor executor = ContextCompat.getMainExecutor(SplashScreen.this);
                        biometricPrompt = new BiometricPrompt(this,
                                executor, new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);

                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            }

                            @Override
                            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                            }
                        });

                        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Enter phone screen lock pattern,PIN,password or fingerprint").setDescription("Unlock SNotes").setDeviceCredentialAllowed(true).build();
                        biometricPrompt.authenticate(promptInfo);
                    }
                    else
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
//                                progressBar.setVisibility(View.INVISIBLE);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        },2000);

                    }

                }





        int nightModeFlags =
                SplashScreen.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = SplashScreen.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(SplashScreen.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                 window = SplashScreen.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(SplashScreen.this,R.color.toolBackgroundColor));
                break;


        }















    }




    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean(toggleBtn,false);

        if(name!=null)
        {
            if(result)
            {
                Executor executor = ContextCompat.getMainExecutor(SplashScreen.this);
                biometricPrompt = new BiometricPrompt(this,
                        executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Enter phone screen lock pattern,PIN,password or fingerprint").setDescription("Unlock SNotes").setDeviceCredentialAllowed(true).build();
                biometricPrompt.authenticate(promptInfo);
            }
            else
            {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this,MainActivity.class));
                        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                },2000);

            }

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

    }









}