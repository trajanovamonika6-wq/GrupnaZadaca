package com.example.teacherapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "students")
public class Student {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String studentId;
    public String studentName;
    public String course;
}