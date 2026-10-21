package com.anticai.studentassistant.fragments.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class HelpSupportFragment extends Fragment {

    private TextView btnHelpBack;
    private Button btnContactSupport;

    // Replace this with your actual support email address.
    private static final String SUPPORT_EMAIL = "your-support-email@example.com";

    public HelpSupportFragment() {
        // Required empty public constructor
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
        btnHelpBack = view.findViewById(R.id.btnHelpBack);
        btnContactSupport = view.findViewById(R.id.btnContactSupport);
    }

    private void setupClickListeners() {

        btnHelpBack.setOnClickListener(v -> goBack());

        btnContactSupport.setOnClickListener(v -> contactSupport());
    }

    private void goBack() {
        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    private void contactSupport() {

        if (!isAdded()) {
            return;
        }

        if (SUPPORT_EMAIL.equals("your-support-email@example.com")) {
            Toast.makeText(
                    requireContext(),
                    "Please configure your support email in HelpSupportFragment.java.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Intent emailIntent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + SUPPORT_EMAIL)
        );

        emailIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Antici AI - Support Request"
        );

        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hello Antici AI Support Team,\n\n"
                        + "I need help with the Antici AI application.\n\n"
                        + "Issue description:\n\n"
                        + "Thank you."
        );

        try {
            startActivity(
                    Intent.createChooser(
                            emailIntent,
                            "Contact Antici AI Support"
                    )
            );
        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    requireContext(),
                    "No email application is available on this device.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
