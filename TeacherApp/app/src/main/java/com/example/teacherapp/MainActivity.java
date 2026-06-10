package com.example.teacherapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private AttendanceAdapter adapter;
    private AppDatabase db;
    private String token;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Teacher App");
        }

        db = AppDatabase.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        token = "Bearer " + prefs.getString("token", "");

        tvStatus = findViewById(R.id.tvStatus);
        ListView listView = findViewById(R.id.listView);
        Button btnSync = findViewById(R.id.btnSync);

        List<AttendanceRecord> records = db.appDao().getAllAttendance();
        adapter = new AttendanceAdapter(this, records);
        listView.setAdapter(adapter);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            tvStatus.setText("NFC: Not supported on this device");
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    PendingIntent.FLAG_MUTABLE);
            tvStatus.setText("NFC Session: ACTIVE");
        }

        btnSync.setOnClickListener(v -> syncData());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            IntentFilter[] filters = new IntentFilter[]{
                    new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            };
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            processNfcIntent(intent);
        }
    }

    private void processNfcIntent(Intent intent) {
        try {
            android.nfc.NdefMessage[] messages = null;
            android.os.Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
            }

            if (messages != null && messages.length > 0) {
                NdefRecord record = messages[0].getRecords()[0];
                String payload = new String(record.getPayload());
                // payload format: "studentId|studentName|course"
                String[] parts = payload.split("\\|");
                if (parts.length >= 2) {
                    String studentId = parts[0];
                    String studentName = parts[1];
                    String course = parts.length > 2 ? parts[2] : "General";
                    registerAttendance(studentId, studentName, course);
                }
            } else {
                // Tag detected but no NDEF message - register with tag ID
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag != null) {
                    byte[] tagId = tag.getId();
                    StringBuilder sb = new StringBuilder();
                    for (byte b : tagId) {
                        sb.append(String.format("%02X", b));
                    }
                    registerAttendance(sb.toString(), "Unknown Student", "General");
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "NFC Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerAttendance(String studentId, String studentName, String course) {
        AttendanceRecord record = new AttendanceRecord();
        record.studentId = studentId;
        record.studentName = studentName;
        record.className = course;
        record.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        record.synced = false;

        db.appDao().insertAttendance(record);
        adapter.updateData(db.appDao().getAllAttendance());

        Toast.makeText(this, "✓ " + studentName + " registered!", Toast.LENGTH_SHORT).show();
        tvStatus.setText("Last tap: " + studentName + " at " + record.timestamp);
    }

    private void syncData() {
        List<AttendanceRecord> unsynced = db.appDao().getUnsyncedAttendance();
        if (unsynced.isEmpty()) {
            Toast.makeText(this, "Nothing to sync", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getService().syncAttendance(token, unsynced).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    for (AttendanceRecord r : unsynced) {
                        r.synced = true;
                        db.appDao().updateAttendance(r);
                    }
                    Toast.makeText(MainActivity.this, "Synced " + unsynced.size() + " records!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Sync failed - will retry later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}