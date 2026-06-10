package com.example.teacherapp;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("attendance.php")
    Call<GenericResponse> syncAttendance(@Header("Authorization") String token, @Body List<AttendanceRecord> records);

    @GET("attendance.php")
    Call<List<AttendanceRecord>> getAttendance(@Header("Authorization") String token);
}