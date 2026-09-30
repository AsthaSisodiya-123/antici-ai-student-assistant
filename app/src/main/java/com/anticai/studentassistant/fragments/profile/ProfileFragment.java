package com.anticai.studentassistant.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class ProfileFragment extends Fragment {

    private TextView btnEditProfile;
    private TextView btnPrivacy;
    private TextView btnSettings;
    private TextView btnHelpSupport;

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
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {

        btnEditProfile =
                view.findViewById(R.id.btnEditProfile);

        btnPrivacy =
                view.findViewById(R.id.btnPrivacy);

        btnSettings =
                view.findViewById(R.id.btnSettings);

        btnHelpSupport =
                view.findViewById(R.id.btnHelpSupport);
    }

    private void setupClickListeners() {

        // Edit Profile
        btnEditProfile.setOnClickListener(v ->
                openFragment(new EditProfileFragment())
        );

        // Privacy
        btnPrivacy.setOnClickListener(v ->
                openFragment(new PrivacyFragment())
        );

        // Settings
        btnSettings.setOnClickListener(v ->
                openFragment(new SettingsFragment())
        );

        // Help & Support
        btnHelpSupport.setOnClickListener(v ->
                openFragment(new HelpSupportFragment())
        );
    }

    private void openFragment(Fragment fragment) {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(
                        R.id.mainContainer,
                        fragment
                )
                .addToBackStack(null)
                .commit();
    }
}