package com.example.tasktodo.DBHolder;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.tasktodo.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    List<Task> getAll();

    @Delete
    void delete(Task task);


    @Insert(onConflict = REPLACE)
    public void insert(Task task);
    @Query("SELECT * FROM Task ORDER BY hour ASC")
    List<Task> getTasksSortedByDueDate();
    @Query("SELECT * FROM Task where year=:year1 And day =:day1 And month=:month1 And status=:status1 And hour=:hour1 And minute=:minute1")
    List<Task> getClosetTask(int day1, int month1 , int year1,String status1,int hour1, int minute1);
    @Query("SELECT * FROM Task WHERE id = :id LIMIT 1")
    Task getTaskById(String id);




}
