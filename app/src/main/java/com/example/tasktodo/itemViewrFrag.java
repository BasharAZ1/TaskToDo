package com.example.tasktodo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class itemViewrFrag extends Fragment {
    Task selectedTask;
    TextView titleviewr,desViewer,dateviewer,Timeviewer;

    public void EditSelectedTask(Task task){
        selectedTask = task;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_viewr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleviewr = view.findViewById(R.id.titleviewr);
        desViewer = view.findViewById(R.id.desViewer);
        dateviewer = view.findViewById(R.id.dateviewer);
        Timeviewer = view.findViewById(R.id.Timeviewer);
        Edit_widget();

    }

    public void Edit_widget(){
        Log.d("selectedddd",selectedTask.getTitle()+ " " + selectedTask.getDescription()+ " " + selectedTask.getDay()+ " " + selectedTask.getMonth()+ " " + selectedTask.getYear());
        titleviewr.setText(selectedTask.getTitle());
        desViewer.setText(selectedTask.getDescription());
        dateviewer.setText(selectedTask.getDay()+"/"+ selectedTask.getMonth()+"/"+selectedTask.getYear());
        Timeviewer.setText(selectedTask.getHour()+":"+selectedTask.getMinute());

    }
}