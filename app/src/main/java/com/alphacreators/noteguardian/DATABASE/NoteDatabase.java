package com.alphacreators.noteguardian.DATABASE;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.alphacreators.noteguardian.DAO.NoteDAO;
import com.alphacreators.noteguardian.DAO.RemainderDao;
import com.alphacreators.noteguardian.DAO.TodoDao;
import com.alphacreators.noteguardian.DAO.UserDao;
import com.alphacreators.noteguardian.ENTITY.Note;
import com.alphacreators.noteguardian.ENTITY.RNote;
import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.ENTITY.User;
import com.alphacreators.noteguardian.NOTEUTILITY.TypeConvertor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class, RNote.class, TodoTask.class, User.class},version = 23 ,exportSchema = false)
@TypeConverters({TypeConvertor.class})
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase noteDatabase;

    public abstract NoteDAO noteDAO();

    public abstract RemainderDao remainderDao();

    public abstract TodoDao todoDao();

    public abstract UserDao userDao();


    public static synchronized NoteDatabase getInstance(Context context)
    {
        if(noteDatabase == null)
        {
            noteDatabase = Room.databaseBuilder(context.getApplicationContext(),NoteDatabase.class,"note_database")
                    .fallbackToDestructiveMigration().addCallback(roomCallback).build();
        }
        return noteDatabase;
    }

    public static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

//            new PopulateDbAsyncTask(noteDatabase).execute();


            NoteDAO noteDAO = noteDatabase.noteDAO();

            RemainderDao remainderDao = noteDatabase.remainderDao();

            TodoDao todoDao = noteDatabase.todoDao();

            UserDao userDao = noteDatabase.userDao();


            ExecutorService executors = Executors.newSingleThreadExecutor();


            executors.execute(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };








}
