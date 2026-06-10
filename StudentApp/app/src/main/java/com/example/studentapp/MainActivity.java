package com.example.studentapp;

import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String studentId, studentName, course;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        studentId = prefs.getString("studentId", "");
        studentName = prefs.getString("studentName", "");
        course = prefs.getString("course", "General");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("Welcome, " + studentName + "!");
        tvResult = findViewById(R.id.tvResult);

        String payload = studentId + "|" + studentName + "|" + course;
        CardService.setPayload(payload);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            tvResult.setText("NFC not supported");
        } else if (!nfcAdapter.isEnabled()) {
            tvResult.setText("Please enable NFC");
        } else {
            tvResult.setText("Ready to tap!");
        }
    }
}