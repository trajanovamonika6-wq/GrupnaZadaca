package com.example.teacherapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface AppDao {
    @Insert
    void insertAttendance(AttendanceRecord record);

    @Query("SELECT * FROM attendance ORDER BY timestamp DESC")
    List<AttendanceRecord> getAllAttendance();

    @Query("SELECT * FROM attendance WHERE synced = 0")
    List<AttendanceRecord> getUnsyncedAttendance();

    @Update
    void updateAttendance(AttendanceRecord record);

    @Insert
    void insertStudent(Student student);

    @Query("SELECT * FROM students WHERE studentId = :studentId LIMIT 1")
    Student getStudentById(String studentId);
}