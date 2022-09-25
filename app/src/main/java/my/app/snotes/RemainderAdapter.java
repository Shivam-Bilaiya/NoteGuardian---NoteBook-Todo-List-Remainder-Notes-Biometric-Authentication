package my.app.snotes;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RemainderAdapter extends RecyclerView.Adapter<RemainderAdapter.RemainderNoteHolder>{
    private List<RNote> rNotes = new ArrayList<>();

    private Timer timer;

    private List<RNote> noteSource;

    private onRItemClickListener RListener;

    @NonNull
    @Override
    public RemainderNoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent,false);
        return new RemainderNoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemainderNoteHolder holder, int position) {
        RNote currentNode = rNotes.get(position);
        holder. RTextViewTitle.setText(currentNode.getRTitle());
        holder.RTextViewDescription.setText(currentNode.getRDescription());
        holder.RImageView.setImageBitmap(BitmapFactory.decodeByteArray(currentNode.getRImage(), 0,currentNode.getRImage().length));
        holder.RDateTime.setText(currentNode.getRNoteDate());
        holder.RDateTime.setSelected(true);

//        int color = getRandomColor();
//        holder.RNoteLayout.setBackgroundColor(holder.RNoteLayout.getResources().getColor(color,null));

    }

//    private int getRandomColor(){
//        List<Integer> list = new ArrayList<>();
//        list.add(R.color.color1);
//        list.add(R.color.color2);
//        list.add(R.color.color3);
//        list.add(R.color.color4);
//        list.add(R.color.color5);
//        Random random = new Random();
//        int color = random.nextInt(list.size());
//        return list.get(color);
//    }



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
        notifyDataSetChanged();
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



        public RemainderNoteHolder(@NonNull View itemView) {
            super(itemView);
            RTextViewTitle = itemView.findViewById(R.id.RTextViewTitle);
            RTextViewDescription = itemView.findViewById(R.id.RTextViewDescription);
            RImageView = itemView.findViewById(R.id.RCardViewImage);
            RDateTime = itemView.findViewById(R.id.RDateTime);
            RCardView = itemView.findViewById(R.id.RCardView);
            RNoteLayout = itemView.findViewById(R.id.RNoteLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(RListener!=null && position!=RecyclerView.NO_POSITION){
                        RListener.onItemClick(rNotes.get(position));
                    }
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




    public void searchNotes(final String search)
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (search.trim().isEmpty())
                {
                    rNotes = noteSource;
                }
                else {
                    ArrayList<RNote> temp = new ArrayList<>();

                    for (RNote rNote : noteSource)
                    {
                        if (rNote.getRTitle().toLowerCase().contains(search.toLowerCase())|| rNote.getRDescription().toLowerCase().contains(search.toLowerCase()))
                        {
                            temp.add(rNote);
                        }
                    }

                    rNotes = temp;
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
