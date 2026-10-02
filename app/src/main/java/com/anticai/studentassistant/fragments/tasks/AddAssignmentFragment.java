package com.anticai.studentassistant.fragments.tasks;

import android.app.DatePickerDialog;
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

public class AddAssignmentFragment extends Fragment {

    private EditText etAssignmentTitle;
    private EditText etDescription;
    private Spinner spinnerSubject;
    private Spinner spinnerPriority;
    private TextView tvDueDate;
    private Button btnAddAssignment;

    private String selectedDueDate = "";

    public AddAssignmentFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_add_assignment,
                container,
                false
        );

        initializeViews(view);
        setupSpinners();
        setupDatePicker();
        setupAddAssignmentButton();

        return view;
    }

    private void initializeViews(View view) {

        etAssignmentTitle =
                view.findViewById(R.id.etAssignmentTitle);

        etDescription =
                view.findViewById(R.id.etDescription);

        spinnerSubject =
                view.findViewById(R.id.spinnerSubject);

        spinnerPriority =
                view.findViewById(R.id.spinnerPriority);

        tvDueDate =
                view.findViewById(R.id.tvDueDate);

        btnAddAssignment =
                view.findViewById(R.id.btnAddAssignment);
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

        String[] priorities = {
                "Select Priority",
                "Low",
                "Medium",
                "High"
        };

        ArrayAdapter<String> priorityAdapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        priorities
                );

        priorityAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPriority.setAdapter(priorityAdapter);
    }

    private void setupDatePicker() {

        tvDueDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            requireContext(),
                            (datePicker, selectedYear,
                             selectedMonth, selectedDay) -> {

                                selectedDueDate =
                                        selectedDay + " "
                                                + getMonthName(selectedMonth)
                                                + " "
                                                + selectedYear;

                                tvDueDate.setText(selectedDueDate);
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
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

    private void setupAddAssignmentButton() {

        btnAddAssignment.setOnClickListener(v -> {

            String title =
                    etAssignmentTitle
                            .getText()
                            .toString()
                            .trim();

            String description =
                    etDescription
                            .getText()
                            .toString()
                            .trim();

            String subject =
                    spinnerSubject
                            .getSelectedItem()
                            .toString();

            String priority =
                    spinnerPriority
                            .getSelectedItem()
                            .toString();

            if (title.isEmpty()) {

                etAssignmentTitle.setError(
                        "Enter assignment title"
                );

                etAssignmentTitle.requestFocus();
                return;
            }

            if (subject.equals("Select Subject")) {

                Toast.makeText(
                        requireContext(),
                        "Please select a subject",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (priority.equals("Select Priority")) {

                Toast.makeText(
                        requireContext(),
                        "Please select priority",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (selectedDueDate.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Please select due date",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Assignment added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }
}