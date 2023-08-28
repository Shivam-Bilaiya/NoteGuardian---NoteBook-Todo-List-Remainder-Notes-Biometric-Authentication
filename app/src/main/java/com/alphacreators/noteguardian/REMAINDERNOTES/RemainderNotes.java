package com.alphacreators.noteguardian.REMAINDERNOTES;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alphacreators.noteguardian.ADAPTER.RemainderAdapter;
import com.alphacreators.noteguardian.ENTITY.RNote;
import com.alphacreators.noteguardian.NOTIFICATION.MyAlarmReceiver;
import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.alphacreators.noteguardian.VIEWMODEL.SharedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
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
    private SharedViewModel sharedViewModel;
    int year, month, day, hr, min;
    Calendar calendar = Calendar.getInstance();
    int color;
    List<RNote> undoDeleteNote;
    public static final String google_calendar_shared_pref = "googleCalendarSharedPref";
    public static final String google_calendar_toggleBtn = "googleCalendarToggleBtn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remainder_notes);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.delete);

        RNoteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        RNoteViewModel.getRNotes().observe(this, new Observer<List<RNote>>() {
            @Override
            public void onChanged(List<RNote> rNotes) {
                RNoteList = rNotes;
                undoDeleteNote = rNotes;
                RAdapter.RSetNotes(rNotes);
            }
        });

        RSearchView = findViewById(R.id.remainderSearchBar);
        RRecyclerView = findViewById(R.id.remainderRecyclerView);
        RNewNote = findViewById(R.id.remainderNewNote);
        RToolbar = findViewById(R.id.remainderToolbar);
        setSupportActionBar(RToolbar);
        RToolbar.setTitle(String.valueOf(R.string.add_remainder));
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
                Log.d("MY REMAINDER TESTING DATE",note.getRNoteDate().toString());
                intent.putExtra("RDate", note.getRNoteDate().toString());
                intent.putExtra("RDrawImageUri", note.getDrawImageUri());
                intent.putExtra("RImage", note.getRImage());
                intent.putExtra("RBackgroundColor", note.getRBackgroundColor());
                intent.putExtra("RFavouriteNote", note.isRFavouriteNote());
                intent.putExtra("RPinnedNote", note.isRPinned());
                intent.putExtra("RPriority", note.getRPriority().toString());

                activityResultLauncherForUpdateRemainderNote.launch(intent);
            }
        });

        registerActivityForUpdateRemainderNote();
        changeStatusBarColor();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(RemainderNotes.this);

                // Set the message show for the Alert time
                builder.setMessage(R.string.want_to_delete_this_note);


                // Set Alert Title
                builder.setTitle(R.string.alert);

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);


                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                    RNoteViewModel.RDelete(RAdapter.getRNotes(viewHolder.getAdapterPosition()));
                    mediaPlayer.start();

                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                    RAdapter.RSetNotes(undoDeleteNote);
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
                Button Yes = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Yes.setTextColor(getResources().getColor(R.color.color2, null));

            }
        }).attachToRecyclerView(RRecyclerView);

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

        sharedViewModel.getSortRTrigger().observe(this, aBoolean -> {
            if (aBoolean){
                sortRemainderNotesByDate();
                sharedViewModel.setSortRTrigger(false);
            }
        });

        sharedViewModel.getSortRPriorityTrigger().observe(this, aBoolean -> {
            if (aBoolean){
                sortRemainderNotesByPriority();
                sharedViewModel.setSortRPriorityTrigger(false);
            }


        });

        sharedViewModel.getSortRFavoriteTrigger().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    sortRemainderNotesByFavorite();
                    sharedViewModel.setSortRFavoriteTrigger(false);
                }
            }
        });



    }


    private void registerActivityForUpdateRemainderNote() {

        activityResultLauncherForUpdateRemainderNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int resultCode = result.getResultCode();
            Intent data = result.getData();

            if (resultCode == RESULT_OK && data != null) {
                String title = data.getStringExtra("URNoteTitle");
                String description = data.getStringExtra("URNoteDescription");
                int id = data.getIntExtra("noteId", -1);
                String uri = data.getStringExtra("RImage");
                String drawImageUri = data.getStringExtra("RDrawImage");
                String date = data.getStringExtra("URNoteDate");
                Log.d("keankenewfewkfws",date);
                Priority priority = Priority.valueOf(data.getStringExtra("URNotePriority"));
                StringTokenizer stringTokenizer = new StringTokenizer(date, " ");
                String s1 = stringTokenizer.nextToken();
                String s2 = stringTokenizer.nextToken();
                StringTokenizer stringTokenizer1 = new StringTokenizer(s1, "/");
                int y = Integer.parseInt(stringTokenizer1.nextToken());
                int m = Integer.parseInt(stringTokenizer1.nextToken());
                int d = Integer.parseInt(stringTokenizer1.nextToken());
                m = m - 1;
                year = y;
                month = m;
                day = d;
                StringTokenizer stringTokenizer2 = new StringTokenizer(s2, ":");
                int h = Integer.parseInt(stringTokenizer2.nextToken());
                int mi = Integer.parseInt(stringTokenizer2.nextToken());
                hr = h;
                min = mi;
                int color = data.getIntExtra("UColor", R.color.noteBackgroundColor);
                boolean ans = data.getBooleanExtra("favourite", false);
                boolean pinned = data.getBooleanExtra("pinnedNote", false);
                RNote note = new RNote(title, description, uri, date, pinned, color, ans, drawImageUri, priority);
                note.setRid(id);
                RNoteViewModel.RUpdate(note);
                sendNotification(year, month, day, hr, min, title, description);
                SharedPreferences sharedPreferences = getSharedPreferences(google_calendar_shared_pref, MODE_PRIVATE);
                boolean answer = sharedPreferences.getBoolean(google_calendar_toggleBtn, false);
                if (answer) {
                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                    calIntent.setData(CalendarContract.Events.CONTENT_URI);
                    calIntent.putExtra(CalendarContract.Events.TITLE, title);
                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, description);
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(year, month, day, hr, min);
                    Calendar endTime = Calendar.getInstance();
                    endTime.set(year, month, day, hr, min);
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            startTime.getTimeInMillis());
                    calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            endTime.getTimeInMillis());
                    startActivity(calIntent);
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
                    String title = data.getStringExtra("RNoteTitle");
                    String description = data.getStringExtra("RNoteDescription");
                    String date = data.getStringExtra("RNoteDate");
                    Log.d("dlfelfnew",date);
                    StringTokenizer stringTokenizer = new StringTokenizer(date, " ");
                    String s1 = stringTokenizer.nextToken();
                    Log.d("first", "onActivityResult: " + s1);
                    String s2 = stringTokenizer.nextToken();
                    Log.d("second", "onActivityResult: " + s2);
                    StringTokenizer tokenizer = new StringTokenizer(s1, "/");
                    int y = Integer.parseInt(tokenizer.nextToken());
                    int m = Integer.parseInt(tokenizer.nextToken());
                    int d = Integer.parseInt(tokenizer.nextToken());
                    m = m - 1;
                    Log.d("lef;nekneknw",m+"");
                    year = d;
                    month = m;
                    day = y;
                    Log.d("year month day", "onActivityResult: " + year + " " + month + " " + day);
                    StringTokenizer tokenizer1 = new StringTokenizer(s2, ":");
                    int h = Integer.parseInt(tokenizer1.nextToken());
                    int mi = Integer.parseInt(tokenizer1.nextToken());
                    hr = h;
                    min = mi;
                    Log.d("hr min", "onActivityResult: " + hr + " " + min);
                    String uri = data.getStringExtra("RImage");
                    String drawImageUri = data.getStringExtra("RDrawImage");
                    int color = data.getIntExtra("color", R.color.noteBackgroundColor);
                    boolean ans = data.getBooleanExtra("favourite", false);
                    boolean pinned = data.getBooleanExtra("pinnedNote", false);
                    Priority priority = Priority.valueOf(data.getStringExtra("RNotePriority"));
                    Log.d("kwlnkwnfkfnw",priority.toString());
                    RNote rNote = new RNote(title, description, uri, date, pinned, color, ans, drawImageUri, priority);
                    RNoteViewModel.RInsert(rNote);
                    Log.d("remainder date time with user " , year + "");
                    sendNotification(year, month, day, hr, min, title, description);
                    SharedPreferences sharedPreferences = getSharedPreferences(google_calendar_shared_pref, MODE_PRIVATE);
                    boolean answer = sharedPreferences.getBoolean(google_calendar_toggleBtn, false);
                    if (answer) {
                        Intent calIntent = new Intent(Intent.ACTION_INSERT);
                        calIntent.setData(CalendarContract.Events.CONTENT_URI);
                        calIntent.putExtra(CalendarContract.Events.TITLE, title);
                        calIntent.putExtra(CalendarContract.Events.DESCRIPTION, description);
                        Calendar startTime = Calendar.getInstance();
                        startTime.set(year, month, day, hr, min);
                        Log.d("calendar date and time", "onActivityResult: " + year + " " + month + " " + day + " " + hr + " " + min);
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(year, month, day, hr, min);
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                startTime.getTimeInMillis());
                        calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                endTime.getTimeInMillis());
                        startActivity(calIntent);
                    }
                }
            }
        });

//        sendNotification(year,month,day,hr,min);

        // for delete and swap of notes


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

    private void sendNotification(int year, int month, int day, int hour, int minute, String title, String description) {
        Log.d("qweknfwklfn_lwqjfwkfj",year + " " + month + " " + day + " " + hour + " " + minute);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_notes,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortUserNotes){
            showRemainderBottomSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRemainderBottomSheet() {
        RemainderNotesBottomSheet bottomSheetFragment = new RemainderNotesBottomSheet();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void sortRemainderNotesByDate(){
        RNoteViewModel.getRemainderNotesSortByDate().observe(this, rNotes -> {
            if (rNotes != null){
                RAdapter.RSetNotes(rNotes);
            }
        });
    }

    private void sortRemainderNotesByPriority(){
        RNoteViewModel.getRemainderNotesSortByPriority().observe(this, rNotes -> {
            if (rNotes != null){
                RAdapter.RSetNotes(rNotes);
            }
        });
    }

    private void sortRemainderNotesByFavorite(){
        RNoteViewModel.getRemainderNotesSortByFavorite().observe(this, rNotes -> {
            if (rNotes != null){
                RAdapter.RSetNotes(rNotes);
            }
        });
    }
}