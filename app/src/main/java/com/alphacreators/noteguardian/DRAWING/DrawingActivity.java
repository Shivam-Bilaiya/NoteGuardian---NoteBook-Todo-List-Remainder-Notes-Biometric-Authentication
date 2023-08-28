package com.alphacreators.noteguardian.DRAWING;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.alphacreators.noteguardian.R;
import com.google.android.material.textview.MaterialTextView;
import com.kyanogen.signatureview.SignatureView;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class DrawingActivity extends AppCompatActivity {


    private SignatureView signatureView;
    private ImageButton eraser;
    private ImageButton colorPicker;
    private ImageButton done;
    private AppCompatSeekBar penSize;
    private MaterialTextView penSizeText;
    private int defaultColor;
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        signatureView = findViewById(R.id.signature_view);
        eraser = findViewById(R.id.eraser);
        colorPicker = findViewById(R.id.colorPickerImage);
        done = findViewById(R.id.saveDraw);
        penSize = findViewById(R.id.penSize);
        penSizeText = findViewById(R.id.penSizeText);
        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.white,null));
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);


        defaultColor = getResources().getColor(R.color.white, null);

        colorPicker.setOnClickListener(v -> openColorPickerForDrawing());

        penSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                penSizeText.setText(String.valueOf(progress));
                signatureView.setPenSize(progress);
                penSize.setMax(100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eraser.setOnLongClickListener(v -> {
            eraseWholeDrawing();
            return true;
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eraseTheDrawing();
            }
        });

        done.setOnClickListener(v -> {
            saveImageAndReturnUri();
        });
        



    }

    private void saveImageAndReturnUri() {
        Bitmap bitmap = signatureView.getSignatureBitmap();
        Uri uri = convertBitmapToUri(bitmap);
        String imageUri = uri.toString();
        Intent intent = new Intent();
        if (imageUri.isEmpty()){
            intent.putExtra("drawImageUri","");
        }else{
            intent.putExtra("drawImageUri",imageUri);
        }
        setResult(RESULT_OK,intent);
        finish();

    }

    private void eraseWholeDrawing() {
        signatureView.clearCanvas();
    }

    private void eraseTheDrawing() {
        signatureView.setPenSize(30);
        signatureView.setPenColor(getResources().getColor(R.color.white, null));
    }

    private void openColorPickerForDrawing() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                signatureView.setPenColor(defaultColor);
            }
        });

        ambilWarnaDialog.show();
    }

    private Uri convertBitmapToUri(Bitmap bitmap) {
        // Get the content resolver
        ContentResolver contentResolver = getContentResolver();

        // Generate a unique name for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        // Insert the bitmap as an image into MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri imageUri = null;
        try {
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                OutputStream outputStream = contentResolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageUri;
    }


}