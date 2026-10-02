package com.anticai.studentassistant.fragments.schedule;

import android.app.DatePickerDialog;
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

public class AddExamFragment extends Fragment {

    private EditText etExamTitle;
    private EditText etRoom;

    private Spinner spinnerSubject;
    private Spinner spinnerExamType;

    private TextView tvExamDate;
    private TextView tvExamTime;

    private Button btnAddExam;

    private String selectedDate = "";
    private String selectedTime = "";

    public AddExamFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_add_exam,
                container,
                false
        );

        initializeViews(view);
        setupSpinners();
        setupDatePicker();
        setupTimePicker();
        setupAddExamButton();

        return view;
    }

    private void initializeViews(View view) {

        etExamTitle = view.findViewById(R.id.etExamTitle);
        etRoom = view.findViewById(R.id.etRoom);

        spinnerSubject = view.findViewById(R.id.spinnerSubject);
        spinnerExamType = view.findViewById(R.id.spinnerExamType);

        tvExamDate = view.findViewById(R.id.tvExamDate);
        tvExamTime = view.findViewById(R.id.tvExamTime);

        btnAddExam = view.findViewById(R.id.btnAddExam);
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

        String[] examTypes = {
                "Select Exam Type",
                "Mid Semester",
                "End Semester",
                "Internal",
                "Practical",
                "Viva"
        };

        ArrayAdapter<String> examTypeAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        examTypes
                );

        examTypeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerExamType.setAdapter(examTypeAdapter);
    }

    private void setupDatePicker() {

        tvExamDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog =
                    new DatePickerDialog(
                            requireContext(),
                            (datePicker, selectedYear,
                             selectedMonth, selectedDay) -> {

                                selectedDate =
                                        selectedDay + " "
                                                + getMonthName(selectedMonth)
                                                + " "
                                                + selectedYear;

                                tvExamDate.setText(selectedDate);
                            },
                            year,
                            month,
                            day
                    );

            dialog.show();
        });
    }

    private void setupTimePicker() {

        tvExamTime.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog =
                    new TimePickerDialog(
                            requireContext(),
                            (timePicker, selectedHour,
                             selectedMinute) -> {

                                selectedTime =
                                        formatTime(
                                                selectedHour,
                                                selectedMinute
                                        );

                                tvExamTime.setText(selectedTime);
                            },
                            hour,
                            minute,
                            false
                    );

            dialog.show();
        });
    }

    private String getMonthName(int month) {

        String[] months = {
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        };

        return months[month];
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

    private void setupAddExamButton() {

        btnAddExam.setOnClickListener(v -> {

            String title =
                    etExamTitle.getText()
                            .toString()
                            .trim();

            String room =
                    etRoom.getText()
                            .toString()
                            .trim();

            String subject =
                    spinnerSubject.getSelectedItem()
                            .toString();

            String examType =
                    spinnerExamType.getSelectedItem()
                            .toString();

            if (title.isEmpty()) {

                etExamTitle.setError(
                        "Enter exam title"
                );

                etExamTitle.requestFocus();
                return;
            }

            if (subject.equals("Select Subject")) {

                showMessage("Please select a subject");
                return;
            }

            if (examType.equals("Select Exam Type")) {

                showMessage("Please select exam type");
                return;
            }

            if (selectedDate.isEmpty()) {

                showMessage("Please select exam date");
                return;
            }

            if (selectedTime.isEmpty()) {

                showMessage("Please select exam time");
                return;
            }

            if (room.isEmpty()) {

                etRoom.setError(
                        "Enter exam room"
                );

                etRoom.requestFocus();
                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Exam added successfully",
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