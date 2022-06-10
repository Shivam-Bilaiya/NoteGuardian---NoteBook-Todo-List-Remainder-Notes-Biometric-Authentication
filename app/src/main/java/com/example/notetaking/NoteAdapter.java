package com.example.notetaking;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.util.*;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {


    private List<Note> notes = new ArrayList<>();

    private Timer timer;

    private List<Note> noteSource;

    private onItemClickListener listener;




    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent,false);
        return new NoteHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

        Note currentNode = notes.get(position);
        holder.textViewTitle.setText(currentNode.getTitle());
        holder.textViewDescription.setText(currentNode.getDescription());
        holder.imageView.setImageBitmap(BitmapFactory.decodeByteArray(currentNode.getImage(), 0,currentNode.getImage().length));
        holder.dateTime.setText(currentNode.getNoteDate());
        holder.dateTime.setSelected(true);


    }


    @Override
    public int getItemCount() {
        if(notes == null)
        {
            return -1;
        }
        if(notes.size()==0)
            return -1;
        return notes.size();
    }



    public void setNotes(List<Note> notes)
    {
        this.notes = notes;
        noteSource = notes;
        notifyDataSetChanged();
    }




    public Note getNotes(int position)
    {
        return notes.get(position);
    }




    class NoteHolder extends RecyclerView.ViewHolder{

        TextView textViewTitle;
        TextView textViewDescription;
        ImageView imageView;
        TextView dateTime;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.cardViewImage);
            dateTime = itemView.findViewById(R.id.dateTime);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null && position != RecyclerView.NO_POSITION)
                    {
                        listener.onItemClick(notes.get(position));
                    }
                }
            });
        }
    }


    public interface onItemClickListener
    {
           void onItemClick(Note note);
    }

    public void setOnItemClickListener(onItemClickListener listener)
    {
        this.listener = listener;

    }


    public void searchNotes(final String search)
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (search.trim().isEmpty())
                {
                    notes = noteSource;
                }
                else {
                    ArrayList<Note> temp = new ArrayList<>();

                    for (Note note : noteSource)
                    {
                       if (note.getTitle().toLowerCase().contains(search.toLowerCase())|| note.getDescription().toLowerCase().contains(search.toLowerCase()))
                       {
                           temp.add(note);
                       }
                    }

                    notes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run()  {
                        notifyDataSetChanged();
                    }
                });

            }
        },0);
    }

    public void cancelTimer()
    {
        if (timer!=null)
        {
            timer.cancel();
        }
    }
}
