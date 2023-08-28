package com.alphacreators.noteguardian.NOTES;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.alphacreators.noteguardian.DRAWING.DrawingActivity;
import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    BottomNavigationView bottomNavigationView;
    private FloatingActionButton saveBtn;
    BottomAppBar bottomAppBar;
    private ImageView addImageNote;
    private CoordinatorLayout addLayout;
    private boolean check = false;
    private int defaultColor;
    private ImageView favouriteNote;
    ImageView backImage;
    private ScrollView nestedScrollView;
    private RadioGroup noteRadioGroup;
    private Priority priority;
    private ImageView pinNote;
    private boolean pinnedNote = false;
    private Uri addImageUri;
    private ImageView drawImage;
    private String imageUri;
    private boolean remove = false;
    private boolean clean = false;
    private ActivityResultLauncher<Intent> activityResultLauncherForAddDrawing;
    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_add_note);


        defaultColor = ContextCompat.getColor(this, R.color.noteBackgroundColor);

        Objects.requireNonNull(getSupportActionBar()).hide();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        addImageNote = findViewById(R.id.addImageNote);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        saveBtn = findViewById(R.id.saveBtn);
        addLayout = findViewById(R.id.addLayout);
        favouriteNote = findViewById(R.id.favouriteNote);
        backImage = findViewById(R.id.backImage);
        nestedScrollView = findViewById(R.id.myScrollView);
        noteRadioGroup = findViewById(R.id.noteRadioGroup);
        pinNote = findViewById(R.id.pinNote);
        ImageView drawingNote = findViewById(R.id.drawingNote);
        drawImage = findViewById(R.id.drawImage);

        favouriteNote.setImageResource(R.drawable.dislike);




        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.colorPicker:
                    defaultColor = openColorPicker();
                    break;
                case R.id.voice:
                    checkForPermission();
                    break;
                case R.id.photoTaken:
                    ImagePicker.Companion.with(AddNoteActivity.this)
                            .crop()
                            .cropSquare()
                            .maxResultSize(1080, 1080)
                            .start();

                    break;

                case R.id.share:
                    sharePermission();
                    break;

            }
            return true;
        });


        saveBtn.setOnClickListener(view -> {
            if (editTextTitle.getText().toString().isEmpty() || editTextDescription.getText().toString().isEmpty()) {
                Toast.makeText(AddNoteActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                saveNote();
            }

        });


        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            float y = 0;

            @Override
            public void onScrollChanged() {
                if (nestedScrollView.getScrollY() > y) {
                    saveBtn.hide();  // Assuming you're using the hide() method for FloatingActionButton
                    bottomNavigationView.setVisibility(View.GONE);
                    bottomAppBar.setVisibility(View.GONE);
                } else {
                    saveBtn.show();  // Assuming you're using the show() method for FloatingActionButton
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    bottomAppBar.setVisibility(View.VISIBLE);
                }
                y = nestedScrollView.getScrollY();
            }
        });


        addImageNote.setOnLongClickListener(view -> {

            if (addImageNote != null) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddNoteActivity.this);
                alert.setTitle(getString(R.string.delete_Image)).setMessage(getString(R.string.Are_you_sure_to_delete_image)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addImageNote.setImageBitmap(null);
                        addImageNote.setImageDrawable(null);
                    }
                }).setNegativeButton(getString(R.string.no), (dialogInterface, i) -> {


                });

                alert.show();

            }


            return true;
        });

        drawImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                if (drawImage != null){
                    AlertDialog.Builder alert = new AlertDialog.Builder(AddNoteActivity.this);
                    alert.setTitle(getString(R.string.delete_Image)).setMessage(getString(R.string.Are_you_sure_to_delete_image)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            drawImage.setImageBitmap(null);
                            drawImage.setImageDrawable(null);
                        }
                    }).setNegativeButton(getString(R.string.no),(dialogInterface, i)->{

                    });

                    alert.show();
                }

                return true;
            }
        });



        changeStatusBarColor();



        registerForAddDrawingNote();


//        addLayout.setOnClickListener(Utils::hideSoftKeyboard);

        favouriteNote.setOnClickListener(view -> {
            if (favouriteNote.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dislike,null).getConstantState()){
                Toast.makeText(AddNoteActivity.this, "like note", Toast.LENGTH_SHORT).show();
                favouriteNote.setImageResource(R.drawable.lover);
                check = true;
            }else{
                Toast.makeText(AddNoteActivity.this, "dislike note", Toast.LENGTH_SHORT).show();
                favouriteNote.setImageResource(R.drawable.dislike);
            }

        });

        backImage.setOnClickListener(view -> finish());

        pinNote.setOnClickListener(view -> {
            if (pinNote.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.pin,null).getConstantState()){
                Toast.makeText(AddNoteActivity.this,"Pinned Note",Toast.LENGTH_SHORT).show();
                pinNote.setImageResource(R.drawable.pin_note);
                pinnedNote = true;
            }else{
                Toast.makeText(AddNoteActivity.this,"Unpinned Note",Toast.LENGTH_SHORT).show();
                pinNote.setImageResource(R.drawable.pin);
            }
        });

        noteRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.noteRadioButtonHigh){
                priority = Priority.HIGH;
            }else if (i == R.id.noteRadioButtonMedium){
                priority = Priority.MEDIUM;
            }else if (i == R.id.noteRadioButtonLow){
                priority = Priority.LOW;
            }
        });

        drawingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(AddNoteActivity.this, DrawingActivity.class);
               activityResultLauncherForAddDrawing.launch(intent);
            }
        });



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
                defaultColor = color;
                addLayout.setBackgroundColor(color);
                editTextTitle.setBackgroundColor(color);
                editTextDescription.setBackgroundColor(color);
            }

            @Override
            public void onCancel() {
                // put code
            }
        });


        return defaultColor;

    }

    private void sharePermission() {
        if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextTitle.getText().toString());
            sharingIntent.putExtra(Intent.EXTRA_TEXT, editTextDescription.getText().toString());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        if (noteRadioGroup.getCheckedRadioButtonId() == -1){
            priority = Priority.LOW;
        }

        Intent intent = new Intent();

        if (addImageNote.getDrawable() != null) {
            intent.putExtra("image", addImageUri.toString());
        }else{
            intent.putExtra("image","");
        }

        if (drawImage.getDrawable() != null){
            intent.putExtra("drawImage",imageUri);
        }else{
            intent.putExtra("drawImage","");
        }

        intent.putExtra("favourite", check);

        intent.putExtra("pinnedNote", pinnedNote);


        intent.putExtra("noteTitle", noteTitle);
        intent.putExtra("noteDescription", noteDescription);
        intent.putExtra("saveNoteDate", formattedDate);
        intent.putExtra("notePriority",priority.toString());
        intent.putExtra("color", defaultColor);

        setResult(RESULT_OK, intent);
        finish();
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


        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> speakResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editTextDescription.setText(speakResult.get(0));
        }
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                addImageNote.setImageURI(uri);
                addImageUri = uri;
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
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(AddNoteActivity.this, R.color.background_color));
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                window = AddNoteActivity.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(AddNoteActivity.this, R.color.toolBackgroundColor));
                break;


        }

    }

    public void registerForAddDrawingNote(){
        activityResultLauncherForAddDrawing = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getData() != null && result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                String uri = data.getStringExtra("drawImageUri");
                if (uri != null && !uri.isEmpty()) {
                    imageUri = uri;
                    drawImage.setImageURI(Uri.parse(uri));
                } else {
                    drawImage.setImageURI(null);
                }
            }

        });
    }
}

