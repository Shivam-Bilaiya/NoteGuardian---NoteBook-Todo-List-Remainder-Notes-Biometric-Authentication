package com.example.notetaking;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    private NoteViewModel noteViewModel;

    RelativeLayout relativeLayout;

    byte[] image;







    EditText searchNotes;

    RecyclerView recyclerView;



    List<Note> noteList;

    FloatingActionButton newNote;

    NoteAdapter noteAdapter;

    ActivityResultLauncher<Intent> activityResultLauncherForAddNote;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateNote;


    Toolbar toolbar ;


    private static final String shared_pref_name = "myPref";
    private static final String userFirstName = "firstName";
    private static final String userLastName = "lastName";

    public static  final  String shared_pref = "sharedPrefs";
    public static final String toggleBtn = "toggleBtn";

    SharedPreferences prefs;

    ImageView imageView;

//    ExecutorService executors = Executors.newFixedThreadPool(1);
//    Setting setting = new Setting();
//
//    int ans;
//
//    Future<Integer> future;

    boolean res;







    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);


        Calendar calendar = Calendar.getInstance();





        setStatusBarColor();

         relativeLayout = findViewById(R.id.rLayout);
         relativeLayout.setVisibility(View.INVISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences(shared_pref,MODE_PRIVATE);
        res = sharedPreferences.getBoolean(toggleBtn,false);


        searchNotes = findViewById(R.id.searchYourNotes);













        NavigationView navigationView = findViewById(R.id.navView);
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.searchImage);
        toolbar.setTitle("Note Taking");

        prefs = getSharedPreferences(shared_pref_name,MODE_PRIVATE);

        String firstName = prefs.getString(userFirstName,null);


        imageView.setBackground(new ColorDrawable(getResources().getColor(R.color.background_color)));


          navigationView.setBackgroundColor(getResources().getColor(R.color.background_color));





        View view = navigationView.getHeaderView(0);
        TextView textView = view.findViewById(R.id.headerName);



        if (firstName!=null)
        {
            relativeLayout.setVisibility(View.VISIBLE);
            textView.setText("Hi " + firstName);
        }






        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
       actionBarDrawerToggle.syncState();



       toolbar.setTitle(R.string.app_name);


//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//
//
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);





        navigationView.setBackground(new ColorDrawable(getResources().getColor(R.color.navigationViewColor)));


       // for accessing item in navigation drawer

        // YE NAVIGATION DRAWER

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {

                    case R.id.userAccount:
                        startActivity(new Intent(MainActivity.this,screenLock.class));
                        return true;
                    case R.id.userNotes:
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                        finish();
                        return true;
                    case R.id.addNote:
                        Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);
                        activityResultLauncherForAddNote.launch(intent);
                        return true;
                    case R.id.features:
                        Intent featuresIntent = new Intent(MainActivity.this,Features.class);
                        startActivity(featuresIntent);
                        return true;
                    case R.id.setting:
                        Intent intent1 = new Intent(MainActivity.this,Setting.class);
                        startActivity(intent1);
                        return true;

                    case R.id.developer:
                        startActivity(new Intent(MainActivity.this,Developer.class));
                        return true;

                }

                return false;
            }
        });










//Create the text view




        registerActivityForAddNote();

        newNote = findViewById(R.id.newNote);

        recyclerView = findViewById(R.id.recyclerView);




        registerActivityForUpdateNote();


//        registerActivityForFinger();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));


        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);


















//        https://mahendranv.github.io/posts/viewmodel-store/

        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);

        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {

//               update recycler view

                noteList = notes;

                adapter.setNotes(notes);

            }
        });



        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                // RecyclerView.ViewHolder viewHolder => yaha iska mtlb hai ki kis note ko swap karna hai


                // RecyclerView.ViewHolder target => yaha iska mtlb hai ki viewholder vale note ko target vale note se swap karna hai




                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(noteList,fromPosition,toPosition);

                recyclerView.getAdapter().notifyItemMoved(fromPosition,toPosition);


                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                noteViewModel.delete(adapter.getNotes(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);




        adapter.setOnItemClickListener(new NoteAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this,UpdateActivity.class);
                intent.putExtra("id",note.getId());
                intent.putExtra("title",note.getTitle());
                intent.putExtra("description",note.getDescription());
                intent.putExtra("image",note.getImage());


                activityResultLauncherForUpdateNote.launch(intent);
            }
        });











//        https://ptyagicodecamp.github.io/scheduling-repeating-local-notifications-using-alarm-manager.html




        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {

            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 16);
            calendar.set(Calendar.SECOND, 0);
            Intent intent1 = new Intent(MainActivity.this, Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_MUTABLE);
            AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else{
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 16);
            calendar.set(Calendar.SECOND, 0);
            Intent intent1 = new Intent(MainActivity.this, Notification_Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }










        // YE CODE STATUS BAR KA COLOR CHANGE KARNE KE LIYE HAI


        int nightModeFlags =
                MainActivity.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.toolBackgroundColor));
                break;


        }

        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , AddNoteActivity.class);
                activityResultLauncherForAddNote.launch(intent);
            }
        });




//




        // YE NOTE SEARCH KARNE KE LIYE HAI


           searchNotes.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                   adapter.cancelTimer();

               }

               @Override
               public void afterTextChanged(Editable editable) {

                   adapter.searchNotes(editable.toString());



               }
           });





           // YE FAB KO SCROLL KARTE TIME GAYAB KARNE KE LIYE HAI

          recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
              @Override
              public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                  if(newState == RecyclerView.SCROLL_STATE_IDLE){
                      newNote.show();


                  }

                  super.onScrollStateChanged(recyclerView, newState);


              }

              @Override
              public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                  if(dy > 0 || dy < 0 && newNote.isShown())
                  {
                      newNote.hide();
                  }
              }
          });


































    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Your Notes");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void registerActivityForUpdateNote()
    {
        activityResultLauncherForUpdateNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data = result.getData();

                if(resultCode == RESULT_OK && data!=null)
                {
                    String title = data.getStringExtra("titleLast");
                    String description = data.getStringExtra("descriptionLast");
                    int id = data.getIntExtra("noteId",-1);
                    image = data.getByteArrayExtra("image");
                    String date = data.getStringExtra("updateCurrentDate");
                    Note note = new Note(title,description,image,date);
                    note.setId(id);
                    noteViewModel.update(note);


                }
            }
        });
    }






    public void registerActivityForAddNote()
    {
        activityResultLauncherForAddNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        int resultCode = result.getResultCode();
                        Intent data = result.getData();

                        if(resultCode == RESULT_OK && data!=null)
                        {
                            String title = data.getStringExtra("noteTitle");
                            String description = data.getStringExtra("noteDescription");
                            String date = data.getStringExtra("saveNoteDate");
                            image = data.getByteArrayExtra("image");
                            Note note = new Note(title,description,image,date);
                            noteViewModel.insert(note);




                        }
                    }
                });
    }



    // YE CODE LANGUAGE CHANGE KARNE KE LIYE HAI


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


    // YE CODE JAB ACTIVITY LOAD HOGI TAB LANG CHECK KAREGA

    private void loadLocale()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = sharedPreferences.getString("app_lang","");
        setLocal(language);
    }

    public  void setStatusBarColor ()
    {
        Window window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.toolBackgroundColor));
    }





//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 1 && resultCode == RESULT_OK && data!=null)
//        {
//            String title = data.getStringExtra("noteTitle");
//            String description = data.getStringExtra("noteDescription");
//
//
//            Note note = new Note(title,description);
//            noteViewModel.insert(note);
//        }
//    }
}