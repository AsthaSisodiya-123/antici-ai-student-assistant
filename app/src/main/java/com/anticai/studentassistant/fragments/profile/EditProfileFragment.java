package com.anticai.studentassistant.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class EditProfileFragment extends Fragment {

    private EditText etName;
    private EditText etEmail;
    private EditText etCollege;
    private EditText etCourse;
    private EditText etYear;
    private Button btnSaveProfile;
    private ImageView btnBack;

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
        loadSavedProfile();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etCollege = view.findViewById(R.id.etCollege);
        etCourse = view.findViewById(R.id.etCourse);
        etYear = view.findViewById(R.id.etYear);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void loadSavedProfile() {
        if (getContext() == null) return;

        android.content.SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "AnticiAIProfile",
                        Context.MODE_PRIVATE
                );

        etName.setText(preferences.getString(
                "name", "Astha Sisodiya"
        ));

        etEmail.setText(preferences.getString(
                "email", ""
        ));

        etCollege.setText(preferences.getString(
                "college", "NMIET, Talegaon Dabhade"
        ));

        etCourse.setText(preferences.getString(
                "course", "B.E. Computer Engineering"
        ));

        etYear.setText(preferences.getString(
                "year", "Second Year"
        ));
    }

    private void setupClickListeners() {
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String college = etCollege.getText().toString().trim();
        String course = etCourse.getText().toString().trim();
        String year = etYear.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter your name");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)
                || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(college)) {
            etCollege.setError("Please enter your college");
            etCollege.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(course)) {
            etCourse.setError("Please enter your course");
            etCourse.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(year)) {
            etYear.setError("Please enter your academic year");
            etYear.requestFocus();
            return;
        }

        android.content.SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "AnticiAIProfile",
                        Context.MODE_PRIVATE
                );

        boolean saved = preferences.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("college", college)
                .putString("course", course)
                .putString("year", year)
                .commit();

        if (saved) {
            Toast.makeText(
                    requireContext(),
                    "Profile saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        } else {
            Toast.makeText(
                    requireContext(),
                    "Could not save profile. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
