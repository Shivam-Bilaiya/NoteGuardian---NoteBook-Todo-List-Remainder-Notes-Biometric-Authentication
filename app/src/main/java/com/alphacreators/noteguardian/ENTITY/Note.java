package com.alphacreators.noteguardian.ENTITY;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.NOTEUTILITY.TypeConvertor;

import java.time.LocalDate;

@Entity(tableName = "note_table")
@TypeConverters(TypeConvertor.class)
public class Note {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // id is for to accessing a particular note in db

    @ColumnInfo(name = "notes_title")
    public String title;

    @ColumnInfo(name = "notes_description")
    public String description;

    @ColumnInfo(name = "note_image")
    String imageUri;

    @ColumnInfo(name = "note_date")
    public LocalDate noteDate;

    @ColumnInfo(name = "note_priority")
    public Priority notePriority;

    @ColumnInfo(name = "background_color")
    public int backgroundColor;

    @ColumnInfo(name = "favourite_notes")
    public boolean favouriteNote;

    @ColumnInfo(name = "pinned")
    public boolean pinned;

    @ColumnInfo(name = "draw_image_uri")
    public String drawImageUri;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public LocalDate getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(LocalDate noteDate) {
        this.noteDate = noteDate;
    }

    public Note(String title, String description , String imageUri , LocalDate noteDate,Priority notePriority,int backgroundColor,boolean favouriteNote,boolean pinned,String drawImageUri) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.noteDate = noteDate;
        this.notePriority = notePriority;
        this.backgroundColor = backgroundColor;
        this.favouriteNote = favouriteNote;
        this.pinned = pinned;
        this.drawImageUri = drawImageUri;
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

    public String getImage() {
        return imageUri;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isFavouriteNote() {
        return favouriteNote;
    }

    public void setFavouriteNote(boolean favouriteNote) {
        this.favouriteNote = favouriteNote;
    }


    public Priority getNotePriority() {
        return notePriority;
    }

    public void setNotePriority(Priority notePriority) {
        this.notePriority = notePriority;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDrawImageUri() {
        return drawImageUri;
    }

    public void setDrawImageUri(String drawImageUri) {
        this.drawImageUri = drawImageUri;
    }

    @Override
    public String toString() {
        return "Note{" +
                " title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", noteDate=" + noteDate +
                ", notePriority=" + notePriority +
                ", backgroundColor=" + backgroundColor +
                ", favouriteNote=" + favouriteNote +
                ", pinned=" + pinned +
                '}';
    }
}
