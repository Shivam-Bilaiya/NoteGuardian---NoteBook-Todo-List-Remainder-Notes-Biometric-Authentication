package my.app.snotes;

import androidx.cardview.widget.CardView;

public interface NotesListener {
    void onLongClick(Note note, CardView cardView);
}
