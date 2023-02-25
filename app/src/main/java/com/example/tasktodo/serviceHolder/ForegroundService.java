package com.example.tasktodo.serviceHolder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;


import com.example.tasktodo.BrodcastHolder.BroadcastReceiverListner;
import com.example.tasktodo.DBHolder.AppDB;
import com.example.tasktodo.R;
import com.example.tasktodo.Task;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.List;

public class ForegroundService extends Service {
    String CHANNEL_ID = "CHANNEL_SAMPLE";
    AppDB DataBase;
    Notification.Builder NotifyBuilder;
    NotificationManager notificationManager;
    ZonedDateTime time;
    LocalDate lt;
    Intent Brodcastintent = new Intent();
    @Override
    public void onCreate() {
        super.onCreate();
        CreateNotificationChannel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //send notefication to the user
        startForeground(1,sendNotification("Reminders"));
        //get the sp value of saveBox
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

       new Thread(new Runnable() {
           @Override
           public void run() {

               while (true) {
                   boolean save_removed_tasks = app_preferences.getBoolean("SaveBox",true);

                   /*********/
                   lt = LocalDate.now();
                   Calendar currentDateTime = Calendar.getInstance();

                   //time = ZonedDateTime.now();
                   ZoneId israelTimeZone = ZoneId.of("Asia/Jerusalem");

                   // Get the current time in the Israel timezone
                   ZonedDateTime israelDateTime = ZonedDateTime.now(israelTimeZone);
                   //time=israelTimeZone.;
                   Log.d("time now",israelDateTime.getYear() + ":" + israelDateTime.getMonth()+":"+israelDateTime.getDayOfMonth());
                   /*********/

                   DataBase = AppDB.getInstance(getApplicationContext());
                   //getting all task that their time is now !
                   List<Task> colsestTask = DataBase.taskDao().getClosetTask(lt.getDayOfMonth(),lt.getMonthValue(),lt.getYear(),"yet",israelDateTime.getHour()-2,israelDateTime.getMinute());
                   // the service always check if there is any task that their date is already pass
                    List<Task> AllTask = DataBase.taskDao().getAll();
                   for(Task temp : AllTask) {
                       LocalDate myDate2 = LocalDate.of(temp.getYear(), temp.getMonth(), temp.getDay());
                       int compareValue = lt.compareTo(myDate2);
                       if(temp.getStatus().equals("yet") && getTaskDueDateStatus(temp)==-1) {
                           temp.setStatus();

                           DataBase.taskDao().insert(temp);
                           Brodcastintent.setAction("com.TaskRemainder.CUSTOM_INTENT");
                           sendBroadcast(Brodcastintent);
                       }
                       else if(temp.getStatus().equals("yet") && getTaskDueDateStatus(temp)==0) {
                           int x=currentDateTime.get(Calendar.HOUR);
                           int am_pm = currentDateTime.get(Calendar.AM_PM);
                           if (am_pm == Calendar.AM) {
                               // Current time is in the morning (AM)

                           } else if (am_pm == Calendar.PM) {
                               // Current time is in the afternoon/evening (PM)
                               x=currentDateTime.get(Calendar.HOUR)+12;
                               Log.d("Cutr",x + ":" + temp.getHour());

                           }

                           if (temp.getHour() == x && temp.getMinute() <= currentDateTime.get(Calendar.MINUTE)) {
                               temp.setStatus();

                                       DataBase.taskDao().insert(temp);
                               Brodcastintent.setAction("com.TaskRemainder.CUSTOM_INTENT");
                               sendBroadcast(Brodcastintent);

                           }
                           else  if (temp.getHour() < x ) {
                               temp.setStatus();
                               DataBase.taskDao().insert(temp);
                               Brodcastintent.setAction("com.TaskRemainder.CUSTOM_INTENT");
                               sendBroadcast(Brodcastintent);

                           }
                       }
                   }
                   for(Task temp : AllTask) {
                       if(getTaskDueDateStatus(temp)==0) {
                           //Log.d("AirplaneMode", String.valueOf(BroadcastReceiverListner.SendEnable));

                           if(isTaskDueSoon(temp)){
                               if((currentDateTime.get(Calendar.SECOND)%60==3)&& (BroadcastReceiverListner.SendEnable==true))

                               sendNotification(temp.getTitle());
                           }

                       }
                   }
               }

           }
       }).start();

        return super.onStartCommand(intent, flags, startId);
    }
    public static int getTaskDueDateStatus(Task task) {
        // Create a Calendar object for the current date and time
        // Get the current date/time
        Calendar currentDateTime = Calendar.getInstance();
        Log.d("Current date/time: ", currentDateTime.get(Calendar.YEAR) + "-" + (currentDateTime.get(Calendar.MONTH) + 1) + "-" + currentDateTime.get(Calendar.DAY_OF_MONTH) + " " + currentDateTime.get(Calendar.HOUR_OF_DAY) + ":" + currentDateTime.get(Calendar.MINUTE));

// Get the date/time of the task
        Calendar taskDateTime = Calendar.getInstance();
        taskDateTime.set(task.getYear(), task.getMonth(), task.getDay(), task.getHour(), task.getMinute());
        Log.d("Task date/time: ", taskDateTime.get(Calendar.YEAR) + "-" + (taskDateTime.get(Calendar.MONTH) + 1) + "-" + taskDateTime.get(Calendar.DAY_OF_MONTH) + " " + task.getHour() + ":" + task.getMinute());
        taskDateTime.set(Calendar.HOUR_OF_DAY, 0);
        taskDateTime.set(Calendar.MINUTE, 0);
        taskDateTime.set(Calendar.SECOND, 0);
        taskDateTime.set(Calendar.MILLISECOND, 0);

        currentDateTime.set(Calendar.HOUR_OF_DAY, 0);
        currentDateTime.set(Calendar.MINUTE, 0);
        currentDateTime.set(Calendar.SECOND, 0);
        currentDateTime.set(Calendar.MILLISECOND, 0);
        // Compare the two Calendar objects
        if (taskDateTime.before(currentDateTime)) {
            // Task is past due
            Log.d("compare ","-1");
            return -1;
        } else if (taskDateTime.get(Calendar.YEAR) == currentDateTime.get(Calendar.YEAR) &&
                taskDateTime.get(Calendar.DAY_OF_YEAR) == currentDateTime.get(Calendar.DAY_OF_YEAR)&&taskDateTime.get(Calendar.MONTH) == currentDateTime.get(Calendar.MONTH)) {
            // Task is due today
            Log.d("compare ","0");
            return 0;

        } else {
            // Task is due in the future
            Log.d("compare ","1");
            return 1;
        }
    }
    public static boolean isTaskDueSoon(Task task) {
        // Get the current time
        Calendar currentDateTime = Calendar.getInstance();
        int currentHour = currentDateTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentDateTime.get(Calendar.MINUTE);
        Log.d("time nowss1",currentHour + ":" + currentMinute);
        Log.d("time nowss2",task.getHour() + ":" + task.getMinute());
        // Get the task time
        int taskHour = task.getHour();
        int taskMinute = task.getMinute();

        // Calculate the time difference in minutes
        int timeDifference = (taskHour - currentHour) * 60 + (taskMinute - currentMinute);
        // Check if the time difference is less than or equal to 30 minutes
        if (timeDifference== 30) {
            // Task is due soon
            return true;
        } else {
            // Task is not due soon
            return false;
        }
    }

    public void CreateNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

    }

    public Notification sendNotification(String title) {

        NotifyBuilder = new Notification.Builder(this,CHANNEL_ID)
                .setAutoCancel(true)
                .setContentText("TaskToDoApp")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Don't Forget " + title);

        Notification notification = NotifyBuilder.build();
        notificationManager.notify(0, notification);
        return notification;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
