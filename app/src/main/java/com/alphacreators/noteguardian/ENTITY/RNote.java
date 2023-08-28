package com.alphacreators.noteguardian.ENTITY;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.NOTEUTILITY.TypeConvertor;

@Entity(tableName = "remainder_table")
@TypeConverters(TypeConvertor.class)
public class RNote {

    @PrimaryKey(autoGenerate = true)
    public int Rid;

    @ColumnInfo(name = "R_Title")
    public String RTitle;

    @ColumnInfo(name = "R_Description")
    public String RDescription;

    @ColumnInfo(name = "R_Image")
    public String RImage;

    @ColumnInfo(name = "R_Note_Date")
    public String RNoteDate;

    @ColumnInfo(name = "R_Pinned")
    public boolean RPinned = false;

    @ColumnInfo(name = "R_background_color")
    public int RBackgroundColor;

    @ColumnInfo(name = "R_favourite_note")
    public boolean RFavouriteNote;

    @ColumnInfo(name = "R_Priority")
    public Priority RPriority;

    @ColumnInfo(name = "R_draw_image_uri")
    public String drawImageUri;



    public int getRid() {
        return Rid;
    }

    public void setRid(int rid) {
        Rid = rid;
    }

    public String getRTitle() {
        return RTitle;
    }


    public RNote(String RTitle, String RDescription, String RImage, String RNoteDate, boolean RPinned, int RBackgroundColor, boolean RFavouriteNote, String drawImageUri,Priority RPriority) {
        this.RTitle = RTitle;
        this.RDescription = RDescription;
        this.RImage = RImage;
        this.RNoteDate = RNoteDate;
        this.RPinned = RPinned;
        this.RBackgroundColor = RBackgroundColor;
        this.RFavouriteNote = RFavouriteNote;
        this.drawImageUri = drawImageUri;
        this.RPriority = RPriority;
    }

    public void setRTitle(String RTitle) {
        this.RTitle = RTitle;
    }

    public String getRDescription() {
        return RDescription;
    }

    public void setRDescription(String RDescription) {
        this.RDescription = RDescription;
    }

    public String getRImage() {
        return RImage;
    }

    public void setRImage(String RImage) {
        this.RImage = RImage;
    }

    public String getRNoteDate() {
        return RNoteDate;
    }

    public void setRNoteDate(String RNoteDate) {
        this.RNoteDate = RNoteDate;
    }

    public boolean isRPinned() {
        return RPinned;
    }

    public void setRPinned(boolean RPinned) {
        this.RPinned = RPinned;
    }

    public int getRBackgroundColor() {
        return RBackgroundColor;
    }

    public void setRBackgroundColor(int RBackgroundColor) {
        this.RBackgroundColor = RBackgroundColor;
    }

    public boolean isRFavouriteNote() {
        return RFavouriteNote;
    }

    public void setRFavouriteNote(boolean RFavouriteNote) {
        this.RFavouriteNote = RFavouriteNote;
    }

    public Priority getRPriority() {
        return RPriority;
    }

    public void setRPriority(Priority RPriority) {
        this.RPriority = RPriority;
    }

    public String getDrawImageUri() {
        return drawImageUri;
    }

    public void setDrawImageUri(String drawImageUri) {
        this.drawImageUri = drawImageUri;
    }
}
