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

        TextView btnPrivacy = view.findViewById(R.id.btnPrivacy);
        TextView btnSettings = view.findViewById(R.id.btnSettings);

        btnPrivacy.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.mainContainer,
                            new PrivacyFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });

        btnSettings.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.mainContainer,
                            new SettingsFragment()
                    )
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}