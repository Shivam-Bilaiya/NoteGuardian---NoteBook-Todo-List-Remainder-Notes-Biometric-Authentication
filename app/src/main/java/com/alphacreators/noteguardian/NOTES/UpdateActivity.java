package com.alphacreators.noteguardian.NOTES;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.DRAWING.UpdateDrawingActivity;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class UpdateActivity extends AppCompatActivity {


    EditText updateTitle, updateDescription;
    FloatingActionButton save;
    int noteId;
    BottomNavigationView bottomNavigationView;
    ImageView updateImageNote;
    Bitmap bitmap;
    BottomAppBar bottomAppBar;
    byte[] image;
    Bitmap shareBitmap;
    int defaultColor;
    CoordinatorLayout updateLayout;
    ImageView imageView;
    ImageView backImage;
    boolean changeColor = false;
    int color;
    ScrollView nestedScrollView;
    private Priority priority;
    private RadioButton highRadioButton;
    private RadioButton mediumRadioButton;
    private RadioButton lowRadioButton;
    private ImageView uPinnedNote;
    private Uri updateImageUri;
    private Uri drawImageUri;
    private ImageView updateDrawImage;
    private ImageView uDrawingNote;
    private ActivityResultLauncher<Intent> activityResultLauncherForUpdateDrawingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_update);

        updateImageNote = findViewById(R.id.updateImageNote);
        updateTitle = findViewById(R.id.updateTextTitle);
        updateDescription = findViewById(R.id.updateTextDescription);
        save = findViewById(R.id.saveBtn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        updateLayout = findViewById(R.id.updateLayout);
        imageView = findViewById(R.id.uFavourite);
        backImage = findViewById(R.id.updateBack);
        nestedScrollView = findViewById(R.id.updateNestedView);
        RadioGroup updateRadioGroup = findViewById(R.id.updateRadioGroup);
        highRadioButton = findViewById(R.id.updateNoteRadioButtonHigh);
        mediumRadioButton = findViewById(R.id.updateNoteRadioButtonMedium);
        lowRadioButton = findViewById(R.id.updateNoteRadioButtonLow);
        updateDescription.setBackground(null);
        updateTitle.setBackground(null);
        uPinnedNote = findViewById(R.id.uPinNote);
        updateDrawImage = findViewById(R.id.updateDrawImageNote);
        uDrawingNote = findViewById(R.id.uDrawingNote);
        bottomAppBar = findViewById(R.id.UbottomAppBar);

//        updateImageNote.setBackground(getResources().getDrawable(R.drawable.rounded_border_corner_imageview,null));


        setStatusBarColor();

        defaultColor = ContextCompat.getColor(this, R.color.noteBackgroundColor);

        Objects.requireNonNull(getSupportActionBar()).hide();


        bottomNavigationView.setBackground(null);


//        cancel = findViewById(R.id.updateCancel);


        getData();

        registerForActivityResultForDrawingNote();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateTitle.getText().toString().isEmpty() || updateDescription.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    updateNote();
                }

            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.colorPicker:
                        defaultColor = openColorPicker();
                        break;
                    case R.id.voice:
                        checkForPermission();
                        break;
                    case R.id.photoTaken:
                        ImagePicker.Companion.with(UpdateActivity.this)
                                .crop()
                                .cropSquare()
                                .maxResultSize(1080, 1080)
                                .start();
                        break;


                    case R.id.share:
                        shareContent();
                        break;
                }

                return true;
            }
        });


        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            float y = 0;

            @Override
            public void onScrollChanged() {
                if (nestedScrollView.getScrollY() > y) {
                    save.hide();  // Assuming you're using the hide() method for FloatingActionButton
                    bottomNavigationView.setVisibility(View.GONE);
                    bottomAppBar.setVisibility(View.GONE);
                } else {
                    save.show();  // Assuming you're using the show() method for FloatingActionButton
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomAppBar.setVisibility(View.VISIBLE);
                }
                y = nestedScrollView.getScrollY();
            }
        });
        updateImageNote.setOnLongClickListener(view -> {

            if (updateImageNote != null) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UpdateActivity.this);
                alert.setTitle("Delete Image").setMessage("Are you sure to delete image").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateImageNote.setImageBitmap(null);
                        updateImageNote.setImageDrawable(null);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                alert.show();
            }


            return true;
        });

        updateDrawImage.setOnLongClickListener(view -> {

            if (updateDrawImage != null) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UpdateActivity.this);
                alert.setTitle("Delete Image").setMessage("Are you sure to delete image").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDrawImage.setImageBitmap(null);
                        updateDrawImage.setImageDrawable(null);
                    }
                }).setNegativeButton("No", (dialogInterface, i) -> {

                });

                alert.show();
            }


            return true;
        });

        imageView.setOnClickListener(view -> {

            if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dislike, null).getConstantState()) {
                Toast.makeText(UpdateActivity.this, "like note", Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.lover);
            } else {
                Toast.makeText(UpdateActivity.this, "dislike note", Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.dislike);
            }

        });


        uPinnedNote.setOnClickListener(view -> {
            if (uPinnedNote.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.pin, null).getConstantState()) {
                Toast.makeText(UpdateActivity.this, "Pinned Note", Toast.LENGTH_SHORT).show();
                uPinnedNote.setImageResource(R.drawable.pin_note);
            } else {
                Toast.makeText(UpdateActivity.this, "Unpinned Note", Toast.LENGTH_SHORT).show();
                uPinnedNote.setImageResource(R.drawable.pin);
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.updateNoteRadioButtonHigh) {
                priority = Priority.HIGH;
            } else if (i == R.id.updateNoteRadioButtonMedium) {
                priority = Priority.MEDIUM;
            } else if (i == R.id.updateNoteRadioButtonLow) {
                priority = Priority.LOW;
            }
        });

        uDrawingNote.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateActivity.this, UpdateDrawingActivity.class);
            if (drawImageUri != null) {
                Log.d("lwnfkfwkfw", drawImageUri.toString());
                intent.putExtra("drawImageUri", drawImageUri.toString());
            }
            activityResultLauncherForUpdateDrawingNote.launch(intent);
        });

    }


    private void sharePermission() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with sharing
            shareContent();
        } else {
            // Request the permission
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
    }

    private void shareContent() {
        String title = updateTitle != null ? updateTitle.getText().toString() : "";
        String description = updateDescription != null ? updateDescription.getText().toString() : "";
        String shareText = title + "\n" + description;

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }




    private void checkForPermission() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            speechToText();
        } else {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speechToText();
            }
        }

        if (requestCode == 3) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sharePermission();
            }
        }
    }

    private int openColorPicker() {
        ColorPicker colorPicker = new ColorPicker(this);
        ArrayList<String> colorList = new ArrayList<>();
        colorList.add("#EB4034");
        colorList.add("#ff8000");
        colorList.add("#0040ff");
        colorList.add("#00ffff");
        colorList.add("#660033");
        colorList.add("#ffcc66");
        colorList.add("#cc6699");
        colorList.add("#663300");
        colorList.add("#ccffff");
        colorList.add("#666699");
        colorList.add("#999966");
        colorList.add("#FCF6F5FF");
        colorList.add("#323232");
        colorList.add("#186a3b");
        colorList.add("#1abc9c");
        colorList.add("#003300");

        colorPicker.setColors(colorList);
        colorPicker.setColumns(4);
        colorPicker.setRoundColorButton(true);
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                changeColor = true;
                defaultColor = color;
                updateLayout.setBackgroundColor(color);
                updateTitle.setBackgroundColor(color);
                updateDescription.setBackgroundColor(color);
            }

            @Override
            public void onCancel() {
                // put code
            }
        });


        return defaultColor;

    }


    private void updateNote() {

        String titleLast = updateTitle.getText().toString();
        String descriptionLast = updateDescription.getText().toString();
        Intent intent = new Intent();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        if (updateImageNote.getDrawable() != null) {
            intent.putExtra("image", updateImageUri.toString());
        } else {
            intent.putExtra("image", "");
        }

        if (updateDrawImage.getDrawable() != null) {
            intent.putExtra("drawImage", drawImageUri.toString());
        } else {
            intent.putExtra("drawImage", "");
        }


        intent.putExtra("favourite", imageView.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.dislike, null).getConstantState());

        intent.putExtra("pinnedNote", uPinnedNote.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.pin, null).getConstantState());

        intent.putExtra("titleLast", titleLast);
        intent.putExtra("descriptionLast", descriptionLast);
        intent.putExtra("updateCurrentDate", formattedDate);
        intent.putExtra("updateNotePriority", priority.toString());
        if (changeColor) {
            intent.putExtra("color", defaultColor);
        } else {
            intent.putExtra("color", color);
        }


        if (noteId != -1) {
            intent.putExtra("noteId", noteId);
            setResult(RESULT_OK, intent);
            finish();
        }


    }


    private void getData() {
        Intent intent = getIntent();
        noteId = intent.getIntExtra("id", -1);
        String noteTitle = intent.getStringExtra("title");
        String noteDescription = intent.getStringExtra("description");
        String imageUri = intent.getStringExtra("image");
        color = intent.getIntExtra("color", R.color.noteBackgroundColor);
        boolean favourite = intent.getBooleanExtra("favourite", false);
        boolean isPinnedNote = intent.getBooleanExtra("pinnedNote", false);
        String drawUri = intent.getStringExtra("drawImageUri");
        updateTitle.setText(noteTitle);
        updateDescription.setText(noteDescription);
        if (!Objects.equals(imageUri, "")) {
            updateImageUri = Uri.parse(imageUri);
            updateImageNote.setImageURI(updateImageUri);
        }
        if (!Objects.equals(drawUri, "")) {
            drawImageUri = Uri.parse(drawUri);
            updateDrawImage.setImageURI(drawImageUri);
        }
        updateLayout.setBackgroundColor(color);
        if (favourite) {
            imageView.setImageResource(R.drawable.lover);
        } else {
            imageView.setImageResource(R.drawable.dislike);
        }

        String userNotePriority = intent.getStringExtra("notePriority");
        priority = Priority.valueOf(userNotePriority);
        if ("HIGH".equals(userNotePriority)) {
            highRadioButton.setChecked(true);
        } else if ("MEDIUM".equals(userNotePriority)) {
            mediumRadioButton.setChecked(true);
        } else if ("LOW".equals(userNotePriority)) {
            lowRadioButton.setChecked(true);
        }

        if (isPinnedNote) {
            uPinnedNote.setImageResource(R.drawable.pin_note);
        } else {
            uPinnedNote.setImageResource(R.drawable.pin);
        }


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
            updateDescription.setText(speakResult.get(0));
        }

        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                updateImageNote.setImageURI(uri);
                updateImageUri = uri;
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
        int nightModeFlags =
                UpdateActivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = UpdateActivity.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(UpdateActivity.this, R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = UpdateActivity.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(UpdateActivity.this, R.color.background_color));
                break;


        }
    }

    private void registerForActivityResultForDrawingNote() {
        activityResultLauncherForUpdateDrawingNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getData() != null && result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                String uri = data.getStringExtra("drawImageUri");
                if (uri != null && !uri.isEmpty()) {
                    drawImageUri = Uri.parse(uri);
                    updateDrawImage.setImageURI(Uri.parse(uri));
                } else {
                    updateDrawImage.setImageURI(null);
                }
            }

        });
    }


}