package com.alphacreators.noteguardian.DAO;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alphacreators.noteguardian.ENTITY.Note;

import java.util.List;

@Dao
public interface NoteDAO {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM note_table ORDER BY id ASC")
    LiveData<List<Note>> getAllNotes();

    @Query("SELECT * FROM note_table ORDER BY note_date DESC")
    LiveData<List<Note>> getNotesSortByDate();

    @Query("SELECT * FROM note_table ORDER BY CASE WHEN note_priority = 'HIGH' THEN 1 WHEN note_priority = 'MEDIUM' THEN 2 ELSE 3 END")
    LiveData<List<Note>> getNotesSortByPriority();

    @Query("SELECT * FROM note_table ORDER BY favourite_notes DESC")
    LiveData<List<Note>> getNotesSortByFavorite();




}
