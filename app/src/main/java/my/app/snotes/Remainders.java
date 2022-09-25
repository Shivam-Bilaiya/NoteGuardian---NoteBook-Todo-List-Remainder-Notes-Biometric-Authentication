package my.app.snotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Remainders extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText remainderTitle;
    EditText remainderDescription;

    BottomNavigationView remainderBottomNavigationView;

    FloatingActionButton remainderSaveBtn;

    BottomAppBar bottomAppBar;

    ImageView remainderImage;

    Bitmap bitmap;

    Bitmap scaleImage;

    byte[] image;

    TextView remainder;

    int day , month,year,hour,minute;
    int myDay,myMonth,myYear,myHour,myMinute;

    Calendar calendar = Calendar.getInstance();



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

        remainderBottomNavigationView.setBackground(null);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_remainder);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));

        setStatusBarColor();



        remainderSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (remainderTitle.getText().toString().isEmpty() || remainderDescription.getText().toString().isEmpty()) {
                    Toast.makeText(Remainders.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if (remainder.getText().toString().isEmpty()){
                    Toast.makeText(Remainders.this,"Please select remainder date and time",Toast.LENGTH_SHORT).show();

                } else
                {
                    saveNote();
                }

            }
        });


        remainder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Remainders.this, Remainders.this,year, month,day);
                datePickerDialog.show();

            }
        });

        remainderBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(Remainders.this, RemainderNotes.class));
                        break;
                    case R.id.voice:
                        checkForPermission();
                        break;
                    case R.id.photoTaken:
                        ImagePicker.Companion.with(Remainders.this)
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
            remainderDescription.setText(speakResult.get(0));
        }


        Uri uri = data.getData();

        if (uri != null) {
            remainderImage.setImageURI(uri);
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                remainderImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public void saveNote() {
        String noteTitle = remainderTitle.getText().toString();
        String noteDescription = remainderDescription.getText().toString();


        Intent intent = new Intent();

        String remainderDate = myYear +"/" + myMonth + "/" + myDay + "|" + myHour +":" + myMinute;
        if (remainderImage.getDrawable() != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            scaleImage = makeSmall(bitmap, 300);
            scaleImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            image = outputStream.toByteArray();
            intent.putExtra("RImage", image);
        } else {
            intent.putExtra("RImage", new byte[0]);
        }




        intent.putExtra("RNoteTitle", noteTitle);
        intent.putExtra("RNoteDescription", noteDescription);
        intent.putExtra("RNoteDate", remainderDate);


        setResult(RESULT_OK, intent);
        Toast.makeText(this, "Add Successfully", Toast.LENGTH_SHORT).show();
        finish();



        // for sending notification







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
        myDay = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Remainders.this, Remainders.this, hour, minute,false);
        timePickerDialog.show();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        myMonth = myMonth+1;
        remainder.setText(myYear + "/" + myMonth + "/" + myDay + " | " + myHour + ":" +myMinute);

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

}