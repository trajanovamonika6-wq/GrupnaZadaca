package com.example.teacherapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "attendance")
public class AttendanceRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String studentId;
    public String studentName;
    public String timestamp;
    public String className;
    public boolean synced;
}