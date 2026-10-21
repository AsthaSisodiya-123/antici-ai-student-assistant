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

    public ProfileFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        initializeViews(view);
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

        SharedPreferences preferences =
                requireContext().getSharedPreferences(
                        "AnticiAIProfile",
                        Context.MODE_PRIVATE
                );

        String name = preferences.getString(
                "name",
                "Astha Sisodiya"
        );

        String email = preferences.getString(
                "email",
                "Your registered email"
        );

        String college = preferences.getString(
                "college",
                "NMIET, Talegaon Dabhade"
        );

        String course = preferences.getString(
                "course",
                "B.E. Computer Engineering"
        );

        String year = preferences.getString(
                "year",
                "Second Year"
        );

        tvProfileName.setText(name);
        tvProfileEmail.setText(email);
        tvProfileCollege.setText(college);
        tvProfileCourse.setText(course);
        tvProfileYear.setText(year);

        tvProfileInitials.setText(getInitials(name));
        tvProfileRole.setText("College Student");
    }

    private String getInitials(String name) {

        if (name == null || name.trim().isEmpty()) {
            return "U";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
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
                openFragment(new SettingsFragment())
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
                "Connect logout to your authentication system.",
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
