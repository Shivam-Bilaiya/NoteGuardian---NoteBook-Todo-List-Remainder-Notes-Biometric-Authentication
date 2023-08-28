package com.alphacreators.noteguardian.TODO;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alphacreators.noteguardian.ENTITY.TodoTask;
import com.alphacreators.noteguardian.PRIORITY.Priority;
import com.alphacreators.noteguardian.R;
import com.alphacreators.noteguardian.NOTEUTILITY.Utils;
import com.alphacreators.noteguardian.VIEWMODEL.NoteViewModel;
import com.alphacreators.noteguardian.VIEWMODEL.SharedViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private EditText enterTodo;
    private ImageView calendarButton;
    private ImageView priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;
    boolean check = false;
    private ImageView taskColor;
    int defaultColor;
    int color;
    boolean update = false;
    boolean changeColor = false;

    public BottomSheetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);
        taskColor = view.findViewById(R.id.todoColor);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        tomorrowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedViewModel.getSelectedItems().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();
            if (isEdit) {
                TodoTask todoTask = sharedViewModel.getSelectedItems().getValue();
                enterTodo.setText(todoTask.getTodoTask());


            }
//


        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        defaultColor = ContextCompat.getColor(requireContext(), R.color.noteBackgroundColor);

        calendarButton.setOnClickListener(view13 -> calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
        Utils.hideSoftKeyboard(view);

        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            update = true;

            calendar.clear();
            calendar.set(year, month, dayOfMonth);
            dueDate = calendar.getTime();

        });

        priorityButton.setOnClickListener(view12 -> {
            Utils.hideSoftKeyboard(view12);
            priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                if (priorityRadioGroup.getVisibility() == View.VISIBLE) {
                    selectedButtonId = checkedId;
                    selectedRadioButton = view.findViewById(selectedButtonId);
                    if (selectedRadioButton.getId() == R.id.radioButton_high) {
                        priority = Priority.HIGH;
                    } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                        priority = Priority.MEDIUM;
                    } else if (selectedRadioButton.getId() == R.id.radioButton_low) {
                        priority = Priority.LOW;
                    } else {
                        priority = Priority.LOW;
                    }
                } else {
                    priority = Priority.LOW;
                }

            });
        });


        taskColor.setOnClickListener(view15 -> {
            Utils.hideSoftKeyboard(view15);
            color = openColorPicker();
        });

        saveButton.setOnClickListener(view1 -> {
            String task = enterTodo.getText().toString().trim();

            if (!TextUtils.isEmpty(task) && dueDate != null) {
                TodoTask todoTask = new TodoTask(task, priority, false, dueDate, Calendar.getInstance().getTime(), check, defaultColor);
                if (isEdit) {
                    TodoTask updateTask = sharedViewModel.getSelectedItems().getValue();
                    assert updateTask != null;
                    updateTask.setTodoTask(task);
                    updateTask.setTodoDate(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    if (!update) {
                        updateTask.setDueDate(updateTask.getDueDate());
                    } else {
                        updateTask.setDueDate(dueDate);
                    }
                    updateTask.setDueDate(dueDate);
                    if (updateTask.isTodoFavouriteNote()) {
                        updateTask.setTodoFavouriteNote(false);
                        check = true;
                    } else {
                        updateTask.setTodoFavouriteNote(true);
                        check = false;

                    }
                    if (changeColor) {
                        updateTask.setRemainderBackgroundColor(defaultColor);
                    } else {
                        updateTask.setRemainderBackgroundColor(updateTask.getRemainderBackgroundColor());
                    }
                    NoteViewModel.todoUpdate(updateTask);
                    sharedViewModel.setIsEdit(false);
                } else {
                    NoteViewModel.todoInsert(todoTask);
                }

                enterTodo.setText("");

                if (this.isVisible()) {
                    this.dismiss();
                }


            } else if (task.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.empty), Toast.LENGTH_SHORT).show();
            } else if (dueDate == null) {
                Toast.makeText(getContext(), getString(R.string.unempty), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int openColorPicker() {
        changeColor = true;
        ColorPicker colorPicker = new ColorPicker(requireActivity());
        ArrayList<String> colorList = new ArrayList<>();
        colorList.add("#EB4034");
        colorList.add("#ff8000");
        colorList.add("#0040ff");
        colorList.add("#00ffff");
        colorList.add("#660033");
        colorList.add("#ffcc66");
        colorList.add("#cc6699");
        colorList.add("#663300");
        colorList.add("#ccffff");
        colorList.add("#666699");
        colorList.add("#999966");
        colorList.add("#FCF6F5FF");
        colorList.add("#323232");
        colorList.add("#186a3b");
        colorList.add("#1abc9c");
        colorList.add("#0040ff");

        colorPicker.setColors(colorList);
        colorPicker.setColumns(4);
        colorPicker.setRoundColorButton(true);
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position, int color) {
                defaultColor = color;
            }

            @Override
            public void onCancel() {
                // put code
            }
        });


        return defaultColor;

    }


    // YE TODAY , TOMORROW AND NEXT CHIP KE LITE HAI

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.today_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calendar.getTime();
        } else if (id == R.id.tomorrow_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calendar.getTime();
        } else if (id == R.id.next_week_chip) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            dueDate = calendar.getTime();
        }
    }
}
