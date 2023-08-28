package com.alphacreators.noteguardian.REMAINDERNOTES;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.alphacreators.noteguardian.DRAWING.DrawingActivity;
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

public class Remainders extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText remainderTitle;
    EditText remainderDescription;
    BottomNavigationView remainderBottomNavigationView;
    FloatingActionButton remainderSaveBtn;
    ImageView remainderImage;
    Bitmap bitmap;
    TextView remainder;
    int year,month,day,hour,minute;
    int myDay,myMonth,myYear,myHour,myMinute;
    boolean check = false;
    int defaultColor;
    ImageView favouriteNote,backImage,pinNote,drawNote;
    CoordinatorLayout coordinatorLayout;
    ScrollView nestedScrollView;
    boolean pinnedNote = false;
    private RadioGroup noteRadioGroup;
    Priority priority;
    private Uri remainderImageUri;
    private ActivityResultLauncher<Intent> activityResultLauncherForRemainderDrawNote;
    private ImageView RDrawImage;
    private Uri remainderDrawImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainders);

        remainderTitle = findViewById(R.id.remainderTitle);
        remainderDescription = findViewById(R.id.remainderDescription);
        remainderBottomNavigationView = findViewById(R.id.remainderBottomNavigationView);
        remainderSaveBtn = findViewById(R.id.remainderSaveBtn);
        remainderImage = findViewById(R.id.remainderImage);
        remainder = findViewById(R.id.remainder);
        coordinatorLayout = findViewById(R.id.remainderLayout);
        favouriteNote = findViewById(R.id.RFavouriteNote);
        backImage = findViewById(R.id.RBackImage);
        pinNote = findViewById(R.id.RPinNote);
        drawNote = findViewById(R.id.RDrawingNote);
        nestedScrollView = findViewById(R.id.RScrollView);
        noteRadioGroup = findViewById(R.id.RNoteRadioGroup);
        RDrawImage = findViewById(R.id.RDrawImage);

        defaultColor = ContextCompat.getColor(this, R.color.noteBackgroundColor);

        remainderBottomNavigationView.setBackground(null);

       Objects.requireNonNull(getSupportActionBar()).hide();

        setStatusBarColor();



        remainderSaveBtn.setOnClickListener(view -> {
            if (remainderTitle.getText().toString().isEmpty() || remainderDescription.getText().toString().isEmpty()) {

                Toast.makeText(Remainders.this, getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show();
            }else if (remainder.getText().toString().isEmpty()){
                Toast.makeText(Remainders.this,getString(R.string.please_add_remainder_date),Toast.LENGTH_SHORT).show();

            } else
            {
                saveNote();
            }

        });


        remainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Remainders.this,Remainders.this,year,month,day);
                datePickerDialog.show();

            }
        });

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            float y = 0;

            @Override
            public void onScrollChanged() {
                if (nestedScrollView.getScrollY() > y) {
                    remainderSaveBtn.setVisibility(View.GONE);
                } else {
                    remainderSaveBtn.setVisibility(View.VISIBLE);
                }
                y = nestedScrollView.getScrollY();
            }
        });

        remainderBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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
                        ImagePicker.Companion.with(Remainders.this)
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
            }
        });

        favouriteNote.setOnClickListener(view -> {
            if (favouriteNote.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.dislike).getConstantState()){
                Toast.makeText(Remainders.this, getString(R.string.likenotes), Toast.LENGTH_SHORT).show();
                favouriteNote.setImageResource(R.drawable.lover);
                check = true;
            }else{
                Toast.makeText(Remainders.this, getString(R.string.dislike_note), Toast.LENGTH_SHORT).show();
                favouriteNote.setImageResource(R.drawable.dislike);
            }

        });

        pinNote.setOnClickListener(v -> {
            if (pinNote.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.pin,null).getConstantState()){
                Toast.makeText(Remainders.this,getString(R.string.pinnedNote),Toast.LENGTH_SHORT).show();
                pinNote.setImageResource(R.drawable.pin_note);
                pinnedNote = true;
            }else{
                Toast.makeText(Remainders.this,getString(R.string.unpinnedNote),Toast.LENGTH_SHORT).show();
                pinNote.setImageResource(R.drawable.pin);
            }
        });

        noteRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.RNoteRadioButtonHigh){
                priority = Priority.HIGH;
            }else if (i == R.id.RNoteRadioButtonMedium){
                priority = Priority.MEDIUM;
            }else if (i == R.id.RNoteRadioButtonLow){
                priority = Priority.LOW;
            }
        });

        drawNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Remainders.this, DrawingActivity.class);
                activityResultLauncherForRemainderDrawNote.launch(intent);
            }
        });

        RDrawImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Remainders.this);
                alert.setTitle(getString(R.string.delete_Image)).setMessage(getString(R.string.Are_you_sure_to_delete_image)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RDrawImage.setImageBitmap(null);
                        RDrawImage.setImageDrawable(null);
                    }
                }).setNegativeButton(getString(R.string.no),(dialogInterface, i)->{

                });
                return true;
            }
        });

        remainderImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Remainders.this);
                alert.setTitle(getString(R.string.delete_Image)).setMessage(getString(R.string.Are_you_sure_to_delete_image)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remainderImage.setImageBitmap(null);
                        remainderImage.setImageDrawable(null);
                    }
                }).setNegativeButton(getString(R.string.no),(dialogInterface, i)->{

                });
                return true;
            }
        });



        backImage.setOnClickListener(view -> finish());

        registerForActivityResultForDrawNote();

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
                coordinatorLayout.setBackgroundColor(color);
                remainderTitle.setBackgroundColor(color);
                remainderDescription.setBackgroundColor(color);
            }

            @Override
            public void onCancel() {
                // put code
            }
        });


        return defaultColor;

    }

    private void checkForPermission() {
        if(getApplicationContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {
            speechToText();
        }
        else
        {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
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
            remainderDescription.setText(speakResult.get(0));
        }


        assert data != null;
        if (data.getData() != null){
            Uri uri = data.getData();

            if (uri != null) {
                remainderImage.setImageURI(uri);
                remainderImageUri = uri;
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    remainderImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public void saveNote() {
        String noteTitle = remainderTitle.getText().toString();
        String noteDescription = remainderDescription.getText().toString();
        if (noteRadioGroup.getCheckedRadioButtonId() == -1){
            priority = Priority.LOW;
        }
        Intent intent = new Intent();

        String remainderDate = myDay +"/" + myMonth + "/" + myYear + " " + myHour +":" + myMinute;
        if (remainderImage.getDrawable() != null) {
            intent.putExtra("RImage", remainderImageUri.toString());
        } else {
            intent.putExtra("RImage","");
        }

        if (RDrawImage.getDrawable() != null){
            intent.putExtra("RDrawImage",remainderDrawImageUri.toString());
        }else{
            intent.putExtra("RDrawImage","");
        }

        intent.putExtra("favourite", check);

        intent.putExtra("pinnedNote", pinnedNote);

        intent.putExtra("RNoteTitle", noteTitle);
        intent.putExtra("RNoteDescription", noteDescription);
        intent.putExtra("RNoteDate", remainderDate);
        intent.putExtra("color", defaultColor);
        intent.putExtra("RNotePriority",priority.toString());
        setResult(RESULT_OK, intent);
        finish();



        // for sending notification







    }



    public void setStatusBarColor() {
        Window window = Remainders.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(Remainders.this, R.color.toolBackgroundColor));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myMonth = month;
        myDay = dayOfMonth;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY); // Use HOUR_OF_DAY for 24-hour format
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Remainders.this, Remainders.this, hour, minute, false);
        timePickerDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        myMonth = myMonth + 1;
        remainder.setText(myDay + "/" + myMonth + "/" + myYear + " " + myHour + ":" + myMinute);
    }


    private void sharePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, remainderTitle.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_TEXT,remainderDescription.getText().toString());
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
            sharingIntent.putExtra(Intent.EXTRA_TEXT, remainderTitle.getText().toString());
            sharingIntent.putExtra(Intent.EXTRA_TEXT,remainderDescription.getText().toString());
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
    }

    private void registerForActivityResultForDrawNote(){
        activityResultLauncherForRemainderDrawNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getData() != null && result.getResultCode() == RESULT_OK){
                    Intent data = result.getData();
                    String uri = data.getStringExtra("drawImageUri");
                    if (uri != null && !uri.isEmpty()) {
                        remainderDrawImageUri = Uri.parse(uri);
                        RDrawImage.setImageURI(Uri.parse(uri));
                    } else {
                        RDrawImage.setImageURI(null);
                    }
            }
        });
    }

}