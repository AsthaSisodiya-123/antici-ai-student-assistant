
package com.anticai.studentassistant.fragments.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.ProfileResponse;
import com.anticai.studentassistant.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView btnEditProfile;

    private View btnPrivacy;
    private View btnSettings;
    private View btnHelpSupport;
    private View btnLogout;

    private TextView tvProfileInitials;
    private TextView tvProfileName;
    private TextView tvProfileRole;
    private TextView tvProfileEmail;
    private TextView tvProfileCollege;
    private TextView tvProfileCourse;
    private TextView tvProfileYear;

    private SharedPreferences preferences;
    private ApiService apiService;

    public ProfileFragment() {
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
                R.layout.fragment_profile,
                container,
                false
        );

        initializeViews(view);

        preferences = requireContext().getSharedPreferences(
                "AnticiAIProfile",
                Context.MODE_PRIVATE
        );

        apiService = RetrofitClient.getApiService(requireContext());

        loadProfileDetails();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnPrivacy = view.findViewById(R.id.btnPrivacy);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnHelpSupport = view.findViewById(R.id.btnHelpSupport);
        btnLogout = view.findViewById(R.id.btnLogout);

        tvProfileInitials = view.findViewById(R.id.tvProfileInitials);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileRole = view.findViewById(R.id.tvProfileRole);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfileCollege = view.findViewById(R.id.tvProfileCollege);
        tvProfileCourse = view.findViewById(R.id.tvProfileCourse);
        tvProfileYear = view.findViewById(R.id.tvProfileYear);
    }

    private void loadProfileDetails() {
        if (!isAdded()) {
            return;
        }

        String name = preferences.getString("name", "Student");
        String email = preferences.getString("email", "");
        String college = preferences.getString("college", "");
        String course = preferences.getString("course", "");
        String year = preferences.getString("year", "");

        displayProfile(name, email, college, course, year);
        loadProfileFromServer();
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

                    String name = nonNull(profile.getName(), "Student");
                    String email = nonNull(profile.getEmail(), "");
                    String college = nonNull(profile.getCollege(), "");
                    String course = nonNull(profile.getCourse(), "");
                    String year = nonNull(profile.getYear(), "");

                    preferences.edit()
                            .putString("name", name)
                            .putString("email", email)
                            .putString("college", college)
                            .putString("course", course)
                            .putString("year", year)
                            .apply();

                    displayProfile(name, email, college, course, year);

                } else if (response.code() == 401) {
                    Toast.makeText(
                            requireContext(),
                            "Please log in again to load your profile.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ProfileResponse> call,
                    @NonNull Throwable t
            ) {
                // Keep displaying locally cached profile information.
            }
        });
    }

    private void displayProfile(
            String name,
            String email,
            String college,
            String course,
            String year
    ) {
        tvProfileName.setText(name);
        tvProfileEmail.setText(
                email.isEmpty() ? "Email not available" : email
        );
        tvProfileCollege.setText(
                college.isEmpty() ? "College not added" : college
        );
        tvProfileCourse.setText(
                course.isEmpty() ? "Course not added" : course
        );
        tvProfileYear.setText(
                year.isEmpty() ? "Academic year not added" : year
        );

        tvProfileInitials.setText(getInitials(name));
        tvProfileRole.setText("College Student");
    }

    private String nonNull(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "S";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (
                parts[0].substring(0, 1)
                        + parts[parts.length - 1].substring(0, 1)
        ).toUpperCase();
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v ->
                openFragment(new EditProfileFragment())
        );

        btnPrivacy.setOnClickListener(v ->
                openFragment(new PrivacyFragment())
        );

        btnSettings.setOnClickListener(v ->
                openFragment(new com.anticai.studentassistant.fragments.profile.SettingsFragment())
        );

        btnHelpSupport.setOnClickListener(v ->
                openFragment(new HelpSupportFragment())
        );

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v ->
                    showLogoutConfirmation()
            );
        }
    }

    private void openFragment(Fragment fragment) {
        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showLogoutConfirmation() {
        if (!isAdded()) {
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Sign Out")
                .setMessage(
                        "Are you sure you want to sign out of Antici AI?"
                )
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss()
                )
                .setPositiveButton(
                        "Sign Out",
                        (dialog, which) -> performLogout()
                )
                .show();
    }

    private void performLogout() {
        Toast.makeText(
                requireContext(),
                "Connect this action to your existing logout flow.",
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() != null && isAdded()) {
            loadProfileDetails();
        }
    }
}
