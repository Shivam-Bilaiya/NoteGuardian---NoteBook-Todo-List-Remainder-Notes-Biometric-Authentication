package my.app.snotes;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class,RNote.class},version = 5,exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase noteDatabase;

    public abstract NoteDAO noteDAO();

    public abstract RemainderDao remainderDao();


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

            ExecutorService executors = Executors.newSingleThreadExecutor();


            executors.execute(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };







/*
    public static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>
    {
        NoteDAO noteDAO;

        public PopulateDbAsyncTask(NoteDatabase database) {
            noteDAO = database.noteDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDAO.insert(new Note("Title 1" , "Description 1"));
            noteDAO.insert(new Note("Title 2" , "Description 2"));
            noteDAO.insert(new Note("Title 3" , "Description 3"));
            noteDAO.insert(new Note("Title 4" , "Description 4"));
            noteDAO.insert(new Note("Title 5" , "Description 5"));
            return null;
        }
    }

 */
}
