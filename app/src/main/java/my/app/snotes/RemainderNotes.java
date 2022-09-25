package my.app.snotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

public class RemainderNotes extends AppCompatActivity {

    SearchView RSearchView;
    RecyclerView RRecyclerView;
    FloatingActionButton RNewNote;
    Toolbar RToolbar;
    byte[] RImage;
    List<RNote> RNoteList;
    List<RNote> searchMyNotes = new ArrayList<>();
    RemainderAdapter RAdapter = new RemainderAdapter();
    ActivityResultLauncher<Intent> activityResultLauncherForAddRemainder;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateRemainderNote;
    private NoteViewModel RNoteViewModel;
    int year, month, day, hr, min;
    Calendar calendar = Calendar.getInstance();
    String title, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder_notes);




        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor)));




        RNoteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);


        RNoteViewModel.getRNotes().observe(this, new Observer<List<RNote>>() {
            @Override
            public void onChanged(List<RNote> rNotes) {
                RNoteList = rNotes;
                RAdapter.RSetNotes(rNotes);
            }
        });

        RSearchView = findViewById(R.id.remainderSearchBar);
        RRecyclerView = findViewById(R.id.remainderRecyclerView);
        RNewNote = findViewById(R.id.remainderNewNote);
        RToolbar = findViewById(R.id.remainderToolbar);


        RRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        RRecyclerView.setAdapter(RAdapter);

        RNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemainderNotes.this, Remainders.class);
                activityResultLauncherForAddRemainder.launch(intent);
            }
        });

        registerActivityForRemainderNote();



        RAdapter.setOnItemClickListener(new RemainderAdapter.onRItemClickListener() {
            @Override
            public void onItemClick(RNote note) {
                Intent intent = new Intent(RemainderNotes.this, UpdateRemainder.class);
                intent.putExtra("RNoteId", note.getRid());
                intent.putExtra("RNoteTitle", note.getRTitle());
                intent.putExtra("RNoteDescription", note.getRDescription());
                intent.putExtra("RDate",note.getRNoteDate());
                intent.putExtra("RImage", note.getRImage());

//                Toast.makeText(context, year+"**"+month+"**"+day+"**"+hr+"**"+min, Toast.LENGTH_SHORT).show();

                activityResultLauncherForAddRemainder.launch(intent);
            }
        });

        registerActivityForUpdateRemainderNote();
        changeStatusBarColor();

        RSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RAdapter.cancelTimer();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RAdapter.searchNotes(newText);
                return true;
            }
        });


        // YE FAB KO SCROLL KARTE TIME GAYAB KARNE KE LIYE HAI

        RRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && RNewNote.isShown())
                    RNewNote.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    RNewNote.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });




    }




    private void registerActivityForUpdateRemainderNote() {

        activityResultLauncherForUpdateRemainderNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    int resultCode = result.getResultCode();
                    Intent data = result.getData();

                    if (resultCode == RESULT_OK && data != null) {
                        String title = data.getStringExtra("titleLast");
                        String description = data.getStringExtra("descriptionLast");
                        int id = data.getIntExtra("noteId", -1);
                        RImage = data.getByteArrayExtra("image");
                        String date = data.getStringExtra("updateCurrentDate");
                        Note note = new Note(title, description, RImage, date);
                        note.setId(id);
                        RNoteViewModel.update(note);


                    }
                }
            });

    }

    public void registerActivityForRemainderNote() {
        activityResultLauncherForAddRemainder = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();

                if (resultCode == RESULT_OK && data != null) {
                    title = data.getStringExtra("RNoteTitle");
                    description = data.getStringExtra("RNoteDescription");
                    String date = data.getStringExtra("RNoteDate");
                    StringTokenizer stringTokenizer = new StringTokenizer(date, "|");
//                    int y = Integer.parseInt(stringTokenizer.nextToken());
//                    int m = Integer.parseInt(stringTokenizer.nextToken());
//                    int d = Integer.parseInt(stringTokenizer.nextToken());
//                    int h = Integer.parseInt(stringTokenizer.nextToken());
//                    int mi = Integer.parseInt(stringTokenizer.nextToken());
//                    m = m-1;

                    String s1 = stringTokenizer.nextToken();
                    String s2 = stringTokenizer.nextToken();

                    StringTokenizer tokenizer = new StringTokenizer(s1,"/");
                    int y = Integer.parseInt(tokenizer.nextToken());
                    int m = Integer.parseInt(tokenizer.nextToken());
                    int d = Integer.parseInt(tokenizer.nextToken());
                    m = m-1;
                    year = y;
                    month = m;
                    day = d;
                    StringTokenizer tokenizer1 = new StringTokenizer(s2,":");
                    int h = Integer.parseInt(tokenizer1.nextToken());
                    int mi = Integer.parseInt(tokenizer1.nextToken());
                    hr = h;
                    min = mi;
                    RImage = data.getByteArrayExtra("RImage");
                    RNote note = new RNote(title, description, RImage, date);
                    RNoteViewModel.RInsert(note);
                    sendNotification(year, month, day, hr, min,title,description);


                }
            }
        });

//        sendNotification(year,month,day,hr,min);

        // for delete and swap of notes

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                // RecyclerView.ViewHolder viewHolder => yaha iska mtlb hai ki kis note ko swap karna hai


                // RecyclerView.ViewHolder target => yaha iska mtlb hai ki viewholder vale note ko target vale note se swap karna hai


                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(RNoteList, fromPosition, toPosition);

                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);


                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                RNoteViewModel.RDelete(RAdapter.getRNotes(viewHolder.getAdapterPosition()));
                Toast.makeText(RemainderNotes.this, "Note Deleted", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(RRecyclerView);


    }

    private void showToast() {
        Toast.makeText(RemainderNotes.this, "UJUJUJUJUJ", Toast.LENGTH_SHORT).show();
    }


    private void changeStatusBarColor() {
        int nightModeFlags =
                RemainderNotes.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = RemainderNotes.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(RemainderNotes.this, R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = RemainderNotes.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(RemainderNotes.this, R.color.toolBackgroundColor));
                break;
        }
    }

    private void sendNotification(int year,int month,int day,int hr,int min,String title,String description){
        Toast.makeText(this, "uyuyuyu", Toast.LENGTH_SHORT).show();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,MyAlarmReceiver.class);
        intent.putExtra("title",title);
        intent.putExtra("description",description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1001,intent,0);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.HOUR_OF_DAY,hr);
        calendar.set(Calendar.MINUTE,min);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        Toast.makeText(this, "SHOW HOGA", Toast.LENGTH_SHORT).show();
    }



//    private boolean isAlarmSet() {
//        Intent intent = MyAlarmReceiver.getTargetIntent(context);
//        PendingIntent service = PendingIntent.getService(
//                context,
//                0,
//                intent,
//                PendingIntent.FLAG_NO_CREATE
//        );
//        return service != null;
//    }


}