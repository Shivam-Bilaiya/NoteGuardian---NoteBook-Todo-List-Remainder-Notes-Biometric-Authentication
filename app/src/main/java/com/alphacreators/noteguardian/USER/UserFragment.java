package com.alphacreators.noteguardian.USER;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alphacreators.noteguardian.NOTES.MainActivity;
import com.alphacreators.noteguardian.R;

import java.util.Calendar;


public class UserFragment extends Fragment {

    EditText userName,userDOB;
    Button started;

    DatePickerDialog .OnDateSetListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);


        enterDOB();

        userName = view.findViewById(R.id.userName);
        userDOB = view.findViewById(R.id.userDOB);

        started = view.findViewById(R.id.started);

        started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(userName.getText().toString().isEmpty() || userDOB.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }

            }
        });







        return view;
    }

    private void enterDOB() {

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        userDOB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,listener,year,month,day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date = day +"/"+ month+"/"+year;
                userDOB.setText(date);
            }
        };

        userDOB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String date = day+"/"+month+"/"+year;
                        userDOB.setText(date);
                    }
                },year,month, day);
                datePickerDialog.show();
            }
        });
    }
}