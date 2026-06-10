package com.example.studentapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etStudentId = findViewById(R.id.etStudentId);
        EditText etStudentName = findViewById(R.id.etStudentName);
        EditText etCourse = findViewById(R.id.etCourse);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String studentId = etStudentId.getText().toString();
            String studentName = etStudentName.getText().toString();
            String course = etCourse.getText().toString();

            if (studentId.isEmpty() || studentName.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            prefs.edit()
                    .putString("studentId", studentId)
                    .putString("studentName", studentName)
                    .putString("course", course)
                    .apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}