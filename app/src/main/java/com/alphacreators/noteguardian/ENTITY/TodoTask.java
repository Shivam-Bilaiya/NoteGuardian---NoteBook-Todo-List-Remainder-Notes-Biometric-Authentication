package com.alphacreators.noteguardian.ENTITY;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alphacreators.noteguardian.PRIORITY.Priority;

import java.util.Date;

@Entity(tableName = "todo_list")
public class TodoTask {

    @PrimaryKey(autoGenerate = true)
    public int todoId;

    @ColumnInfo(name = "todo_task")
    public String todoTask;

    public Priority priority;

    @ColumnInfo(name = "todo_completed")
    public boolean completed;


    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @ColumnInfo(name = "due_date")
    public Date dueDate;

    @ColumnInfo(name = "todo_date")
    public Date todoDate;

    @ColumnInfo(name = "todo_favourite_note")
    public boolean todoFavouriteNote;

    @ColumnInfo(name = "remainder_background_color")
    public int remainderBackgroundColor;


    @NonNull
    @Override
    public String toString() {
        return "TodoTask{" +
                "todoId=" + todoId +
                ", todoTask='" + todoTask + '\'' +
                ", priority=" + priority +
                ", completed=" + completed +
                ", dueDate=" + dueDate +
                ", todoDate=" + todoDate +
                '}';
    }

    public TodoTask(String todoTask, Priority priority, boolean completed, Date dueDate, Date todoDate,boolean todoFavouriteNote,int remainderBackgroundColor) {
        this.todoTask = todoTask;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = completed;
        this.todoDate = todoDate;
        this.todoFavouriteNote = todoFavouriteNote;
        this.remainderBackgroundColor = remainderBackgroundColor;
    }


    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public String getTodoTask() {
        return todoTask;
    }

    public void setTodoTask(String todoTask) {
        this.todoTask = todoTask;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Date getTodoDate() {
        return todoDate;
    }

    public void setTodoDate(Date todoDate) {
        this.todoDate = todoDate;
    }


    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public boolean isTodoFavouriteNote() {
        return todoFavouriteNote;
    }

    public void setTodoFavouriteNote(boolean todoFavouriteNote) {
        this.todoFavouriteNote = todoFavouriteNote;
    }

    public int getRemainderBackgroundColor() {
        return remainderBackgroundColor;
    }

    public void setRemainderBackgroundColor(int remainderBackgroundColor) {
        this.remainderBackgroundColor = remainderBackgroundColor;
    }
}
