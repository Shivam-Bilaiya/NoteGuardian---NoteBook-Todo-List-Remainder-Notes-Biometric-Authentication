package com.alphacreators.noteguardian.USER;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alphacreators.noteguardian.ENTITY.User;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class screenLock extends AppCompatActivity {

    private static final String shared_pref_name = "myPref";
    private static final String userFirstName = "firstName";
    private static final String userLastName = "lastName";
    SharedPreferences prefs;
    EditText fName, lName;
    Button button;
    private Uri userImageUri;
    ImageView circularImageView;
    Bitmap bitmap;
    Bitmap scaleImage;
    byte[] image;
    private NoteViewModel noteViewModel;
    private SharedPreferences userIdPref;
    private Button updateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        setContentView(R.layout.activity_screen_lock);

        setStatusBarColor();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));

        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(new Application()).create(NoteViewModel.class);

        getSupportActionBar().setTitle(R.string.user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fName = findViewById(R.id.screenLockFirstName);
        lName = findViewById(R.id.screenLockLastName);


        circularImageView = findViewById(R.id.userImage);

        button = findViewById(R.id.screenLockBtn);

        updateButton = findViewById(R.id.screenLockUpdateBtn);

        prefs = getSharedPreferences(shared_pref_name, MODE_PRIVATE);
        userIdPref = getSharedPreferences("USER_ID_PREF", Context.MODE_PRIVATE);

        getUserData();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            }
        });


        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        String previouslyEncodedImage = shre.getString("image_data", "");

        if (!previouslyEncodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            circularImageView.setImageBitmap(bitmap);
        }


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
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(screenLock.this, R.color.background_color));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = screenLock.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(screenLock.this, R.color.background_color));
                break;


        }


        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


    }

    private void updateUserData() {
        long userId = userIdPref.getLong("userId", -1);
        if (userId != -1) {
            Log.d("kenknwwknv", "knkrn" + userId);
            String name = fName.getText().toString();
            String lastName = lName.getText().toString();
            if (name.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(screenLock.this, "All Fields are required", Toast.LENGTH_SHORT).show();
            } else {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    Log.d("elonkenwekw", "knknfkewfnwe " + userId);

                    String imageUri = userImageUri != null ? userImageUri.toString() : "";

                    if (circularImageView.getDrawable().getConstantState() ==
                            getResources().getDrawable(R.drawable.ic_baseline_add_24, null).getConstantState()) {
                        imageUri = "";
                    }

                    User user = new User(name, lastName, imageUri);
                    user.setId(userId);
                    noteViewModel.updateUser(user);
                });

                // Shutdown the executor to release resources
                executor.shutdown();
                finish();
            }
        }
    }


    private void selectImage() {
        ImagePicker.Companion.with(screenLock.this)
                .crop()
                .cropSquare()
                .maxResultSize(1080, 1080)
                .start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (data.getData() != null) {
            Uri uri = data.getData();

            if (uri != null) {
                circularImageView.setImageURI(uri);
                userImageUri = uri;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    circularImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        Window window = screenLock.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(screenLock.this, R.color.background_color));
    }


    private void getUserData() {
        Log.d("klefnekfnekfbekfb", "kenfkenfknekfenfke");
        long userId = userIdPref.getLong("userId", -1);
        if (userId != -1L) {
            noteViewModel.getUserData(userId).observe(this, user -> {
                if (user != null) {
                    fName.setText(user.getFirstName());
                    lName.setText(user.getLastName());
                    if (user.getImageUri() != null && user.getImageUri().isEmpty()) {
                        circularImageView.setImageURI(null);
                    } else {
                        circularImageView.setImageURI(Uri.parse(user.getImageUri()));
                    }
                }
            });
        }
    }
}