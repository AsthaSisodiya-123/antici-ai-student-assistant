package com.anticai.studentassistant.fragments.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

import java.util.Calendar;

public class AddClassFragment extends Fragment {

    private EditText etTeacher;
    private EditText etRoom;

    private Spinner spinnerSubject;
    private Spinner spinnerDay;
    private Spinner spinnerClassType;

    private TextView tvStartTime;
    private TextView tvEndTime;

    private Button btnAddClass;

    private String selectedStartTime = "";
    private String selectedEndTime = "";

    public AddClassFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_add_class,
                container,
                false
        );

        initializeViews(view);
        setupSpinners();
        setupTimePickers();
        setupAddClassButton();

        return view;
    }

    private void initializeViews(View view) {

        etTeacher = view.findViewById(R.id.etTeacher);
        etRoom = view.findViewById(R.id.etRoom);

        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        spinnerDay = view.findViewById(R.id.spinnerDay);
        spinnerClassType = view.findViewById(R.id.spinnerClassType);

        tvStartTime = view.findViewById(R.id.tvStartTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);

        btnAddClass = view.findViewById(R.id.btnAddClass);
    }

    private void setupSpinners() {

        String[] subjects = {
                "Select Subject",
                "DBMS",
                "DSA",
                "Java",
                "AI & ML",
                "Computer Networks",
                "Operating Systems"
        };

        ArrayAdapter<String> subjectAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        subjects
                );

        subjectAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSubject.setAdapter(subjectAdapter);

        String[] days = {
                "Select Day",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        };

        ArrayAdapter<String> dayAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        days
                );

        dayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerDay.setAdapter(dayAdapter);

        String[] classTypes = {
                "Select Class Type",
                "Lecture",
                "Practical",
                "Tutorial"
        };

        ArrayAdapter<String> typeAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        classTypes
                );

        typeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerClassType.setAdapter(typeAdapter);
    }

    private void setupTimePickers() {

        tvStartTime.setOnClickListener(v ->
                showTimePicker(true)
        );

        tvEndTime.setOnClickListener(v ->
                showTimePicker(false)
        );
    }

    private void showTimePicker(boolean isStartTime) {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog =
                new TimePickerDialog(
                        requireContext(),
                        (timePicker, selectedHour, selectedMinute) -> {

                            String time = formatTime(
                                    selectedHour,
                                    selectedMinute
                            );

                            if (isStartTime) {

                                selectedStartTime = time;
                                tvStartTime.setText(time);

                            } else {

                                selectedEndTime = time;
                                tvEndTime.setText(time);
                            }
                        },
                        hour,
                        minute,
                        false
                );

        dialog.show();
    }

    private String formatTime(int hour, int minute) {

        String period = hour >= 12 ? "PM" : "AM";

        int displayHour = hour % 12;

        if (displayHour == 0) {
            displayHour = 12;
        }

        return String.format(
                "%02d:%02d %s",
                displayHour,
                minute,
                period
        );
    }

    private void setupAddClassButton() {

        btnAddClass.setOnClickListener(v -> {

            String teacher =
                    etTeacher.getText()
                            .toString()
                            .trim();

            String room =
                    etRoom.getText()
                            .toString()
                            .trim();

            String subject =
                    spinnerSubject.getSelectedItem()
                            .toString();

            String day =
                    spinnerDay.getSelectedItem()
                            .toString();

            String classType =
                    spinnerClassType.getSelectedItem()
                            .toString();

            if (subject.equals("Select Subject")) {

                showMessage("Please select a subject");
                return;
            }

            if (day.equals("Select Day")) {

                showMessage("Please select a day");
                return;
            }

            if (classType.equals("Select Class Type")) {

                showMessage("Please select class type");
                return;
            }

            if (selectedStartTime.isEmpty()) {

                showMessage("Please select start time");
                return;
            }

            if (selectedEndTime.isEmpty()) {

                showMessage("Please select end time");
                return;
            }

            if (teacher.isEmpty()) {

                etTeacher.setError("Enter teacher name");
                etTeacher.requestFocus();
                return;
            }

            if (room.isEmpty()) {

                etRoom.setError("Enter classroom or room");
                etRoom.requestFocus();
                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Class added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }

    private void showMessage(String message) {

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}