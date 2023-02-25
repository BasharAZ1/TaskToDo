package com.example.tasktodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasktodo.DBHolder.AppDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class TodoListFrag extends Fragment {

    RecyclerView recycleView;
    public ArrayList<Task> taskList;
    private RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fbutton;

    public static TaskListFragListener fragListner;

    public TodoListFrag() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycleView = view.findViewById(R.id.recycleView);
        recycleView.setHasFixedSize(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        layoutManager = new LinearLayoutManager(getContext());
        taskList = new ArrayList<>();
        AppDB abc = AppDB.getInstance(this.getContext());
        List<Task> all = abc.taskDao().getAll();
        List<Task> deletedTasks = new ArrayList<>();

        taskList.addAll(all);
        ItemsRecycleAdapter madapter = new ItemsRecycleAdapter(taskList, getContext(),sharedPreferences);
        recycleView.setLayoutManager(layoutManager);

        recycleView.setAdapter(madapter);
        fbutton = view.findViewById(R.id.floating_action_button);
        fbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {

                    addnotefrag fragB = new addnotefrag(getContext());
                    MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                            add(R.id.fragContainer, fragB).//add on top of the static fragment
                            addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                            commit();
                    MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();

                }

            }
        });



    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }


   /**************************************** interface for talking with other fragments ****************************************/
    public interface TaskListFragListener{
        public void DoInEditEvent(Task task);


       void DoInDoneEvent(Task task, Context con);

       public void DoInViewEvent(Task task);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try{
            this.fragListner = (TaskListFragListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException("the class " + context.getClass().getName() + " must implements the interface 'FragAListener'");
        }
        super.onAttach(context);
    }
}