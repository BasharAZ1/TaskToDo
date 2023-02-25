package com.example.tasktodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.tasktodo.DBHolder.AppDB;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class EditNotefrag extends Fragment {

    Task selectedTask;
    EditText Edittitle,EditDesc;
    Button updatebutton ,date, time;;
    EditText txtDate, txtTime;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;
    public static int finalyear , finalmonth , finalday, finalhour, finalminute;
    AppDB DataBase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_notefrag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Edittitle = view.findViewById(R.id.Edittitle1);
        EditDesc = view.findViewById(R.id.EditDesc1);
        updatebutton = view.findViewById(R.id.updatebutton);
        date = view.findViewById(R.id.dateEdit);
        txtDate=view.findViewById(R.id.in_date);
        txtTime=view.findViewById(R.id.in_time);
        time = view.findViewById(R.id.timeEdit);

        Edit_widget();

        LocalDate lt = LocalDate.now();
        int year = lt.getYear();
        int month = lt.getMonthValue()-1;
        int day = lt.getDayOfMonth();

        ZonedDateTime tt = ZonedDateTime.now();
        ZonedDateTime IsraelDateTime = tt.withZoneSameInstant(ZoneId.of("Asia/Jerusalem"));
        int hour = IsraelDateTime.getHour();
        int minute = IsraelDateTime.getMinute();


        finalyear = selectedTask.year;
        finalmonth = selectedTask.month;
        finalday = selectedTask.day;
        finalhour = selectedTask.hour;
        finalminute = selectedTask.minute;
        txtDate.setText(finalday +"/"+finalmonth+"/"+finalyear );
        txtTime.setText(finalhour + ":" + finalminute);



        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Dialog_MinWidth
                        ,onDateSetListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                finalyear=i;
                finalmonth=i1+1;
                finalday =i2;
                txtDate.setText(finalday +"/"+finalmonth+"/"+finalyear );

            }
        };

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog datePickerDialog = new TimePickerDialog(getContext(),android.R.style.Theme_Holo_Dialog_MinWidth,
                        onTimeSetListener
                        ,hour,minute,true);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                finalhour = i;
                finalminute = i1;
                txtTime.setText(finalhour + ":" + finalminute);
            }
        };


        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title,desc,id;
                title = Edittitle.getText().toString();
                desc = EditDesc.getText().toString();
                id=EditDesc.getText().toString();

                //remove the old task from the sql
                DataBase = AppDB.getInstance(getContext());
                DataBase.taskDao().delete(selectedTask);
                ItemsRecycleAdapter.listner.removeTask(selectedTask);

                //Adding to SQL
                Task newTask = new Task(title, desc, finalday, finalmonth, finalyear, "yet", finalhour, finalminute,id);
                DataBase.taskDao().insert(newTask);
                ItemsRecycleAdapter.listner.TaskList.add(newTask);
                ItemsRecycleAdapter.listner.notifyDataSetChanged();
                MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
                getActivity().onBackPressed();

            }
        });
    }


    public void EditSelectedTask(Task task){
        selectedTask = task;
    }

    public void Edit_widget(){

        Log.d("selectedddd",selectedTask.getTitle()+ " " + selectedTask.getDescription()+ " " + selectedTask.getDay()+ " " + selectedTask.getMonth()+ " " + selectedTask.getYear());
        Edittitle.setText(selectedTask.getTitle());
        EditDesc.setText(selectedTask.getDescription());

    }


}