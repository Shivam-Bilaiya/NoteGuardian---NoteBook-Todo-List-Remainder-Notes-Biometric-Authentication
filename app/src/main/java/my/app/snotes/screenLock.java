package my.app.snotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.drjacky.imagepicker.ImagePicker;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

public class screenLock extends AppCompatActivity {

    private static final String shared_pref_name = "myPref";
    private static final String userFirstName = "firstName";
    private static final String userLastName = "lastName";
    private static final String userDateOfBirth = "dateOfBirth";

    SharedPreferences prefs;

    EditText fName,lName;

    Button button;



    CircularImageView circularImageView;

    Bitmap bitmap;

    Bitmap scaleImage;

    byte[] image;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        setContentView(R.layout.activity_screen_lock);

        setStatusBarColor();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));



        getSupportActionBar().setTitle(R.string.user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fName = findViewById(R.id.screenLockFirstName);
        lName = findViewById(R.id.screenLockLastName);





        circularImageView = findViewById(R.id.circularImage);

        button = findViewById(R.id.screenLockBtn);

        fName.setKeyListener(null);
        lName.setKeyListener(null);


        prefs = getSharedPreferences(shared_pref_name,MODE_PRIVATE);

        String firstName = prefs.getString(userFirstName,null);
        String lastName = prefs.getString(userLastName,null);


        fName.setText(firstName);
        lName.setText(lastName);


        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
        String previouslyEncodedImage = shre.getString("image_data", "");

        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            circularImageView.setImageBitmap(bitmap);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bitmap = ((BitmapDrawable) circularImageView.getDrawable()).getBitmap();

                if (bitmap == null){
                    Toast.makeText(screenLock.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (bitmap!=null){
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    scaleImage = makeSmall(bitmap, 300);
                    scaleImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                    image = outputStream.toByteArray();

                    String encodedImage = Base64.encodeToString(image, Base64.DEFAULT);

                    SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(screenLock.this);
                    SharedPreferences.Editor edit=shre.edit();
                    edit.putString("image_data",encodedImage);
                    edit.commit();
                    Toast.makeText(screenLock.this, "Save Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }




            }
        });

        int nightModeFlags =
                screenLock.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = screenLock.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(screenLock.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = screenLock.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(screenLock.this,R.color.toolBackgroundColor));
                break;


        }


        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


    }

    private void selectImage() {
        ImagePicker.Companion.with(screenLock.this)
                .crop()
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Uri uri = data.getData();

        if (uri!=null){
            circularImageView.setImageURI(uri);
            try {

                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                circularImageView.setImageBitmap(bitmap);
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
        Window window = screenLock.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(screenLock.this,R.color.toolBackgroundColor));
    }

    public Bitmap makeSmall(Bitmap image, int maxSize) {

        if(image == null)
        {
            return null;
        }
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
}