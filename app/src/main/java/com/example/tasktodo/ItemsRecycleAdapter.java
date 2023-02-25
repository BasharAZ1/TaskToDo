package com.example.tasktodo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tasktodo.DBHolder.AppDB;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;

public class ItemsRecycleAdapter extends RecyclerView.Adapter<ItemsRecycleAdapter.MyViewHolder> implements SharedPreferences.OnSharedPreferenceChangeListener {
  static   public ArrayList<Task> TaskList;
    TodoListFrag.TaskListFragListener fragListner1;
    Context con;
    AppDB database;
    AlertDialog dialog;
   static  SharedPreferences prefManager;
    SharedPreferences.Editor editor;
    static boolean flag=false;
    static int sorted=0;


    public static ItemsRecycleAdapter listner;

    public ItemsRecycleAdapter(ArrayList<Task> taskList, Context conn,SharedPreferences sp) {
        this.TaskList = taskList;
        boolean remember = sp.getBoolean("SaveBox", false);

        Log.d("task list", String.valueOf(TaskList.size()));
        fragListner1 = TodoListFrag.fragListner;
        con = conn;
        database = AppDB.getInstance(con);
        listner = this;
        this.prefManager = sp;
        sp.registerOnSharedPreferenceChangeListener(this);
        if (remember) {
            Collections.sort(taskList, Task.sortBy("dueDate"));
        } else {
            Collections.sort(taskList, Task.sortBy("title"));
        }// Register the adapter as a listener
        notifyDataSetChanged();


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        MyViewHolder evh = new MyViewHolder(view, null);
        return evh;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Task task = TaskList.get(position);
        holder.titleitem.setText(task.getTitle());
        holder.describeItem.setText(task.getDescription());
        holder.dateItem.setText(task.getDay()+"/"+(task.getMonth()+1)+"/"+task.getYear());
        boolean remember = prefManager.getBoolean("SaveBox", false);
        if (remember) {
            Collections.sort(TaskList, Task.sortBy("dueDate"));
        } else {
            Collections.sort(TaskList, Task.sortBy("title"));
        }



        if(task.getMinute()<10)
            holder.itemTime.setText(task.getHour()+":0"+task.getMinute());
        else
            holder.itemTime.setText(task.getHour()+":"+task.getMinute());

        if(task.getStatus().equals("Done/Passed")){
            ShapeDrawable drawable = new ShapeDrawable();
            drawable.setShape(new RectShape());
            drawable.getPaint().setColor(Color.GREEN);
            holder.itemView.setBackground(drawable);
            holder.itemstatus.setTextColor(Color.YELLOW);
            holder.itemstatus.setText("Status: " + task.getStatus());


        }
        else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.itemstatus.setText("Status: " + task.getStatus());
            holder.itemstatus.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return TaskList.size();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("SaveBox")) {
            boolean remember = sharedPreferences.getBoolean("SaveBox", false);
            if (remember) {
                Collections.sort(TaskList, Task.sortBy("dueDate"));

            } else {
                Collections.sort(TaskList, Task.sortBy("title"));

            }
            notifyDataSetChanged(); // Update the adapter after changing the sort order
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleitem, describeItem, dateItem,itemTime,itemstatus;
        Button itemEditButton;
        Button Donebutton;


        public MyViewHolder(@NonNull View itemView, Object o) {
            super(itemView);
            titleitem = itemView.findViewById(R.id.titleitem);
            describeItem = itemView.findViewById(R.id.describeItem);
            dateItem = itemView.findViewById(R.id.dateItem);
            itemEditButton = itemView.findViewById(R.id.itemEditButton);
            Donebutton=itemView.findViewById(R.id.doneButton);
            itemTime = itemView.findViewById(R.id.itemTime);
            itemstatus = itemView.findViewById(R.id.itemstatus);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
            SharedPreferences.Editor editor = sharedPref.edit();

            itemEditButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    fragListner1.DoInEditEvent(TaskList.get(pos));
                }
            });
            Donebutton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    fragListner1.DoInDoneEvent(TaskList.get(pos),con);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();

                    dialog = new AlertDialog.Builder(con)
                            .setTitle("Delete Note")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (pos != RecyclerView.NO_POSITION) {
                                        editor.putString("deleted_task_" + TaskList.get(pos).getId(), TaskList.get(pos).getTitle());
                                        editor.apply();
                                        database.taskDao().delete(TaskList.get(pos));
                                        TaskList.remove(pos);

                                    }
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.cancel();
                                }
                            })
                            .show();


                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    fragListner1.DoInViewEvent(TaskList.get(pos));
                }
            });


        }

    }

   public void UpdateTaskList() {
        AppDB abc = AppDB.getInstance(con);
        List<Task> all = abc.taskDao().getAll();
        //Collections.sort(all, Task.sortBy("dueDate"));
       addnotefrag.sortTaskList();
        ItemsRecycleAdapter.listner.TaskList = new ArrayList<>();
        for (Task temp : all) {
            ItemsRecycleAdapter.listner.TaskList.add(temp);
        }
       boolean remember = prefManager.getBoolean("SaveBox", false);
       if (remember) {
           Collections.sort(TaskList, Task.sortBy("dueDate"));
       } else {
           Collections.sort(TaskList, Task.sortBy("title"));
       }
       notifyDataSetChanged();
    }



    public  void removeTask(Task task){
        for( int i=0 ; i<TaskList.size() ; i++)
            if(TaskList.get(i).equals(task))
                TaskList.remove(i);
        UpdateTaskList();
        notifyDataSetChanged();
    }


}
