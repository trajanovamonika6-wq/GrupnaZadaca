package com.example.teacherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class AttendanceAdapter extends BaseAdapter {
    private Context context;
    private List<AttendanceRecord> records;

    public AttendanceAdapter(Context context, List<AttendanceRecord> records) {
        this.context = context;
        this.records = records;
    }

    public void updateData(List<AttendanceRecord> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return records.size(); }

    @Override
    public Object getItem(int position) { return records.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_attendance, parent, false);
        }
        AttendanceRecord r = records.get(position);
        ((TextView) convertView.findViewById(R.id.tvStudentName)).setText(r.studentName);
        ((TextView) convertView.findViewById(R.id.tvStudentId)).setText(r.studentId);
        ((TextView) convertView.findViewById(R.id.tvTimestamp)).setText(r.timestamp);
        return convertView;
    }
}