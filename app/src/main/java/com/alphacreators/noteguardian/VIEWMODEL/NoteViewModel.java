package com.alphacreators.noteguardian.VIEWMODEL;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.alphacreators.noteguardian.ENTITY.Note;
import com.alphacreators.noteguardian.ENTITY.RNote;
import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.ENTITY.User;
import com.alphacreators.noteguardian.NOTEREPO.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private static NoteRepository repository;

    private LiveData<List<Note>> notes;

    private LiveData<List<RNote>> rNotes;

    private LiveData<List<TodoTask>> todoTask;

    private LiveData<List<User>> users;




    public NoteViewModel(@NonNull Application application) {
        super(application);

        repository = new NoteRepository(application);
        notes = repository.getAllNotes();
        rNotes = repository.getRNotes();
        todoTask = repository.getTodoTask();
        users = repository.getUsers();

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

    public LiveData<List<Note>> getNotesSortByDate(){
        return repository.getNotesSortByDate();
    }

    public LiveData<List<Note>> getNotesSortByPriority(){
        return repository.getNotesSortByPriority();
    }

    public LiveData<List<Note>> getNotesByFavorite(){
        return repository.getNotesByFavorite();
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

    public LiveData<List<RNote>> getRemainderNotesSortByDate(){
        return repository.getRemainderNotesSortByDate();
    }

    public LiveData<List<RNote>> getRemainderNotesSortByPriority(){
        return repository.getRemainderNotesSortByPriority();
    }

    public LiveData<List<RNote>> getRemainderNotesSortByFavorite(){
        return repository.getRemainderNotesSortByFavorite();
    }

    public static void todoInsert(TodoTask todoTask){
        repository.todoInsert(todoTask);
    }

    public static void todoUpdate(TodoTask todoTask){
        repository.todoUpdate(todoTask);
    }

    public static void todoDelete(TodoTask todoTask){
        repository.todoDelete(todoTask);
    }

    public LiveData<List<TodoTask>> getTodoTask(){
        return todoTask;
    }

    public LiveData<TodoTask> getSingleTask(int todoId){
        return repository.getSingleTask(todoId);
    }

    public LiveData<User> getUserData(long userId){
        return repository.getUserData(userId);
    }

    public long insertUser(User user){
        return repository.addUser(user);
    }

    public void updateUser(User user){
         repository.updateUserData(user);
    }

    public LiveData<List<User>> getAllUsers(){
        return repository.getUsers();
    }








}
