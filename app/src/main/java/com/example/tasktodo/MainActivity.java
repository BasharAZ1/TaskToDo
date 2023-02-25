package com.example.tasktodo;

import static java.lang.System.exit;
import static java.security.AccessController.getContext;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tasktodo.BrodcastHolder.BroadcastReceiverListner;
import com.example.tasktodo.DBHolder.AppDB;
import com.example.tasktodo.serviceHolder.ForegroundService;


public class MainActivity extends AppCompatActivity implements TodoListFrag.TaskListFragListener{
    private static MainActivity main;
    BroadcastReceiver br = new BroadcastReceiverListner();
    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main= (MainActivity) this;
    /************************************************ define service **********************************************************/

        if(!checkServiceRunning(ForegroundService.class)){

            Intent serviceIntent = new Intent(this, ForegroundService.class);
            startForegroundService(serviceIntent);
        }


    }

    private boolean checkServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

/************************************************ define menu **********************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settingBotton:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content ,new MyPreferences()).addToBackStack(null)
                        .commit();
                break;

            case R.id.exitBotton:
                exit(0);


        }
        return super.onOptionsItemSelected(item);
    }


    public static MainActivity getactiv() {
        return main;
    }

    public void Cancelled(View view) {
        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
        main.onBackPressed();
    }


    /************************************************ define Preferences  **********************************************************/

    public static class MyPreferences extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.mypreferencescreen, rootKey);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            view.setBackgroundColor(Color.WHITE);
            super.onViewCreated(view, savedInstanceState);
        }
    }

    /************************************************ talking with frag **********************************************************/

    @Override
    public void DoInEditEvent(Task task) {
        EditNotefrag editNotefrag = new EditNotefrag();
        editNotefrag.EditSelectedTask(task);
        MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                add(R.id.fragContainer, editNotefrag).//add on top of the static fragment
                addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                commit();
        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();

    }


    @Override
    public void DoInDoneEvent(Task task, Context con) {

        AppDB DataBase;
        DataBase = AppDB.getInstance(con);
        DataBase.taskDao().delete(task);
        ItemsRecycleAdapter.listner.removeTask(task);
        task.setStatus();
        Task temp=task;
        DataBase.taskDao().insert(temp);
        ItemsRecycleAdapter.TaskList.add(temp);
        ItemsRecycleAdapter.listner.notifyDataSetChanged();


    }
   @Override
    public void DoInViewEvent(Task task) {
        itemViewrFrag viewerFrag = new itemViewrFrag();
        viewerFrag.EditSelectedTask(task);
        MainActivity.getactiv().getSupportFragmentManager().beginTransaction().
                add(R.id.fragContainer, viewerFrag).//add on top of the static fragment
                addToBackStack("BBB").//cause the back button scrolling through the loaded fragments
                commit();
        MainActivity.getactiv().getSupportFragmentManager().executePendingTransactions();
    }


    @Override
    protected void onStart() {
        super.onStart();
        filter.addAction("com.TaskRemainder.CUSTOM_INTENT");
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(br, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(br);
    }



}