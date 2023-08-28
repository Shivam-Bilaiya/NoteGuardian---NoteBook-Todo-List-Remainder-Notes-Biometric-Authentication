package com.alphacreators.noteguardian.REMAINDERNOTES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.alphacreators.noteguardian.DRAWING.UpdateDrawingActivity;
import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class UpdateRemainder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    int Rid;
    byte[] RImage;
    Bitmap RBitmap;
    EditText URemainderTitle, URemainderDescription;
    BottomNavigationView URemainderBottomNavigationView;
    ImageView URemainderImage;
    TextView URemainder;
    FloatingActionButton URemainderSaveBtn;
    CoordinatorLayout coordinatorLayout;
    ImageView backImage, favouriteImage;
    boolean check = true;
    int defaultColor;
    int myYear, myMonth, myDay, myHour, myMinute;
    int nYear,nMonth,nDay,nHour,nMinute;
    boolean clicked = false;
    int color;
    boolean changeColor = false;
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
    private RadioGroup updateRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_remainder);

        setStatusBarColor();

        URemainderTitle = findViewById(R.id.URemainderTitle);
        URemainderDescription = findViewById(R.id.URemainderDescription);
        URemainderImage = findViewById(R.id.URemainderImage);
        URemainder = findViewById(R.id.URemainder);
        URemainderBottomNavigationView = findViewById(R.id.URemainderBottomNavigationView);
        URemainderSaveBtn = findViewById(R.id.URemainderSaveBtn);
        coordinatorLayout = findViewById(R.id.URemainderLayout);
        backImage = findViewById(R.id.URBackImage);
        favouriteImage = findViewById(R.id.URFavouriteNote);
        highRadioButton = findViewById(R.id.URNoteRadioButtonHigh);
        lowRadioButton = findViewById(R.id.URNoteRadioButtonLow);
        mediumRadioButton = findViewById(R.id.URNoteRadioButtonMedium);
        uPinnedNote = findViewById(R.id.URPinNote);
        uDrawingNote = findViewById(R.id.URDrawingNote);
        updateRadioGroup = findViewById(R.id.URNoteRadioGroup);
        updateDrawImage = findViewById(R.id.UDrawImage);

        nestedScrollView = findViewById(R.id.URScrollView);

        URemainderBottomNavigationView.setBackground(null);

        URemainderTitle.setBackground(null);
        URemainderDescription.setBackground(null);
        URemainder.setBackground(null);

        Objects.requireNonNull(getSupportActionBar()).hide();


        defaultColor = ContextCompat.getColor(this, R.color.noteBackgroundColor);

        URemainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateRemainder.this, UpdateRemainder.this, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        getData();

        URemainderBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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
                        ImagePicker.Companion.with(UpdateRemainder.this)
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
                    URemainderSaveBtn.setVisibility(View.GONE);
                } else {
                    URemainderSaveBtn.setVisibility(View.VISIBLE);
                }
                y = nestedScrollView.getScrollY();
            }
        });

        URemainderSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (URemainderTitle.getText().toString().isEmpty() || URemainderDescription.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateRemainder.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (URemainder.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateRemainder.this, "Please add remainder date", Toast.LENGTH_SHORT).show();
                } else {
                    saveNote();
                }
            }
        });




        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        favouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (favouriteImage.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dislike, null).getConstantState()) {
                    Toast.makeText(UpdateRemainder.this, "like note", Toast.LENGTH_SHORT).show();
                    favouriteImage.setImageResource(R.drawable.lover);
                } else {
                    Toast.makeText(UpdateRemainder.this, "dislike note", Toast.LENGTH_SHORT).show();
                    favouriteImage.setImageResource(R.drawable.dislike);
                }
            }
        });

        uPinnedNote.setOnClickListener(v -> {
            if (uPinnedNote.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.pin, null).getConstantState()) {
                Toast.makeText(UpdateRemainder.this, "Pinned Note", Toast.LENGTH_SHORT).show();
                uPinnedNote.setImageResource(R.drawable.pin_note);
            } else {
                Toast.makeText(UpdateRemainder.this, "Unpinned Note", Toast.LENGTH_SHORT).show();
                uPinnedNote.setImageResource(R.drawable.pin);
            }

        });

        updateRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.URNoteRadioButtonHigh) {
                priority = Priority.HIGH;
            } else if (i == R.id.URNoteRadioButtonMedium) {
                priority = Priority.MEDIUM;
            } else if (i == R.id.URNoteRadioButtonLow) {
                priority = Priority.LOW;
            }
        });

        uDrawingNote.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateRemainder.this, UpdateDrawingActivity.class);
            if (drawImageUri != null){
                intent.putExtra("drawImageUri",drawImageUri.toString());
            }
            activityResultLauncherForUpdateDrawingNote.launch(intent);
        });


        updateDrawImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UpdateRemainder.this);
                alert.setTitle(getString(R.string.delete_Image)).setMessage(getString(R.string.Are_you_sure_to_delete_image)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDrawImage.setImageBitmap(null);
                        updateDrawImage.setImageDrawable(null);
                    }
                }).setNegativeButton(getString(R.string.no),(dialogInterface, i)->{

                });
                return true;
            }
        });

        URemainderImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(UpdateRemainder.this);
                alert.setTitle(getString(R.string.delete_Image)).setMessage(getString(R.string.Are_you_sure_to_delete_image)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        URemainderImage.setImageBitmap(null);
                        URemainderImage.setImageDrawable(null);
                    }
                }).setNegativeButton(getString(R.string.no),(dialogInterface, i)->{

                });
                return true;
            }
        });


        registerForActivityResultUpdateDrawNote();


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myMonth = month;
        myDay = dayOfMonth;
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateRemainder.this, UpdateRemainder.this, hour, minute, false);
        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        myMonth = myMonth + 1;
        URemainder.setText(myDay + "/" + myMonth + "/" + myYear + " " + myHour + ":" + myMinute);
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
        String title = URemainderTitle != null ? URemainderDescription.getText().toString() : "";
        String description = URemainderTitle != null ? URemainderDescription.getText().toString() : "";
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

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                speechToText();
            }
        }

        if (requestCode == 3){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sharePermission();
            }
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
            if (data != null){
                ArrayList<String> speakResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                URemainderDescription.setText(speakResult.get(0));
            }

        }




        if (data != null){
            Uri uri = data.getData();

            if (uri != null) {
                updateImageUri = uri;
                URemainderImage.setImageURI(uri);
                try {
                    RBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    URemainderImage.setImageBitmap(RBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    private void saveNote() {
        String noteTitle = URemainderTitle.getText().toString();
        String noteDescription = URemainderDescription.getText().toString();

        Intent intent = new Intent();

        String remainderDate;
        if (clicked){
            remainderDate = myDay +"/" + myMonth + "/" + myYear + " " + myHour +":" + myMinute;
        }else{
            Log.d("klkefbewjbew",URemainder.getText().toString());
            remainderDate = URemainder.getText().toString();
        }

        intent.putExtra("favourite", favouriteImage.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.dislike, null).getConstantState());

        intent.putExtra("pinnedNote", uPinnedNote.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.pin, null).getConstantState());

        if (URemainderImage.getDrawable() != null) {
            intent.putExtra("RImage", updateImageUri.toString());
        } else {
            intent.putExtra("RImage", "");
        }
        if (updateDrawImage.getDrawable() != null){
            intent.putExtra("RDrawImage",drawImageUri.toString());
        }else{
            intent.putExtra("RDrawImage","");
        }

        intent.putExtra("URNoteTitle", noteTitle);
        intent.putExtra("URNoteDescription", noteDescription);
        intent.putExtra("URNoteDate", remainderDate);
        intent.putExtra("URNotePriority",priority.toString());

        if (changeColor){
            intent.putExtra("UColor",defaultColor);
        }else {
            intent.putExtra("UColor", color);
        }


        if (Rid != -1) {
            intent.putExtra("noteId", Rid);
            setResult(RESULT_OK, intent);
            finish();
        }


    }

    private void getData() {
        Intent intent = getIntent();
        Rid = intent.getIntExtra("RNoteId", -1);
        String RTitle = intent.getStringExtra("RNoteTitle");
        String RDescription = intent.getStringExtra("RNoteDescription");
        String imageUri = intent.getStringExtra("RImage");
        String drawUri = intent.getStringExtra("RDrawImageUri");
        String date = intent.getStringExtra("RDate");
        color = intent.getIntExtra("RBackgroundColor", R.color.noteBackgroundColor);
        boolean favourite = intent.getBooleanExtra("RFavouriteNote", false);
        boolean isPinnedNote = intent.getBooleanExtra("RPinnedNote",false);
        URemainderTitle.setText(RTitle);
        URemainderDescription.setText(RDescription);
        URemainder.setText(date);
        coordinatorLayout.setBackgroundColor(color);

        if (!Objects.equals(imageUri, "")) {
            updateImageUri = Uri.parse(imageUri);
            URemainderImage.setImageURI(updateImageUri);
        }
        if (!Objects.equals(drawUri,"")){
            drawImageUri = Uri.parse(drawUri);
            updateDrawImage.setImageURI(drawImageUri);
        }

        if (favourite) {
            favouriteImage.setImageResource(R.drawable.lover);
        } else {
            favouriteImage.setImageResource(R.drawable.dislike);
        }

        if (isPinnedNote) {
            uPinnedNote.setImageResource(R.drawable.pin_note);
        } else {
            uPinnedNote.setImageResource(R.drawable.pin);
        }

        String userNotePriority = intent.getStringExtra("RPriority");
        priority = Priority.valueOf(userNotePriority);
        if ("HIGH".equals(userNotePriority)) {
            highRadioButton.setChecked(true);
        } else if ("MEDIUM".equals(userNotePriority)) {
            mediumRadioButton.setChecked(true);
        } else if ("LOW".equals(userNotePriority)) {
            lowRadioButton.setChecked(true);
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
                coordinatorLayout.setBackgroundColor(color);
                URemainderTitle.setBackgroundColor(color);
                URemainderDescription.setBackgroundColor(color);
            }

            @Override
            public void onCancel() {
                // put code
            }
        });


        return defaultColor;

    }



    public void setStatusBarColor() {
        int nightModeFlags =
                UpdateRemainder.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = UpdateRemainder.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(UpdateRemainder.this, R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = UpdateRemainder.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(UpdateRemainder.this, R.color.toolBackgroundColor));
                break;


        }
    }

    private void registerForActivityResultUpdateDrawNote(){
        activityResultLauncherForUpdateDrawingNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null && result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                String uri = data.getStringExtra("drawImageUri");
                if (uri!=null && !uri.isEmpty()){
                    drawImageUri = Uri.parse(uri);
                    updateDrawImage.setImageURI(Uri.parse(uri));
                }else{
                    updateDrawImage.setImageURI(null);
                }
            }
        });
    }
}