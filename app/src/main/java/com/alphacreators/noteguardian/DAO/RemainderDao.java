package com.alphacreators.noteguardian.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alphacreators.noteguardian.ENTITY.RNote;

import java.util.List;

@Dao
public interface RemainderDao {

    @Insert
    void RemainderInsert(RNote rNote);

    @Update
    void RemainderUpdate(RNote rNote);

    @Delete
    void RemainderDelete(RNote rNote);


    @Query("SELECT * FROM remainder_table ORDER BY Rid ASC")
    LiveData<List<RNote>> getAllRemainderNotes();

    @Query("SELECT * FROM remainder_table ORDER BY R_Note_Date DESC")
    LiveData<List<RNote>> getRemainderNotesSortByDate();

    @Query("SELECT * FROM remainder_table ORDER BY CASE WHEN R_Priority = 'HIGH' THEN 1 WHEN R_Priority = 'MEDIUM' THEN 2 ELSE 3 END")
    LiveData<List<RNote>> getRemainderNotesSortByPriority();

    @Query("SELECT * FROM remainder_table ORDER BY R_favourite_note DESC")
    LiveData<List<RNote>> getRemainderNotesSortByFavorite();
}
