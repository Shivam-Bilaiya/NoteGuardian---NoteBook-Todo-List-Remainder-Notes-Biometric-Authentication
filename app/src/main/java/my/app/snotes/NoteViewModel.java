package my.app.snotes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository repository;

    private LiveData<List<Note>> notes;

    private LiveData<List<RNote>> rNotes;


    public NoteViewModel(@NonNull Application application) {
        super(application);

        repository = new NoteRepository(application);
        notes = repository.getAllNotes();
        rNotes = repository.getRNotes();
    }

    public void insert(Note note)
    {
        repository.insert(note);
    }

    public void update(Note note)
    {
        repository.update(note);
    }

    public void delete(Note note)
    {
        repository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes()
    {
        return notes;
    }

    public void RInsert(RNote rNote){
        repository.RInsert(rNote);
    }

    public void RDelete(RNote rNote){
        repository.RDelete(rNote);
    }

    public void RUpdate(RNote rNote){
        repository.RUpdate(rNote);
    }

    public LiveData<List<RNote>> getRNotes(){
         return rNotes;
    }




}
