package com.example.notetaking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // id is for to accessing a particular note in db

    @ColumnInfo(name = "notes_title")
    public String title;

    @ColumnInfo(name = "notes_description")
    public String description;

    @ColumnInfo(name = "note_image")
    byte[] image;

    @ColumnInfo(name = "note_date")
    public String noteDate;


    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public Note(String title, String description , byte[] image , String noteDate) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.noteDate = noteDate;


    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }


}
