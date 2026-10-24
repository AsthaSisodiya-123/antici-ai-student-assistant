
package com.anticai.studentassistant.fragments.profile;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.ProfileResponse;
import com.anticai.studentassistant.network.ProfileUpdateRequest;
import com.anticai.studentassistant.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private EditText etName;
    private EditText etEmail;
    private EditText etCollege;
    private EditText etCourse;
    private EditText etYear;

    private Button btnSaveProfile;
    private ImageView btnBack;

    private ApiService apiService;
    private SharedPreferences preferences;

    private boolean isSaving = false;

    public EditProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_edit_profile,
                container,
                false
        );

        initializeViews(view);

        preferences = requireContext().getSharedPreferences(
                "AnticiAIProfile",
                Context.MODE_PRIVATE
        );

        apiService = RetrofitClient.getApiService(requireContext());

        loadSavedProfile();
        setupClickListeners();
        loadProfileFromServer();

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
        etName.setText(preferences.getString("name", ""));
        etEmail.setText(preferences.getString("email", ""));
        etCollege.setText(preferences.getString("college", ""));
        etCourse.setText(preferences.getString("course", ""));
        etYear.setText(preferences.getString("year", ""));
    }

    private void setupClickListeners() {
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        btnBack.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack();
            }
        });
    }

    private void loadProfileFromServer() {
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<ProfileResponse> call,
                    @NonNull Response<ProfileResponse> response
            ) {
                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();

                    setTextIfNotNull(etName, profile.getName());
                    setTextIfNotNull(etEmail, profile.getEmail());
                    setTextIfNotNull(etCollege, profile.getCollege());
                    setTextIfNotNull(etCourse, profile.getCourse());
                    setTextIfNotNull(etYear, profile.getYear());

                    saveLocally(
                            value(profile.getName()),
                            value(profile.getEmail()),
                            value(profile.getCollege()),
                            value(profile.getCourse()),
                            value(profile.getYear())
                    );
                } else if (response.code() == 401) {
                    showToast("Session expired. Please log in again.");
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ProfileResponse> call,
                    @NonNull Throwable t
            ) {
                // Local values remain available when the server is unreachable.
            }
        });
    }

    private void saveProfile() {
        if (isSaving) {
            return;
        }

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

        /*
         * The backend updates name, college, course and year.
         * Email is read-only here because changing the account email
         * requires a separate authenticated backend feature.
         */
        isSaving = true;
        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Saving...");

        ProfileUpdateRequest request = new ProfileUpdateRequest(
                name,
                college,
                course,
                year
        );

        apiService.updateProfile(request).enqueue(
                new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ProfileResponse> call,
                            @NonNull Response<ProfileResponse> response
                    ) {
                        isSaving = false;

                        if (!isAdded()) {
                            return;
                        }

                        btnSaveProfile.setEnabled(true);
                        btnSaveProfile.setText("Save Profile");

                        if (response.isSuccessful()
                                && response.body() != null) {

                            ProfileResponse profile = response.body();

                            String savedName = profile.getName() != null
                                    ? profile.getName() : name;
                            String savedEmail = profile.getEmail() != null
                                    ? profile.getEmail() : email;
                            String savedCollege = profile.getCollege() != null
                                    ? profile.getCollege() : college;
                            String savedCourse = profile.getCourse() != null
                                    ? profile.getCourse() : course;
                            String savedYear = profile.getYear() != null
                                    ? profile.getYear() : year;

                            saveLocally(
                                    savedName,
                                    savedEmail,
                                    savedCollege,
                                    savedCourse,
                                    savedYear
                            );

                            Toast.makeText(
                                    requireContext(),
                                    "Profile updated successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            requireActivity()
                                    .getSupportFragmentManager()
                                    .popBackStack();

                        } else if (response.code() == 401) {
                            showToast(
                                    "Session expired. Please log in again."
                            );
                        } else {
                            showToast(
                                    "Could not update profile. Error: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ProfileResponse> call,
                            @NonNull Throwable t
                    ) {
                        isSaving = false;

                        if (!isAdded()) {
                            return;
                        }

                        btnSaveProfile.setEnabled(true);
                        btnSaveProfile.setText("Save Profile");

                        showToast(
                                "Server connection failed. "
                                        + "Profile was not saved to the server."
                        );
                    }
                }
        );
    }

    private void saveLocally(
            String name,
            String email,
            String college,
            String course,
            String year
    ) {
        preferences.edit()
                .putString("name", name)
                .putString("email", email)
                .putString("college", college)
                .putString("course", course)
                .putString("year", year)
                .apply();
    }

    private void setTextIfNotNull(EditText field, String text) {
        if (text != null) {
            field.setText(text);
        }
    }

    private String value(String text) {
        return text == null ? "" : text;
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
