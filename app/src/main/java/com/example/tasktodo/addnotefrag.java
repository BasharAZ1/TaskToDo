package com.example.tasktodo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.tasktodo.DBHolder.AppDB;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class addnotefrag extends Fragment {
    EditText AddtitleNote,AddDescreptionNote;
    Button addNoteButton,date, time,cancel;
    Context conn;
    AppDB DataBase;
    EditText txtDate, txtTime;
    SharedPreferences prefs;
    public static addnotefrag addnotef;
    public static DatePickerDialog.OnDateSetListener onDateSetListener;
    TimePickerDialog.OnTimeSetListener onTimeSetListener;
    boolean timeselected , dateselected;
    public static int finalyear , finalmonth , finalday, finalhour, finalminute;

    public addnotefrag(Context con) {
        conn = con;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addnotef = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addnotefrag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AddtitleNote = view.findViewById(R.id.AddtitleNote);
        AddDescreptionNote = view.findViewById(R.id.AddDescreptionNote);
        addNoteButton = view.findViewById(R.id.addNoteButton);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        txtDate=view.findViewById(R.id.in_date);
        txtTime=view.findViewById(R.id.in_time);
        date.setOnClickListener(new DateButtonClass());

        ZonedDateTime tt = ZonedDateTime.now();
        ZonedDateTime IsraelDateTime = tt.withZoneSameInstant(ZoneId.of("Asia/Jerusalem"));
        int hour = IsraelDateTime.getHour();
        int minute = IsraelDateTime.getMinute();

        timeselected = false;
        dateselected = false;

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                finalyear=i;
                finalmonth=i1;
                finalday =i2;
                dateselected = true;
                txtDate.setText(finalday +"/"+(finalmonth+1)+"/"+finalyear );
            }
        };

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),android.R.style.Theme_Holo_Dialog_MinWidth,
                        onTimeSetListener
                        ,hour,minute,true);

                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.show();


            }
        });

        onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                finalhour = i;
                finalminute = i1;
                timeselected = true;
                if(finalminute<10){
                    txtTime.setText(finalhour + ":0" + finalminute);
                }
               else txtTime.setText(finalhour + ":" + finalminute);
            }
        };
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title,desc;
                title = AddtitleNote.getText().toString();
                desc = AddDescreptionNote.getText().toString();
                String id=generateUniqueId();
                if(!title.equals("") && !desc.equals("") && timeselected && dateselected){
                    Task newTask = new Task(title, desc, finalday, finalmonth, finalyear, "yet", finalhour, finalminute,id);
                    addTask(newTask);
                } else {
                    Toast.makeText(getContext(), "All data is required", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    public static String generateUniqueId() {
        return Long.toString(System.currentTimeMillis());
    }
    private void addTask(Task newTask) {
        DataBase = AppDB.getInstance(getContext());
        AppDB abc = AppDB.getInstance(this.getContext());
        DataBase.taskDao().insert(newTask);
        List<Task> all = abc.taskDao().getAll();
       ItemsRecycleAdapter.TaskList=new ArrayList<>();
        for (Task task : all) {
            ItemsRecycleAdapter.TaskList.add(task);
        }
        ItemsRecycleAdapter.listner.notifyDataSetChanged();
        sortTaskList();
        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
        getActivity().onBackPressed();
        ItemsRecycleAdapter.listner.notifyDataSetChanged();
    }


    public static void sortTaskList() {
        boolean remember = ItemsRecycleAdapter.prefManager.getBoolean("SaveBox", false);
        if (remember) {
            Collections.sort(ItemsRecycleAdapter.listner.TaskList, Task.sortBy("dueDate"));
            Log.d("SORTING", "Rember true");
        } else {
            Collections.sort(ItemsRecycleAdapter.TaskList, Task.sortBy("title"));
            Log.d("SORTING", "Rember false");
        }
        ItemsRecycleAdapter.listner.notifyItemChanged(1);
    }




    /****************************************** inner class listner to date button ****************************************************/
    class DateButtonClass implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            LocalDate lt = LocalDate.now();
            int year = lt.getYear();
            int month = lt.getMonthValue()-1;
            int day = lt.getDayOfMonth();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Dialog_MinWidth
                    ,onDateSetListener,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
            txtDate.setText(day + "-" + (month + 1) + "-" + year);
        }
    }
}