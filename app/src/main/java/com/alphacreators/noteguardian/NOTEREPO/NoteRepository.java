package com.alphacreators.noteguardian.NOTEREPO;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;


import com.alphacreators.noteguardian.DAO.NoteDAO;
import com.alphacreators.noteguardian.DAO.RemainderDao;
import com.alphacreators.noteguardian.DAO.TodoDao;
import com.alphacreators.noteguardian.DAO.UserDao;
import com.alphacreators.noteguardian.DATABASE.NoteDatabase;
import com.alphacreators.noteguardian.ENTITY.Note;
import com.alphacreators.noteguardian.ENTITY.RNote;
import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.ENTITY.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {

    private final NoteDAO noteDAO;
    private final LiveData<List<Note>> notes;

    private final RemainderDao remainderDao;
    private final LiveData<List<RNote>> rNotes;


    private final TodoDao todoDao;
    private final LiveData<List<TodoTask>> todoTask;

    private final UserDao userDao;
    private final LiveData<List<User>> user;




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

        todoDao = noteDatabase.todoDao();
        todoTask = todoDao.getAllTodoTask();

        userDao = noteDatabase.userDao();
        user = userDao.getAllUsers();




    }

    public void insert(Note note)
    {
//        new InsertNoteAsyncTask(noteDAO).execute(note);
         executors.execute(() -> noteDAO.insert(note));
    }

    public void delete(Note note)
    {
//        new DeleteNoteAsyncTask(noteDAO).execute(note);

        executors.execute(() -> noteDAO.delete(note));
    }

    public void update(Note note)
    {
//        new UpdateNoteAsyncTask(noteDAO).execute(note);

        executors.execute(() -> noteDAO.update(note));
    }

    public LiveData<List<Note>> getAllNotes()
    {
        return notes;
    }


    public LiveData<List<Note>> getNotesSortByDate(){
        return noteDAO.getNotesSortByDate();
    }

    public LiveData<List<Note>> getNotesSortByPriority(){
        return noteDAO.getNotesSortByPriority();
    }

    public LiveData<List<Note>> getNotesByFavorite(){
        return noteDAO.getNotesSortByFavorite();
    }

    public void RInsert(RNote rNote){
        executors.execute(() -> remainderDao.RemainderInsert(rNote));
    }

    public void RDelete(RNote rNote){
        executors.execute(() -> remainderDao.RemainderDelete(rNote));
    }

    public void RUpdate(RNote rNote){
        executors.execute(() -> remainderDao.RemainderUpdate(rNote));
    }

    public LiveData<List<RNote>> getRNotes(){
        return rNotes;
    }

    public LiveData<List<RNote>> getRemainderNotesSortByDate(){
        return remainderDao.getRemainderNotesSortByDate();
    }

    public LiveData<List<RNote>> getRemainderNotesSortByPriority(){
        return remainderDao.getRemainderNotesSortByPriority();
    }

    public LiveData<List<RNote>> getRemainderNotesSortByFavorite(){
        return remainderDao.getRemainderNotesSortByFavorite();
    }

    public void todoInsert(TodoTask todoTask){
        executors.execute(() -> todoDao.todoInsert(todoTask));
    }

    public void todoUpdate(TodoTask todoTask){
        executors.execute(() -> todoDao.todoUpdate(todoTask));
    }

    public void todoDelete(TodoTask todoTask){
        executors.execute(() -> todoDao.todoDelete(todoTask));
    }

    public LiveData<List<TodoTask>> getTodoTask(){
        return todoTask;
    }

    public LiveData<TodoTask>  getSingleTask(int todoId){
        return todoDao.getSingleTask(todoId);
    }

    public long addUser(User user){
        return userDao.insert(user);
    }


    public LiveData<User> getUserData(long userId){
        Log.d("lkenkelnge","lknekgnrkn " + userId);
        return userDao.getUserById(userId);
    }

    public void updateUserData(User user){
        executors.execute(()-> userDao.update(user));
    }

    public LiveData<List<User>> getUsers(){
        return userDao.getAllUsers();
    }




}
