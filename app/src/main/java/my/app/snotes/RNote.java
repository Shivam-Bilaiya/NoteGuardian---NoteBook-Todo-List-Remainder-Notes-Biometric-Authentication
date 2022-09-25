package my.app.snotes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "remainder_table")
public class RNote {

    @PrimaryKey(autoGenerate = true)
    public int Rid;

    @ColumnInfo(name = "R_Title")
    public String RTitle;

    @ColumnInfo(name = "R_Description")
    public String RDescription;

    @ColumnInfo(name = "R_Image")
    public byte[] RImage;

    @ColumnInfo(name = "R_Note_Date")
    public String RNoteDate;

    @ColumnInfo(name = "R_Pinned")
    public boolean RPinned = false;

    public int getRid() {
        return Rid;
    }

    public void setRid(int rid) {
        Rid = rid;
    }

    public String getRTitle() {
        return RTitle;
    }

    public RNote(String RTitle, String RDescription, byte[] RImage, String RNoteDate) {
        this.RTitle = RTitle;
        this.RDescription = RDescription;
        this.RImage = RImage;
        this.RNoteDate = RNoteDate;

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

    public byte[] getRImage() {
        return RImage;
    }

    public void setRImage(byte[] RImage) {
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
}
