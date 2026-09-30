package com.anticai.studentassistant.fragments.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class AddSubjectFragment extends Fragment {

    private EditText etSubjectName;
    private EditText etSubjectCode;
    private EditText etTeacherName;
    private Spinner spinnerSemester;

    private Button btnAddSubject;

    public AddSubjectFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_add_subject,
                container,
                false
        );

        initializeViews(view);
        setupSemesterSpinner();
        setupAddSubjectButton();

        return view;
    }

    private void initializeViews(View view) {

        etSubjectName =
                view.findViewById(R.id.etSubjectName);

        etSubjectCode =
                view.findViewById(R.id.etSubjectCode);

        etTeacherName =
                view.findViewById(R.id.etTeacherName);

        spinnerSemester =
                view.findViewById(R.id.spinnerSemester);

        btnAddSubject =
                view.findViewById(R.id.btnAddSubject);
    }

    private void setupSemesterSpinner() {

        String[] semesters = {
                "Select Semester",
                "Semester 3",
                "Semester 4",
                "Semester 5",
                "Semester 6",
                "Semester 7",
                "Semester 8"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        semesters
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerSemester.setAdapter(adapter);
    }

    private void setupAddSubjectButton() {

        btnAddSubject.setOnClickListener(v -> {

            String subjectName =
                    etSubjectName.getText()
                            .toString()
                            .trim();

            String subjectCode =
                    etSubjectCode.getText()
                            .toString()
                            .trim();

            String teacherName =
                    etTeacherName.getText()
                            .toString()
                            .trim();

            String semester =
                    spinnerSemester.getSelectedItem()
                            .toString();

            if (subjectName.isEmpty()) {

                etSubjectName.setError(
                        "Enter subject name"
                );

                etSubjectName.requestFocus();
                return;
            }

            if (subjectCode.isEmpty()) {

                etSubjectCode.setError(
                        "Enter subject code"
                );

                etSubjectCode.requestFocus();
                return;
            }

            if (teacherName.isEmpty()) {

                etTeacherName.setError(
                        "Enter teacher name"
                );

                etTeacherName.requestFocus();
                return;
            }

            if (semester.equals("Select Semester")) {

                Toast.makeText(
                        requireContext(),
                        "Please select semester",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Subject added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }
}