package com.example.tasktodo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Calendar;
import java.util.Comparator;

@Entity(primaryKeys = {"day","month","year","hour","minute"})
public class Task {

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "description")
    String description;
    
    @ColumnInfo(name = "year")
    int  year;

    @ColumnInfo(name = "month")
    int  month;

    @ColumnInfo(name = "day")
    int  day;

    @ColumnInfo(name = "status")
    String status;

    @ColumnInfo(name = "hour")
    int hour;

    @ColumnInfo(name = "minute")
    int minute;
    @ColumnInfo(name = "id")
    String id;

    public Task(String title, String description, int day, int month, int year, String status, int hour, int minute,String id) {
        this.title = title;
        this.description = description;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.status = status;
        this.id=id;
    }
    public static Comparator<Task> sortBy(final String sortType) {
        return new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                if (sortType.equals("title")) {
                    return t1.getTitle().compareTo(t2.getTitle());
                } else if (sortType.equals("description")) {
                    return t1.getDescription().compareTo(t2.getDescription());
                } else if (sortType.equals("dueDate")) {
                    // Compare the due dates
                    Calendar c1 = Calendar.getInstance();
                    c1.set(t1.getYear(), t1.getMonth(), t1.getDay());
                    Calendar c2 = Calendar.getInstance();
                    c2.set(t2.getYear(), t2.getMonth(), t2.getDay());
                    return c1.compareTo(c2);
                } else {
                    return 0;
                }
            }
        };
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus() {
        this.status = "Done/Passed";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    // Other fields for Task class

    // Constructor for Task class
    public void setId(String id) {this.id = id;}

    // Getter method for id field
    public String getId() {
        return id;
    }



    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", status='" + status + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                ", id=" + id +
                '}';
    }
}
