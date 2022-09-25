package my.app.snotes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

public class UpdateRemainder extends AppCompatActivity {

    int Rid;
    byte[] RImage;
    Bitmap RBitmap;
    Bitmap RShareBitmap;
    Bitmap RScaleImage;
    EditText URemainderTitle,URemainderDescription;
    BottomNavigationView URemainderBottomNavigationView;
    ImageView URemainderImage;
    TextView URemainder;
    FloatingActionButton URemainderSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_remainder);

        URemainderTitle = findViewById(R.id.URemainderTitle);
        URemainderDescription = findViewById(R.id.URemainderDescription);
        URemainderImage = findViewById(R.id.URemainderImage);
        URemainder = findViewById(R.id.URemainder);
        URemainderBottomNavigationView = findViewById(R.id.URemainderBottomNavigationView);
        URemainderSaveBtn = findViewById(R.id.URemainderSaveBtn);

        getData();



        URemainderSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (URemainderTitle.getText().toString().isEmpty() || URemainderDescription.getText().toString().isEmpty()){
                    Toast.makeText(UpdateRemainder.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if (URemainder.getText().toString().isEmpty()){
                    Toast.makeText(UpdateRemainder.this, "Please add remainder date", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveNote();
                }
            }
        });
    }

    private void saveNote() {
        String UTitleLast = URemainderTitle.getText().toString();
        String UDescriptionLast = URemainderDescription.getText().toString();
        String UDate = URemainder.getText().toString();



        Intent intent = new Intent();

        RBitmap = ((BitmapDrawable) URemainderImage.getDrawable()).getBitmap();


        BitmapDrawable bitmapDrawable = (BitmapDrawable) URemainderImage.getDrawable();
        RShareBitmap = bitmapDrawable.getBitmap();

        if (RBitmap == null)
        {
            intent.putExtra("image",new byte[0]);
        }
        if (RBitmap!=null )
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            RScaleImage = makeSmall(RBitmap,300);
            RScaleImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
            RImage = byteArrayOutputStream.toByteArray();
            intent.putExtra("image",RImage);
        }

        intent.putExtra("UTitleLast",UTitleLast);
        intent.putExtra("UDescriptionLast",UDescriptionLast);
        intent.putExtra("UDate",UDate);

        if(Rid != -1)
        {
            intent.putExtra("noteId",Rid);
            setResult(RESULT_OK,intent);
            Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void getData() {
        Intent intent = getIntent();

        Rid = intent.getIntExtra("RNoteId",-1);
        String RTitle = intent.getStringExtra("RNoteTitle");
        String RDescription = intent.getStringExtra("RNoteDescription");
        Bundle bundle = getIntent().getExtras();
        RImage = bundle.getByteArray("RImage");
        String date = intent.getStringExtra("RDate");
        Bitmap bmp = BitmapFactory.decodeByteArray(RImage,0,RImage.length);

        URemainderTitle.setText(RTitle);
        URemainderDescription.setText(RDescription);
        URemainder.setText(date);
        URemainderImage.setImageBitmap(bmp);

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
}