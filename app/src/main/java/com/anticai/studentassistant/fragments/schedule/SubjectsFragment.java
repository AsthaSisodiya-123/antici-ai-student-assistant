package com.anticai.studentassistant.fragments.schedule;

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

public class SubjectsFragment extends Fragment {

    private TextView btnAddSubject;

    private View subjectDbms;
    private View subjectDsa;
    private View subjectJava;
    private View subjectAiMl;

    public SubjectsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_subjects,
                container,
                false
        );

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {

        btnAddSubject =
                view.findViewById(R.id.btnAddSubject);

        subjectDbms =
                view.findViewById(R.id.subjectDbms);

        subjectDsa =
                view.findViewById(R.id.subjectDsa);

        subjectJava =
                view.findViewById(R.id.subjectJava);

        subjectAiMl =
                view.findViewById(R.id.subjectAiMl);
    }

    private void setupClickListeners() {

        // Add new subject
        btnAddSubject.setOnClickListener(v ->
                openFragment(new AddSubjectFragment())
        );

        // Existing subjects
        subjectDbms.setOnClickListener(v ->
                showSubject("Database Management Systems")
        );

        subjectDsa.setOnClickListener(v ->
                showSubject("Data Structures & Algorithms")
        );

        subjectJava.setOnClickListener(v ->
                showSubject("Core Java")
        );

        subjectAiMl.setOnClickListener(v ->
                showSubject("Artificial Intelligence & ML")
        );
    }

    private void showSubject(String subject) {

        Toast.makeText(
                requireContext(),
                subject + " selected",
                Toast.LENGTH_SHORT
        ).show();
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