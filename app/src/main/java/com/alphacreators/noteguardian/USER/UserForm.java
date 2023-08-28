package com.alphacreators.noteguardian.USER;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alphacreators.noteguardian.ENTITY.User;
import com.alphacreators.noteguardian.NOTES.MainActivity;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserForm extends AppCompatActivity {

    EditText firstName, lastName;
    Button started;
    NoteViewModel noteViewModel;
    private SharedPreferences userIdPref;
    String nameFirst;
    String nameLast;
    TextView signUp;
    SharedPreferences storeUserFirstNamePrefs;
    private static final String storeUserFirstNamePref = "STORE_USER_FIRST_NAME_PREFS";
    private static final String userFirstName = "firstName";

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_user_form);

        setStatusBarColor();
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor, null)));

        userIdPref = getSharedPreferences("USER_ID_PREF", Context.MODE_PRIVATE);
        storeUserFirstNamePrefs = getSharedPreferences(storeUserFirstNamePref,Context.MODE_PRIVATE);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        signUp = findViewById(R.id.signUp);

        SpannableString spannableString = new SpannableString(signUp.getText().toString());

        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, signUp.getText().toString().length(), 0);

        signUp.setText(spannableString);

        firstName = findViewById(R.id.uFname);
        lastName = findViewById(R.id.uLname);

        started = findViewById(R.id.screenLockBtn);
        started.setTextColor(getResources().getColor(R.color.headerTextColor, null));


        started.setOnClickListener(view -> {
            if (firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()) {
                Toast.makeText(UserForm.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                nameFirst = firstName.getText().toString();
                nameLast = lastName.getText().toString();
                storeUserFirstNamePrefs.edit().putString(userFirstName,nameFirst).apply();
                User user = new User(nameFirst, nameLast, "");
                addUserInDatabase(user);
            }

        });


        int nightModeFlags =
                UserForm.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = UserForm.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(UserForm.this, R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = UserForm.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(UserForm.this, R.color.background_color));
                break;


        }


    }

    private void setLocal(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());


        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("app_lang", language);

        editor.apply();
    }

    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("app_lang", "");
        setLocal(language);
    }

    public void setStatusBarColor() {
        Window window = UserForm.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(UserForm.this, R.color.background_color));
    }


    private void addUserInDatabase(User user) {
        Log.d("knfkwnfewkjfnw", "knfkewnfweklfnw");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            long user1 = noteViewModel.insertUser(user);
            if (user1 != 0L) {
                userIdPref.edit().putLong("userId", user1).apply();
                runOnUiThread(() -> {
                    Toast.makeText(UserForm.this, "Account is created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserForm.this, MainActivity.class));
                    finish();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(UserForm.this, "Oops Something Went Wrong", Toast.LENGTH_SHORT).show());
            }
        });
    }



}