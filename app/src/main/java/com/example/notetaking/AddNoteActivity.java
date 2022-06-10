package com.example.notetaking;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    EditText editTextTitle;
    EditText editTextDescription;

    BottomNavigationView bottomNavigationView;

    FloatingActionButton saveBtn;

    BottomAppBar bottomAppBar;

    ImageView addImageNote;

    Bitmap bitmap;

    Bitmap scaleImage;

    byte[] image;

    Toolbar toolbar;


    private final String CHANNEL_ID = "Big Text Notification";
    private final int NOTIFICATION_ID = 1;


    ActivityResultLauncher<Intent> activityResultLauncherForAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_add_note);





        getSupportActionBar().setTitle(R.string.add_new_note);

        setStatusBarColor();


//        toolbar = findViewById(R.id.addToolbar);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));


        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);


//        toolbar.setTitle(R.string.add_new_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        bottomAppBar = findViewById(R.id.bottomAppBar);

        addImageNote = findViewById(R.id.addImageNote);


        bottomNavigationView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(AddNoteActivity.this, MainActivity.class));
                        break;
                    case R.id.voice:
                        checkForPermission();
                        break;
                    case R.id.photoTaken:
                        ImagePicker.Companion.with(AddNoteActivity.this)
                                .crop()
                                .cropSquare()
                                .compress(1024)
                                .maxResultSize(1080, 1080)
                                .start();

                        break;

                    case R.id.share:
                        sharePermission();

                }


            }
        });


        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
//
        saveBtn = findViewById(R.id.saveBtn);

        editTextDescription.clearFocus();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextTitle.getText().toString().isEmpty() || editTextDescription.getText().toString().isEmpty()) {
                    Toast.makeText(AddNoteActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    saveNote();
                }

            }
        });


        addImageNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(addImageNote!=null)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AddNoteActivity.this);
                    alert.setTitle("Delete Image").setMessage("Are you sure to delete image").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addImageNote.setImageBitmap(null);
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    alert.show();

                }




                return true;
            }
        });


        int nightModeFlags =
                AddNoteActivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = AddNoteActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(AddNoteActivity.this, R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = AddNoteActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(AddNoteActivity.this, R.color.toolBackgroundColor));
                break;


        }

    }

    private void sharePermission() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, editTextDescription.getText().toString());
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
                else
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
                }
            }
            else
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, editTextDescription.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }




    private void checkForPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(getApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            {
                speechToText();
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
            }
        }
        else
        {
            speechToText();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                speechToText();
            }
        }

        if(requestCode == 3)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, editTextDescription.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void saveNote() {
        String noteTitle = editTextTitle.getText().toString();
        String noteDescription = editTextDescription.getText().toString();


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);

        PreferenceManager.getDefaultSharedPreferences(AddNoteActivity.this).edit().putString("storeDate", formattedDate).apply();


        Intent intent = new Intent();

        if (addImageNote.getDrawable() != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaleImage = makeSmall(bitmap, 300);
            scaleImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            image = outputStream.toByteArray();
            intent.putExtra("image", image);
        } else {
            intent.putExtra("image", new byte[0]);
        }


        String currentDate = PreferenceManager.getDefaultSharedPreferences(AddNoteActivity.this).getString("storeDate", "");


        intent.putExtra("noteTitle", noteTitle);
        intent.putExtra("noteDescription", noteDescription);
        intent.putExtra("saveNoteDate", currentDate);


        setResult(RESULT_OK, intent);
        finish();


    }

    public Bitmap makeSmall(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = (float) width / (float) height;

        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void speechToText() {


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent, 1);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<String> speakResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editTextDescription.setText(speakResult.get(0));
        }


        Uri uri = data.getData();
        if (uri != null) {
            addImageNote.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                addImageNote.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
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
        Window window = AddNoteActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(AddNoteActivity.this, R.color.toolBackgroundColor));
    }


}