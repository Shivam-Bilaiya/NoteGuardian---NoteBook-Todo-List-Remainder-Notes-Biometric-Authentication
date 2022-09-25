package my.app.snotes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_add_note);

//        getSupportActionBar().hide();

//        toolbar = findViewById(R.id.upperToolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_new_note);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));


        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);


        bottomAppBar = findViewById(R.id.bottomAppBar);

        addImageNote = findViewById(R.id.addImageNote);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        break;

                }
                return true;
            }
        });


        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
//
        saveBtn = findViewById(R.id.saveBtn);

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

                if (addImageNote != null) {
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


        changeStatusBarColor();


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.upper_toolbar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.saveNote){
//            Toast.makeText(this, "hii", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
//        }
//        return true;
//
//    }
    }

    private void sharePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextDescription.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
        } else {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextDescription.getText().toString());
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
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextDescription.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }



    public void saveNote() {
        String noteTitle = editTextTitle.getText().toString();
        String noteDescription = editTextDescription.getText().toString();


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy | HH:mm:ss");
        String formattedDate = sdf.format(new Date());


//        Date c = Calendar.getInstance().getTime();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
//        String formattedDate = simpleDateFormat.format(c);

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
        Toast.makeText(this, "Add Successfully", Toast.LENGTH_SHORT).show();
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
            assert data != null;
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void changeStatusBarColor() {
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
}

