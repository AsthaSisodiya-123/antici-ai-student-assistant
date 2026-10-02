package com.anticai.studentassistant.fragments.profile;

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

public class HelpSupportFragment extends Fragment {

    private TextView faqAccount;
    private TextView faqTasks;
    private TextView faqPredictions;
    private TextView faqPrivacy;
    private TextView btnContactSupport;

    public HelpSupportFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_help_support,
                container,
                false
        );

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {

        faqAccount = view.findViewById(R.id.faqAccount);
        faqTasks = view.findViewById(R.id.faqTasks);
        faqPredictions = view.findViewById(R.id.faqPredictions);
        faqPrivacy = view.findViewById(R.id.faqPrivacy);

        btnContactSupport =
                view.findViewById(R.id.btnContactSupport);
    }

    private void setupClickListeners() {

        faqAccount.setOnClickListener(v ->
                showMessage(
                        "Account & Login: Manage your profile, password and account settings."
                )
        );

        faqTasks.setOnClickListener(v ->
                showMessage(
                        "Tasks & Assignments: Add deadlines and track your academic work."
                )
        );

        faqPredictions.setOnClickListener(v ->
                showMessage(
                        "AI Predictions: AnticiAI uses permitted activity data to generate predictions."
                )
        );

        faqPrivacy.setOnClickListener(v ->
                showMessage(
                        "Privacy & Data: You control which optional data sources are permitted."
                )
        );

        btnContactSupport.setOnClickListener(v ->
                showMessage(
                        "Support contact will be connected later."
                )
        );
    }

    private void showMessage(String message) {

        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}