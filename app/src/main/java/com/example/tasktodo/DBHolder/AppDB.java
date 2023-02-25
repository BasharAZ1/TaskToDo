package com.example.tasktodo.DBHolder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.tasktodo.Task;

import java.util.Calendar;
import java.util.Comparator;


@Database(entities = {Task.class},version = 2)
public  abstract class AppDB extends RoomDatabase {
    private static AppDB instance;

    public static AppDB getInstance(Context context) {
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDB.class,"tasks-database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract TaskDao taskDao();

        };



