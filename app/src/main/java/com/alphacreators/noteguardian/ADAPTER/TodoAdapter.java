package com.alphacreators.noteguardian.ADAPTER;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.NOTEUTILITY.Utils;
import com.alphacreators.noteguardian.CLICKINTERFACE.onTodoClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoTaskHolder> {

    private List<TodoTask> todoNotes = new ArrayList<>();

    private final onTodoClickListener todoClickListener;

    private Timer todoTimer;

    private List<TodoTask> todoNotesSource = new ArrayList<>();

    private onTodoItemClickListener todoListener;

    public TodoAdapter(List<TodoTask> todoTasks, onTodoClickListener onTodoClickListener){
        this.todoNotes = todoTasks;
        this.todoClickListener = onTodoClickListener;
    }


    @NonNull
    @Override
    public TodoTaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_card, parent,false);
        return new TodoTaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoTaskHolder holder, int position) {

       TodoTask todoTask = todoNotes.get(position);
       String formatted  = Utils.formatDate(todoTask.getDueDate());
        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        },new int[]{
                Color.LTGRAY,
                Utils.priorityColor(todoTask)
        });
       holder.todoTask.setText(todoTask.getTodoTask());
       holder.todayChip.setText(formatted);
       holder.todayChip.setTextColor(Utils.priorityColor(todoTask));
       holder.todayChip.setTextColor(colorStateList);
       holder.radioButton.setButtonTintList(colorStateList);
       holder.layout.setBackgroundColor(todoTask.getRemainderBackgroundColor());




    }

    @Override
    public int getItemCount() {
        if (todoNotes == null){
            return -1;
        }
        if (todoNotes.size() == 0){
            return -1;
        }
        return todoNotes.size();
    }

    public void setTodoNotes(List<TodoTask> todoTasks){
        this.todoNotes = todoTasks;
        todoNotesSource = todoTasks;
        notifyDataSetChanged();
    }

    public TodoTask getTodoTask(int position){
        return todoNotes.get(position);
    }

    public  class TodoTaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public AppCompatRadioButton radioButton;
        public AppCompatTextView todoTask;
        public TextView todayChip;
        CardView cardView;
        LinearLayout layout;

        onTodoClickListener onTodoClickListener;

        public TodoTaskHolder(@NonNull View itemView) {
            super(itemView);

           radioButton = itemView.findViewById(R.id.todo_radio_button);
           todoTask = itemView.findViewById(R.id.todo_row_todo);
           todayChip = itemView.findViewById(R.id.todo_row_chip);
           cardView = itemView.findViewById(R.id.todo_row_layout);
           layout = itemView.findViewById(R.id.todoCardLayout);
           this.onTodoClickListener = todoClickListener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(todoListener!=null && position!=RecyclerView.NO_POSITION){
                        todoListener.onItemClick(todoNotes.get(position));
                    }
                }
            });

            itemView.setOnClickListener(this);
            radioButton.setOnClickListener(this);










        }

        @Override
        public void onClick(View view) {
           int id = view.getId();
            TodoTask todoTask = todoNotes.get(getAdapterPosition());


            if (id == R.id.todo_row_layout){
               onTodoClickListener.onTodoClick(todoTask);
           }
           else if (id == R.id.todo_radio_button){
               onTodoClickListener.onTodoRadioButtonClicked(todoTask);
           }
        }
    }

    public interface  onTodoItemClickListener{
        void onItemClick(TodoTask note);
    }

    public void setOnItemClickListener(onTodoItemClickListener todoListener){
        this.todoListener = todoListener;
    }


    public void searchNotes(final String search)
    {
        todoTimer = new Timer();
        todoTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (search.trim().isEmpty())
                {
                    todoNotes.clear();
                    todoNotes = todoNotesSource;
                }
                else {
                    ArrayList<TodoTask> temp = new ArrayList<>();

                    for (TodoTask todoTask : todoNotes)
                    {
                        if (todoTask.getTodoTask().toLowerCase().contains(search.toLowerCase()))
                        {
                            temp.add(todoTask);
                        }
                    }

                    todoNotes = temp;
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
        if (todoTimer!=null)
        {
            todoTimer.cancel();
        }
    }


}
