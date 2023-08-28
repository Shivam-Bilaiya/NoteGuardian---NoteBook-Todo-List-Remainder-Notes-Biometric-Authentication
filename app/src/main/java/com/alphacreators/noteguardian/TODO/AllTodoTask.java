package com.alphacreators.noteguardian.TODO;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacreators.noteguardian.ADAPTER.TodoAdapter;
import com.alphacreators.noteguardian.CLICKINTERFACE.onTodoClickListener;
import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.alphacreators.noteguardian.VIEWMODEL.SharedViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class AllTodoTask extends AppCompatActivity implements onTodoClickListener {

    private NoteViewModel todoNoteViewModel;
    RelativeLayout todoRelativeLayout;
    byte[] image;
    RecyclerView todoRecyclerView;
    List<TodoTask> todoNoteList;
    List<TodoTask> todoSearchMyNotes = new ArrayList<>();
    SearchView todoSearchView;
    FloatingActionButton todoNewNote;
    ActivityResultLauncher<Intent> activityResultLauncherForAddTask;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateTask;
    TodoAdapter todoAdapter;
    SharedViewModel sharedViewModel;
    BottomSheetFragment bottomSheetFragment;
    EditText todoTask;
    List<TodoTask> undoDeleteNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_all_todo_task);


        todoRecyclerView = findViewById(R.id.todoRecyclerView);
        todoSearchView = findViewById(R.id.todoSearchView);
        todoNewNote = findViewById(R.id.todoNewNote);
        todoTask = findViewById(R.id.enter_todo_et);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.delete);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.todo_list);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.toolBackgroundColor, null)));


        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);


        todoRecyclerView.setHasFixedSize(true);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        todoNoteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(NoteViewModel.class);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        todoNoteViewModel.getTodoTask().observe(this, todoTasks -> {

            todoNoteList = todoTasks;

            todoAdapter = new TodoAdapter(todoTasks, this);

            todoRecyclerView.setAdapter(todoAdapter);


            undoDeleteNote = todoTasks;
            todoAdapter.setTodoNotes(todoTasks);

        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                // RecyclerView.ViewHolder viewHolder => yaha iska mtlb hai ki kis note ko swap karna hai


                // RecyclerView.ViewHolder target => yaha iska mtlb hai ki viewholder vale note ko target vale note se swap karna hai


                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(todoNoteList, fromPosition, toPosition);

                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);


                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AllTodoTask.this);

                // Set the message show for the Alert time
                builder.setMessage(R.string.want_to_delete_this_note);


                // Set Alert Title
                builder.setTitle(R.string.alert);

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);


                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                    NoteViewModel.todoDelete(todoAdapter.getTodoTask(viewHolder.getAdapterPosition()));
                    mediaPlayer.start();
                });

                // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                    todoAdapter.setTodoNotes(undoDeleteNote);
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();
                // Show the Alert Dialog box
                alertDialog.show();
                Button Yes = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Yes.setTextColor(getResources().getColor(R.color.color2,null));
            }
        }).attachToRecyclerView(todoRecyclerView);




        changeStatusBarColor();

        todoNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        todoSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                todoAdapter.cancelTimer();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                todoAdapter.searchNotes(newText);
                return true;
            }
        });


        // YE FAB KO SCROLL KARTE TIME GAYAB KARNE KE LIYE HAI

        todoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && todoNewNote.isShown())
                    todoNewNote.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    todoNewNote.setVisibility(View.VISIBLE);
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

//        registerActivityForAddTask();

//        registerActivityForUpdateTask();


    }

    private void showBottomSheetDialog() {
        todoTask.setText("");
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void changeStatusBarColor() {
        int nightModeFlags =
                AllTodoTask.this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = AllTodoTask.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:


                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(AllTodoTask.this, R.color.background_color));

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                window = AllTodoTask.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(ContextCompat.getColor(AllTodoTask.this, R.color.toolBackgroundColor));
                break;


        }
    }


//    public void registerActivityForUpdateTask() {
//        activityResultLauncherForUpdateTask = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult result) {
//                int resultCode = result.getResultCode();
//                Intent data = result.getData();
//
//                if (resultCode == RESULT_OK && data != null) {
//                    String title = data.getStringExtra("titleLast");
//                    int id = data.getIntExtra("noteId", -1);
//                    image = data.getByteArrayExtra("image");
//                    String date = data.getStringExtra("updateCurrentDate");
//                    TodoTask todoTask = new TodoTask(title,true,date,image);
//                    todoTask.setTodoId(id);
//                    todoNoteViewModel.todoUpdate(todoTask);
//
//
//                }
//            }
//        });
//    }


//    public void registerActivityForAddTask(){
//        activityResultLauncherForAddTask = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult result) {
//                int resultCode = result.getResultCode();
//                Intent data = result.getData();
//
//                if (resultCode == RESULT_OK && data!=null){
//                    Intent intent = new Intent();
//                    String title = intent.getStringExtra("noteTitle");
//                    String date = intent.getStringExtra("saveNoteDate");
//                    image = data.getByteArrayExtra("todoImage");
//                    TodoTask todoTask = new TodoTask(title,false,date,image);
//                    todoNoteViewModel.todoInsert(todoTask);
//                }
//            }
//        });
//    }


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


    // YE CODE JAB ACTIVITY LOAD HOGI TAB LANG CHECK KAREGA

    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("app_lang", "");
        setLocal(language);
    }


    @Override
    public void onTodoClick(TodoTask todoTask) {
        sharedViewModel.selectItem(todoTask);
        sharedViewModel.setIsEdit(true);
        showBottomSheetDialog();
    }

    @Override
    public void onTodoRadioButtonClicked(TodoTask todoTask) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.delete);
        NoteViewModel.todoDelete(todoTask);
        Toast.makeText(this, getString(R.string.task_is_completed), Toast.LENGTH_SHORT).show();
        mediaPlayer.start();
        todoAdapter.notifyDataSetChanged();
    }
}