package com.alphacreators.noteguardian.NOTES;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alphacreators.noteguardian.ADAPTER.NoteAdapter;
import com.alphacreators.noteguardian.TODO.AllTodoTask;
import com.alphacreators.noteguardian.DEVELOPER.Developer;
import com.alphacreators.noteguardian.ENTITY.Note;
import com.alphacreators.noteguardian.FEATURES.Features;
import com.alphacreators.noteguardian.NOTIFICATION.NotificationScheduler;
import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.REMAINDERNOTES.RemainderNotes;
import com.alphacreators.noteguardian.SETTING.Setting;
import com.alphacreators.noteguardian.VIEWMODEL.SharedViewModel;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.alphacreators.noteguardian.USER.screenLock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private NoteViewModel noteViewModel;
    public int updateApp = 22;
    AppUpdateManager appUpdateManager;
    RecyclerView recyclerView;
    List<Note> noteList;
    SearchView searchView;
    FloatingActionButton newNote;
    ActivityResultLauncher<Intent> activityResultLauncherForAddNote;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateNote;
    Toolbar toolbar;
    private static final String storeUserFirstNamePref = "STORE_USER_FIRST_NAME_PREFS";
    private static final String userFirstName = "firstName";
    SharedPreferences prefs;
    NoteAdapter adapter = new NoteAdapter();
    boolean res;
    int color;
    Calendar calendar = Calendar.getInstance();
    List<Note> undoDeleteNote;
    NavigationView navigationView;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);


        inappUpdate();
        newNote = findViewById(R.id.newNote);
        recyclerView = findViewById(R.id.recyclerView);
        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        searchView = findViewById(R.id.searchBar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        toolbar.setTitle(getString(R.string.snotes));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color,null));
        navigationView = findViewById(R.id.navView);
        navigationView.setBackgroundColor(getResources().getColor(R.color.navigationBar, null));

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        View view = navigationView.getHeaderView(0);
        TextView textView = view.findViewById(R.id.headerName);
        prefs = getSharedPreferences(storeUserFirstNamePref, MODE_PRIVATE);
        String firstName = prefs.getString(userFirstName, null);

        if (firstName != null) {
            String string = String.valueOf(R.string.hi);
            textView.setText("Hi "+ firstName);
        }
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setBackground(new ColorDrawable(getResources().getColor(R.color.background_color,null)));

        navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.userAccount) {
                startActivity(new Intent(MainActivity.this, screenLock.class));
                return true;
            }
            else if (item.getItemId() == R.id.userNotes) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.addNote) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                activityResultLauncherForAddNote.launch(intent);
                return true;
            } else if (item.getItemId() == R.id.remainderActivity) {
                Intent i = new Intent(MainActivity.this, RemainderNotes.class);
                startActivity(i);
                return true;
            } else if (item.getItemId() == R.id.todoActivity) {
                startActivity(new Intent(MainActivity.this, AllTodoTask.class));
                return true;
            } else if (item.getItemId() == R.id.features) {
                Intent featuresIntent = new Intent(MainActivity.this, Features.class);
                startActivity(featuresIntent);
                return true;
            } else if (item.getItemId() == R.id.setting) {
                Intent intent1 = new Intent(MainActivity.this, Setting.class);
                startActivity(intent1);
                return true;
            } else if (item.getItemId() == R.id.developer) {
                startActivity(new Intent(MainActivity.this, Developer.class));
                return true;
            } else if (item.getItemId() == R.id.privacy) {
                String url = "https://shivam-bilaiya.github.io/snotes-privacy/";
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse(url));
                startActivity(intent2);
                return true;
            }
            else if (item.getItemId() == R.id.rate) {
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("https://play.google.com/store/apps/details?id=shivam.notesapp.snotes");
                intent3.setData(data);
                startActivity(intent3);
                return true;
            } else if (item.getItemId() == R.id.shareUs) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                String shareMessage = "I have used this amazing note app\n" +
                        "Try this !!\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=shivam.notesapp.snotes";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));

            }
            return false;
        });

        changeParentItemTextColor();


