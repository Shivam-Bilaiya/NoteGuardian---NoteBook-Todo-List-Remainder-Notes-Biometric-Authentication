package com.alphacreators.noteguardian.NOTEUTILITY;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alphacreators.noteguardian.ENTITY.Note;
import com.alphacreators.noteguardian.ENTITY.RNote;
import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.PRIORITY.Priority;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        simpleDateFormat.applyPattern("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static void hideSoftKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static int priorityColor(TodoTask todoTask) {
        int color;

        if (todoTask.getPriority() == Priority.HIGH){
            color = Color.argb(200,201,21,23);
        }
        else if (todoTask.getPriority() == Priority.MEDIUM){
            color = Color.argb(200,155,179,0);
        }else {
            color = Color.argb(200,51,181,129);
        }
        return color;
    }


    public static int notePriorityColor(Note note){
        int color;

        if (note.getNotePriority() == Priority.HIGH){
            color = Color.argb(200,201,21,23);
        }
        else if (note.getNotePriority() == Priority.MEDIUM){
            color = Color.argb(200,155,179,0);
        }else {
            color = Color.argb(200,51,181,129);
        }
        return color;
    }

    public static int remainderNotePriorityColor(RNote rNote){
        int color;
        if (rNote.getRPriority() == Priority.HIGH){
            color = Color.argb(200,201,21,23);
        }
        else if (rNote.getRPriority() == Priority.MEDIUM){
            color = Color.argb(200,155,179,0);
        }else {
            color = Color.argb(200,51,181,129);
        }
        return color;
    }


}
