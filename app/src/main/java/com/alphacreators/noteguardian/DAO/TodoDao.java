package com.alphacreators.noteguardian.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alphacreators.noteguardian.ENTITY.TodoTask;

import java.util.List;

@Dao
public interface TodoDao {

    @Insert
    void todoInsert(TodoTask todoTask);

    @Update
    void todoUpdate(TodoTask todoTask);

    @Delete
    void todoDelete(TodoTask todoTask);

   @Query("SELECT * FROM todo_list ORDER BY todoId ASC")
    LiveData<List<TodoTask>> getAllTodoTask();

   @Query("DELETE FROM todo_list")
    void deleteAllTask();

   @Query("SELECT * FROM todo_list WHERE todo_list.todoId == :todoId")
    LiveData<TodoTask> getSingleTask(int todoId);
}
