package com.example.finalproject;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity {

    TextView tvSelectedTime;
    Button btnPickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnPickTime = findViewById(R.id.btnPickTime);

        btnPickTime.setOnClickListener(v -> {

            // Get Current Time
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Open Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    TimePickerActivity.this,
                    (view, selectedHour, selectedMinute) -> {

                        String formattedTime = selectedHour + ":" + selectedMinute;
                        tvSelectedTime.setText("Selected Time: " + formattedTime);
                    },
                    hour,
                    minute,
                    false   // false = 12 hour format, true = 24 hour format
            );

            timePickerDialog.show();
        });
    }
}
