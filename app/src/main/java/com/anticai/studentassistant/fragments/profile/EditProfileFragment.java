package com.anticai.studentassistant.fragments.profile;

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

public class EditProfileFragment extends Fragment {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etCollege;
    private EditText etBranch;

    private Spinner spinnerSemester;

    private Button btnSaveChanges;

    public EditProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_edit_profile,
                container,
                false
        );

        initializeViews(view);
        setupSemesterSpinner();
        setupSaveButton();

        return view;
    }

    private void initializeViews(View view) {

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etCollege = view.findViewById(R.id.etCollege);
        etBranch = view.findViewById(R.id.etBranch);

        spinnerSemester =
                view.findViewById(R.id.spinnerSemester);

        btnSaveChanges =
                view.findViewById(R.id.btnSaveChanges);
    }

    private void setupSemesterSpinner() {

        String[] semesters = {
                "Select Semester",
                "Semester 1",
                "Semester 2",
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

    private void setupSaveButton() {

        btnSaveChanges.setOnClickListener(v -> {

            String name =
                    etFullName.getText()
                            .toString()
                            .trim();

            String email =
                    etEmail.getText()
                            .toString()
                            .trim();

            String phone =
                    etPhone.getText()
                            .toString()
                            .trim();

            String college =
                    etCollege.getText()
                            .toString()
                            .trim();

            String branch =
                    etBranch.getText()
                            .toString()
                            .trim();

            String semester =
                    spinnerSemester.getSelectedItem()
                            .toString();

            if (name.isEmpty()) {

                etFullName.setError(
                        "Enter your full name"
                );

                etFullName.requestFocus();
                return;
            }

            if (email.isEmpty()) {

                etEmail.setError(
                        "Enter your email"
                );

                etEmail.requestFocus();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()) {

                etEmail.setError(
                        "Enter a valid email"
                );

                etEmail.requestFocus();
                return;
            }

            if (phone.isEmpty()) {

                etPhone.setError(
                        "Enter your phone number"
                );

                etPhone.requestFocus();
                return;
            }

            if (college.isEmpty()) {

                etCollege.setError(
                        "Enter your college"
                );

                etCollege.requestFocus();
                return;
            }

            if (branch.isEmpty()) {

                etBranch.setError(
                        "Enter your branch"
                );

                etBranch.requestFocus();
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
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
    }
}