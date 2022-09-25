package my.app.snotes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
}
