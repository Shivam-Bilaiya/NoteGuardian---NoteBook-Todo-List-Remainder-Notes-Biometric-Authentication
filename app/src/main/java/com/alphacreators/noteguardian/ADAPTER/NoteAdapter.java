package com.alphacreators.noteguardian.ADAPTER;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacreators.noteguardian.ENTITY.Note;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.NOTEUTILITY.Utils;
import com.alphacreators.noteguardian.CLICKINTERFACE.onItemClickListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {


    private List<Note> notes = new ArrayList<>();

    private List<Note> noteSource;

    private onItemClickListener listener;

    private final List<Note> pinnedNotes = new ArrayList<>();

    private final List<Note> unPinnedNotes = new ArrayList<>();


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

        Note currentNode;
        if (position < pinnedNotes.size()) {
            currentNode = pinnedNotes.get(position);
        } else {
            int unpinnedPosition = position - pinnedNotes.size();
            currentNode = unPinnedNotes.get(unpinnedPosition);
        }

        holder.textViewTitle.setText(currentNode.getTitle());
        holder.textViewDescription.setText(currentNode.getDescription());
        String imageUri = currentNode.getImage();
        String drawImageUri = currentNode.getDrawImageUri();

        if (!imageUri.isEmpty() && !drawImageUri.isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageURI(Uri.parse(imageUri));
            holder.drawImage.setVisibility(View.VISIBLE);
            holder.drawImage.setImageURI(Uri.parse(drawImageUri));
        } else if (!imageUri.isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageURI(Uri.parse(imageUri));
            holder.drawImage.setVisibility(View.GONE);
        } else if (!drawImageUri.isEmpty()) {
            holder.imageView.setVisibility(View.GONE);
            holder.drawImage.setVisibility(View.VISIBLE);
            holder.drawImage.setImageURI(Uri.parse(drawImageUri));
        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.drawImage.setVisibility(View.GONE);
        }
        holder.dateChip.setText(convertDateToString(currentNode.getNoteDate()));
        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        }, new int[]{
                Color.LTGRAY,
                Utils.notePriorityColor(currentNode)
        });
        holder.notePriorityRadioButton.setButtonTintList(colorStateList);
        holder.dateChip.setTextColor(colorStateList);
        holder.dateChip.setBackgroundTintList(colorStateList);
        holder.noteLinearLayout.setBackgroundColor(currentNode.getBackgroundColor());
        if (currentNode.isFavouriteNote()) {
            holder.cardFavouriteNote.setImageResource(R.drawable.lover);
        } else {
            holder.cardFavouriteNote.setImageResource(0);
        }

        if (currentNode.isPinned()) {
            holder.cardPinNote.setImageResource(R.drawable.pin_note);
        } else {
            holder.cardPinNote.setImageResource(0);
        }


    }

    private String convertDateToString(LocalDate noteDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return noteDate.format(dateTimeFormatter);
    }


    @Override
    public int getItemCount() {
        if (notes == null) {
            return -1;
        }
        int itemCount = notes.size();
        Log.d("ItemCount", "ItemCount: " + itemCount);
        return itemCount;
    }


    public void setNotes(List<Note> notes) {
        this.notes = notes;
        noteSource = notes;
        separatePinnedAndUnpinnedNotes();
        notifyDataSetChanged();
    }

    private void separatePinnedAndUnpinnedNotes() {

        if (notes == null){
            return;
        }
        pinnedNotes.clear();
        unPinnedNotes.clear();

        for (Note note : notes) {
            if (note.isPinned()) {
                pinnedNotes.add(note);
            } else {
                unPinnedNotes.add(note);
            }
        }

        Log.d("Separation", "Pinned Notes: " + pinnedNotes.size() + ", Unpinned Notes: " + unPinnedNotes.size());

        notes.clear();
        notes.addAll(pinnedNotes);
        notes.addAll(unPinnedNotes);
    }


    public Note getNotes(int position) {
        return notes.get(position);
    }


    class NoteHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewDescription;
        ImageView imageView;
        ImageView drawImage;
        TextView dateChip;
        CardView cardView;
        RelativeLayout noteLayout;
        ImageView cardFavouriteNote;
        RelativeLayout relativeLayout;
        AppCompatRadioButton notePriorityRadioButton;
        LinearLayout noteLinearLayout;
        ImageView cardPinNote;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.cardViewImage);
            drawImage = itemView.findViewById(R.id.cardViewDrawImage);
            dateChip = itemView.findViewById(R.id.dateChip);
            cardView = itemView.findViewById(R.id.cardView);
            noteLayout = itemView.findViewById(R.id.noteLayout);
            cardFavouriteNote = itemView.findViewById(R.id.cardFavouriteNote);
            relativeLayout = itemView.findViewById(R.id.cardRelative);
            notePriorityRadioButton = itemView.findViewById(R.id.notePriorityRadioButton);
            noteLinearLayout = itemView.findViewById(R.id.noteLinearLayout);
            cardPinNote = itemView.findViewById(R.id.cardPinNote);


            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();

                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(notes.get(position));
                }

            });


        }
    }


    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;

    }


    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Timer timer;

    public void searchNotes(final String search) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (search.trim().isEmpty()) {
                    notes = noteSource;
                    separatePinnedAndUnpinnedNotes();

                } else {
                    Log.d("ipowpdwpwpd","wkldnwkdlnwdklwn");
                    ArrayList<Note> temp = new ArrayList<>();
                    ArrayList<Note> tempPinned = new ArrayList<>();
                    ArrayList<Note> tempUnpinned = new ArrayList<>();

                    for (Note note : noteSource) {
                        if (note.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                                note.getDescription().toLowerCase().contains(search.toLowerCase())) {
                            temp.add(note);
                            if (note.isPinned()) {
                                tempPinned.add(note);
                            } else {
                                tempUnpinned.add(note);
                            }
                        }
                    }

                    notes = temp;
                    pinnedNotes.clear();
                    pinnedNotes.addAll(tempPinned);
                    unPinnedNotes.clear();
                    unPinnedNotes.addAll(tempUnpinned);

                    separatePinnedAndUnpinnedNotes();
                }

                searchHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }, 0);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}


