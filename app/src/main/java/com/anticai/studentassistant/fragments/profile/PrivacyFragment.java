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

public class PrivacyFragment extends Fragment {

    private TextView btnPrivacyBack;

    public PrivacyFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_privacy,
                container,
                false
        );

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        btnPrivacyBack = view.findViewById(
                R.id.btnPrivacyBack
        );
    }

    private void setupClickListeners() {
        btnPrivacyBack.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}
