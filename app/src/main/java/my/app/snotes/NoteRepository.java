package my.app.snotes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private NoteDAO noteDAO;
    private LiveData<List<Note>> notes;

    private RemainderDao remainderDao;
    private LiveData<List<RNote>> rNotes;


    ExecutorService executors = Executors.newSingleThreadExecutor();

    public NoteRepository(Application application)
    {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);



        // noteDAO = noteDatabase.noteDAO(); ye ek abstract method hai toh iska koi code ni hai toh isko yaha use yaha kyo kar rahe hai
        // iska code already room ne likh chuka hai background hai
        noteDAO = noteDatabase.noteDAO();
        notes = noteDAO.getAllNotes();


        remainderDao = noteDatabase.remainderDao();
        rNotes = remainderDao.getAllRemainderNotes();


    }

    public void insert(Note note)
    {
//        new InsertNoteAsyncTask(noteDAO).execute(note);
         executors.execute(new Runnable() {
             @Override
             public void run() {
                 noteDAO.insert(note);
             }
         });
    }

    public void delete(Note note)
    {
//        new DeleteNoteAsyncTask(noteDAO).execute(note);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                noteDAO.delete(note);
            }
        });
    }

    public void update(Note note)
    {
//        new UpdateNoteAsyncTask(noteDAO).execute(note);

        executors.execute(new Runnable() {
            @Override
            public void run() {
                noteDAO.update(note);
            }
        });
    }

    public LiveData<List<Note>> getAllNotes()
    {
        return notes;
    }


    public void RInsert(RNote rNote){
        executors.execute(new Runnable() {
            @Override
            public void run() {
                remainderDao.RemainderInsert(rNote);
            }
        });
    }

    public void RDelete(RNote rNote){
        executors.execute(new Runnable() {
            @Override
            public void run() {
                remainderDao.RemainderDelete(rNote);
            }
        });
    }

    public void RUpdate(RNote rNote){
        executors.execute(new Runnable() {
            @Override
            public void run() {
                remainderDao.RemainderUpdate(rNote);
            }
        });
    }

    public LiveData<List<RNote>> getRNotes(){
        return rNotes;
    }











//    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void>
//    {
//        private NoteDAO noteDAO;
//
//        public InsertNoteAsyncTask(NoteDAO noteDAO) {
//            this.noteDAO = noteDAO;
//        }
//
//        @Override
//        protected Void doInBackground(Note... notes) {
//           noteDAO.insert(notes[0]);
//            return null;
//        }
//    }
//
//    private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void>
//    {
//        private NoteDAO noteDAO;
//
//        public UpdateNoteAsyncTask(NoteDAO noteDAO) {
//            this.noteDAO = noteDAO;
//        }
//        @Override
//        protected Void doInBackground(Note... notes) {
//            noteDAO.update(notes[0]);
//            return null;
//        }
//    }
//
//    private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void>
//    {
//        private NoteDAO noteDAO;
//
//        public DeleteNoteAsyncTask(NoteDAO noteDAO) {
//            this.noteDAO = noteDAO;
//        }
//
//        @Override
//        protected Void doInBackground(Note... notes) {
//            noteDAO.delete(notes[0]);
//            return null;
//        }
//    }
}
