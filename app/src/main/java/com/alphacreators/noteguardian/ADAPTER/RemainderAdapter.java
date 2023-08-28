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
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacreators.noteguardian.ENTITY.RNote;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.NOTEUTILITY.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.RemainderNoteHolder>{
    private List<RNote> rNotes = new ArrayList<>();


    private List<RNote> noteSource;

    private onRItemClickListener RListener;

    private final List<RNote> pinnedNotes = new ArrayList<>();

    private final List<RNote> unPinnedNotes = new ArrayList<>();


    @NonNull
    @Override
    public RemainderNoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent,false);
        return new RemainderNoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemainderNoteHolder holder, int position) {
        RNote currentNode;
        if (position < pinnedNotes.size()) {
            currentNode = pinnedNotes.get(position);
        } else {
            currentNode = unPinnedNotes.get(position - pinnedNotes.size());
        }
        holder. RTextViewTitle.setText(currentNode.getRTitle());
        String imageUri = currentNode.getRImage();
        String drawImageUri = currentNode.getDrawImageUri();
        holder.RTextViewDescription.setText(currentNode.getRDescription());
        holder.RImageView.setImageURI(Uri.parse(currentNode.getRImage()));
        holder.drawImage.setImageURI(Uri.parse(currentNode.getDrawImageUri()));
        if (!imageUri.isEmpty() && !drawImageUri.isEmpty()) {
            holder.RImageView.setVisibility(View.VISIBLE);
            holder.RImageView.setImageURI(Uri.parse(imageUri));
            holder.drawImage.setVisibility(View.VISIBLE);
            holder.drawImage.setImageURI(Uri.parse(drawImageUri));
        } else if (!imageUri.isEmpty()) {
            holder.RImageView.setVisibility(View.VISIBLE);
            holder.RImageView.setImageURI(Uri.parse(imageUri));
            holder.drawImage.setVisibility(View.GONE);
        } else if (!drawImageUri.isEmpty()) {
            holder.RImageView.setVisibility(View.GONE);
            holder.drawImage.setVisibility(View.VISIBLE);
            holder.drawImage.setImageURI(Uri.parse(drawImageUri));
        } else {
            holder.RImageView.setVisibility(View.GONE);
            holder.drawImage.setVisibility(View.GONE);
        }
        Log.d("kefkbfwkfbwkf",currentNode.getRPriority().toString());
        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        },new int[]{
                Color.LTGRAY,
                Utils.remainderNotePriorityColor(currentNode)
        });
        holder.radioButton.setButtonTintList(colorStateList);
        String date = currentNode.getRNoteDate();
        holder.RDateTime.setText(date);
        holder.RDateTime.setTextColor(colorStateList);
        holder.RDateTime.setBackgroundTintList(colorStateList);
        holder.RNoteLayout.setBackgroundColor(currentNode.getRBackgroundColor());

        if (currentNode.isRFavouriteNote()){
            holder.favouriteNote.setImageResource(R.drawable.lover);
        }
        else{
            holder.favouriteNote.setImageResource(0);
        }

        if (currentNode.isRPinned()) {
            holder.pinNote.setImageResource(R.drawable.pin_note);
        } else {
            holder.pinNote.setImageResource(0);
        }
    }





    @Override
    public int getItemCount() {
        if( rNotes== null)
        {
            return -1;
        }
        if(rNotes.size()==0)
            return -1;
        return rNotes.size();
    }



    public void RSetNotes(List<RNote> notes)
    {
        this.rNotes = notes;
        noteSource = notes;
        separatePinnedAndUnpinnedNotes();
        notifyDataSetChanged();
    }

    private void separatePinnedAndUnpinnedNotes() {
        pinnedNotes.clear();
        unPinnedNotes.clear();

        for (RNote note : rNotes) {
            if (note.isRPinned()) {
                pinnedNotes.add(note);
            } else {
                unPinnedNotes.add(note);
            }
        }

        rNotes.clear();
        rNotes.addAll(pinnedNotes);
        rNotes.addAll(unPinnedNotes);
    }

    public RNote getRNotes(int position)
    {
        return rNotes.get(position);
    }




    class RemainderNoteHolder extends RecyclerView.ViewHolder{

        TextView RTextViewTitle;
        TextView RTextViewDescription;
        ImageView RImageView;
        TextView RDateTime;
        CardView RCardView;
        LinearLayout RNoteLayout;
        ImageView favouriteNote;
        ImageView pinNote;
        RadioButton radioButton;
        ImageView drawImage;


        public RemainderNoteHolder(@NonNull View itemView) {
            super(itemView);
            RTextViewTitle = itemView.findViewById(R.id.RTextViewTitle);
            RTextViewDescription = itemView.findViewById(R.id.RTextViewDescription);
            RImageView = itemView.findViewById(R.id.RCardViewImage);
            drawImage = itemView.findViewById(R.id.RCardViewDrawImage);
            RDateTime = itemView.findViewById(R.id.RDateTime);
            RCardView = itemView.findViewById(R.id.RCardView);
            RNoteLayout = itemView.findViewById(R.id.RLayout);
            favouriteNote = itemView.findViewById(R.id.RCardFavouriteNote);
            pinNote = itemView.findViewById(R.id.RCardPinNote);
            radioButton = itemView.findViewById(R.id.RNotePriorityRadioButton);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(RListener!=null && position!=RecyclerView.NO_POSITION){
                    RListener.onItemClick(rNotes.get(position));
                }
            });

        }
    }

    public interface  onRItemClickListener{
        void onItemClick(RNote note);
    }

    public void setOnItemClickListener(onRItemClickListener RListener){
        this.RListener = RListener;
    }



    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Timer timer;

    public void searchNotes(final String search)
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (search.trim().isEmpty())
                {
                    rNotes.clear();
                    rNotes = noteSource;
                    separatePinnedAndUnpinnedNotes();
                }
                else {
                    Log.d("ipowpdwpwpd","wkldnwkdlnwdklwn");
                    ArrayList<RNote> temp = new ArrayList<>();
                    ArrayList<RNote> tempPinned = new ArrayList<>();
                    ArrayList<RNote> tempUnpinned = new ArrayList<>();

                    for (RNote note : noteSource) {
                        if (note.getRTitle().toLowerCase().contains(search.toLowerCase()) ||
                                note.getRDescription().toLowerCase().contains(search.toLowerCase())) {
                            temp.add(note);
                            if (note.isRPinned()) {
                                tempPinned.add(note);
                            } else {
                                tempUnpinned.add(note);
                            }
                        }
                    }

                    rNotes = temp;
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