//        https://mahendranv.github.io/posts/viewmodel-store/

//        https://ptyagicodecamp.github.io/scheduling-repeating-local-notifications-using-alarm-manager.html


        sendNotificationDaily();

        registerActivityForUpdateNote();

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.delete);

        noteViewModel.getAllNotes().observe(this, notes -> {

//               update recycler view

            noteList = notes;
            undoDeleteNote = notes;
            adapter.setNotes(notes);

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                // RecyclerView.ViewHolder viewHolder => yaha iska mtlb hai ki kis note ko swap karna hai


                // RecyclerView.ViewHolder target => yaha iska mtlb hai ki viewholder vale note ko target vale note se swap karna hai


                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(noteList, fromPosition, toPosition);

                Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromPosition, toPosition);


                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Set the message show for the Alert time
                builder.setMessage(R.string.want_to_delete_this_note);


                // Set Alert Title
                builder.setTitle(R.string.alert);

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);


                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                    Note noteToDelete = adapter.getNotes(viewHolder.getAdapterPosition());
                    noteViewModel.delete(noteToDelete);
                    mediaPlayer.start();
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton(R.string.no, (dialog, which) -> {
                    adapter.setNotes(undoDeleteNote);
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
                Button Yes = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Yes.setTextColor(getResources().getColor(R.color.color2,null));
            }
        }).attachToRecyclerView(recyclerView);


        adapter.setOnItemClickListener(note -> {
            Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
            intent.putExtra("id", note.getId());
            intent.putExtra("title", note.getTitle());
            intent.putExtra("description", note.getDescription());
            intent.putExtra("notePriority", note.getNotePriority().toString());
            intent.putExtra("image", note.getImage());
            intent.putExtra("color", note.getBackgroundColor());
            intent.putExtra("favourite", note.isFavouriteNote());
            intent.putExtra("pinnedNote", note.isPinned());
            intent.putExtra("drawImageUri",note.getDrawImageUri());

            activityResultLauncherForUpdateNote.launch(intent);
        });

        registerActivityForAddNote();
        newNote.setOnClickListener(view12 -> {
            Log.d("eklnfewfnwjfw","knefkenwklnfew");

            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            activityResultLauncherForAddNote.launch(intent);
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && newNote.isShown())
                    newNote.hide();
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    newNote.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.cancelTimer();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.searchNotes(newText);
                return true;
            }
        });

        changeStatusBarColor();

        sharedViewModel.getSortTrigger().observe(this, trigger -> {
            if (trigger) {
                Log.d("lkenekgnfe", "wnfwkwlffmw");
                sortUserNotesByDate();
                sharedViewModel.setSortTrigger(false);
            }
        });

        sharedViewModel.getSortPriorityTrigger().observe(this, trigger -> {
            if (trigger) {
                sortUserNotesByPriority();
                sharedViewModel.setSortPriorityTrigger(false);
            }
        });

        sharedViewModel.getSortFavoriteTrigger().observe(this, trigger -> {
            if (trigger) {
                sortUserNotesByFavorite();
                sharedViewModel.setSortFavoriteTrigger(false);
            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
        noteViewModel.getAllNotes().observe(this, notes -> {
            noteList = notes;
            adapter.setNotes(notes);

        });
    }

    private void showSortNotesBottomSheet() {
        SortNotesBottomSheet bottomSheetFragment = new SortNotesBottomSheet();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    private void changeParentItemTextColor() {
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.parent1);
        MenuItem menuItem1 = menu.findItem(R.id.parent2);
        SpannableString string = new SpannableString(menuItem.getTitle());
        SpannableString string1 = new SpannableString(menuItem1.getTitle());
        string.setSpan(new TextAppearanceSpan(this, R.style.ChangeParentItemTextColor), 0, string.length(), 0);
        string1.setSpan(new TextAppearanceSpan(this, R.style.ChangeParentItemTextColor), 0, string1.length(), 0);
        menuItem.setTitle(string);
        menuItem1.setTitle(string1);
    }

    private void changeStatusBarColor() {
        int nightModeFlags =
                MainActivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = MainActivity.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.background_color));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = MainActivity.this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.toolBackgroundColor));
                break;


        }
    }

    private void sendNotificationDaily() {
        NotificationScheduler.scheduleDailyNotification(this,getString(R.string.it_s_time_to_plan_your_day),getString(R.string.making_a_good_habit_of_planing_your_day_so_that_you_will_not_miss_any_task));
    }


    private void inappUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> task = appUpdateManager.getAppUpdateInfo();
        task.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, updateApp);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Log.d("update app", "onSuccess: " + e.toString());
                    }
                }
            }
        });


        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    InstallStateUpdatedListener installStateUpdatedListener = installState -> {
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdatePopUp();
        }
    };

    private void showUpdatePopUp() {

        Snackbar snackbar = Snackbar.make(findViewById(R.id.content), "App update almost done", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.restart, view -> appUpdateManager.completeUpdate());

        snackbar.setTextColor(getResources().getColor(R.color.headerTextColor));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == updateApp) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, getString(R.string.app_update_successfully), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void registerActivityForUpdateNote() {
        activityResultLauncherForUpdateNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();

                if (resultCode == RESULT_OK && data != null) {
                    String title = data.getStringExtra("titleLast");
                    String description = data.getStringExtra("descriptionLast");
                    int id = data.getIntExtra("noteId", -1);
                    String imageUri = data.getStringExtra("image");
                    color = data.getIntExtra("color", R.color.noteBackgroundColor);
                    LocalDate localDate = convertStringToDate(data.getStringExtra("updateCurrentDate"));
                    boolean ans = data.getBooleanExtra("favourite", false);
                    Priority priority = Priority.valueOf(data.getStringExtra("updateNotePriority"));
                    boolean pinnedNote = data.getBooleanExtra("pinnedNote", false);
                    String uri = data.getStringExtra("drawImage");
                    Note note = new Note(title, description, imageUri, localDate, priority, color, ans, pinnedNote,uri);
                    note.setId(id);
                    noteViewModel.update(note);


                }
            }
        });
    }


    public void registerActivityForAddNote() {
        activityResultLauncherForAddNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    int resultCode = result.getResultCode();
                    Intent data = result.getData();
                    if (resultCode == RESULT_OK && data != null) {
                        String title = data.getStringExtra("noteTitle");
                        String description = data.getStringExtra("noteDescription");
                        String date = data.getStringExtra("saveNoteDate");
                        LocalDate localDate = convertStringToDate(date);
                        String imageUri = data.getStringExtra("image");
                        String drawImageUri = data.getStringExtra("drawImage");
                        color = data.getIntExtra("color", R.color.noteBackgroundColor);
                        boolean ans = data.getBooleanExtra("favourite", false);
                        boolean pinnedNote = data.getBooleanExtra("pinnedNote", false);
                        Priority priority = Priority.valueOf(data.getStringExtra("notePriority"));
                        Note note = new Note(title, description, imageUri, localDate, priority, color, ans, pinnedNote,drawImageUri);
                        noteViewModel.insert(note);


                    }
                });
    }


    // YE CODE LANGUAGE CHANGE KARNE KE LIYE HAI


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sortUserNotes) {
            showSortNotesBottomSheet();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // YE CODE JAB ACTIVITY LOAD HOGI TAB LANG CHECK KAREGA

    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("app_lang", "");
        setLocal(language);
    }


    private LocalDate convertStringToDate(String formattedDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            return LocalDate.parse(formattedDate, formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sortUserNotesByDate() {
        noteViewModel.getNotesSortByDate().observe(this, notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });
    }

    private void sortUserNotesByPriority() {
        noteViewModel.getNotesSortByPriority().observe(this, notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });
    }

    private void sortUserNotesByFavorite() {
        noteViewModel.getNotesByFavorite().observe(this, notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });
    }
}