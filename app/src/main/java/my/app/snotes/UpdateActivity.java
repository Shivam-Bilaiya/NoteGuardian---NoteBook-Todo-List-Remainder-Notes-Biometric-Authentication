package my.app.snotes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {


    EditText updateTitle , updateDescription;

    FloatingActionButton save;

    int noteId;

    BottomNavigationView bottomNavigationView;

    ImageView updateImageNote;

    Bitmap bitmap;

    Bitmap scaleImage;

    byte [] image;


    Bitmap shareBitmap;
    int check = 0;

    Toolbar toolbar;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_update);

        setStatusBarColor();



        getSupportActionBar().setTitle(R.string.update_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);







        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));






        updateImageNote = findViewById(R.id.updateImageNote);

        updateTitle = findViewById(R.id.updateTextTitle);
        updateDescription = findViewById(R.id.updateTextDescription);
        save = findViewById(R.id.saveBtn);
//        cancel = findViewById(R.id.updateCancel);



        getData();



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (updateTitle.getText().toString().isEmpty() || updateDescription.getText().toString().isEmpty()){
                    Toast.makeText(UpdateActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else{
                    updateNote();
                }

            }
        });









        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(UpdateActivity.this,MainActivity.class));
                        break;
                    case R.id.voice:
                        checkForPermission();
                        break;
                    case R.id.photoTaken:
                        ImagePicker.Companion.with(UpdateActivity.this)
                                .crop()
                                .cropSquare()
                                .compress(1024)
                                .maxResultSize(1080,1080)
                                .start();
                        break;




                    case R.id.share:
                        sharePermission();
                        break;
                }

                return true;
            }
        });




        updateImageNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(updateImageNote !=null)
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(UpdateActivity.this);
                    alert.setTitle("Delete Image").setMessage("Are you sure to delete image").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateImageNote.setImageBitmap(null);
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
                UpdateActivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = UpdateActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(UpdateActivity.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = UpdateActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(UpdateActivity.this,R.color.toolBackgroundColor));
                break;


        }








    }

    private void sharePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,updateTitle.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_TEXT,updateDescription.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},3);
            }
        }
        else
        {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,updateTitle.getText().toString());
            sharingIntent.putExtra(Intent.EXTRA_TEXT,updateDescription.getText().toString());
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

        if (requestCode == 3)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,updateTitle.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_TEXT,updateDescription.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        }
    }



    private void updateNote() {

        String titleLast = updateTitle.getText().toString();
        String descriptionLast = updateDescription.getText().toString();



        Intent intent = new Intent();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy | HH:mm:ss");
        String formattedDate = sdf.format(new Date());

//        PreferenceManager.getDefaultSharedPreferences(UpdateActivity.this).edit().putString("updateStoreDate",formattedDate).apply();


        bitmap = ((BitmapDrawable) updateImageNote.getDrawable()).getBitmap();


        BitmapDrawable bitmapDrawable = (BitmapDrawable) updateImageNote.getDrawable();
        shareBitmap = bitmapDrawable.getBitmap();

        if (bitmap == null)
        {
            intent.putExtra("image",new byte[0]);
        }
        if (bitmap!=null )
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            scaleImage = makeSmall(bitmap,300);
            scaleImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
            image = byteArrayOutputStream.toByteArray();
            intent.putExtra("image",image);
        }


//        String UpdateCurrentDate = PreferenceManager.getDefaultSharedPreferences(UpdateActivity.this).getString("updateStoreDate", "");

        intent.putExtra("titleLast",titleLast);
        intent.putExtra("descriptionLast",descriptionLast);
        intent.putExtra("updateCurrentDate",formattedDate);









        if(noteId != -1)
        {
                intent.putExtra("noteId",noteId);
                setResult(RESULT_OK,intent);
            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                finish();
        }


    }

    public Bitmap makeSmall(Bitmap image,int maxSize)
    {
        if(image == null)
        {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = (float) width / (float) height;

        if (ratio > 1)
        {
            width = maxSize;
            height = (int)( width/ratio);
        }
        else
        {
            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(image,width, height,true);
    }



    private void getData()
    {
        Intent intent = getIntent();
        noteId = intent.getIntExtra("id",-1);
        String noteTitle = intent.getStringExtra("title");
        String noteDescription = intent.getStringExtra("description");
        Bundle extras = getIntent().getExtras();
        image = extras.getByteArray("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(image,0,image.length);
        updateTitle.setText(noteTitle);
        updateDescription.setText(noteDescription);
        updateImageNote.setImageBitmap(bmp);
    }


    public void speechToText()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            ArrayList<String> speakResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            updateDescription.setText(speakResult.get(0));
        }

        Uri uri = data.getData();
        if(uri!=null)
        {
            updateImageNote.setImageURI(uri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                updateImageNote.setImageBitmap(bitmap);
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
        Window window = UpdateActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(UpdateActivity.this,R.color.toolBackgroundColor));
    }













}