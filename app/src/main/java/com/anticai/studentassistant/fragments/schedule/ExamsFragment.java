package com.anticai.studentassistant.fragments.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class ExamsFragment extends Fragment {

    private TextView btnAddExam;

    private View examDbms;
    private View examDsa;

    public ExamsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_exams,
                container,
                false
        );

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {

        btnAddExam =
                view.findViewById(R.id.btnAddExam);

        examDbms =
                view.findViewById(R.id.examDbms);

        examDsa =
                view.findViewById(R.id.examDsa);
    }

    private void setupClickListeners() {

        // Add new exam
        btnAddExam.setOnClickListener(v ->
                openFragment(new AddExamFragment())
        );

        // DBMS exam
        examDbms.setOnClickListener(v ->
                showExam("DBMS")
        );

        // DSA exam
        examDsa.setOnClickListener(v ->
                showExam("DSA")
        );
    }

    private void showExam(String subject) {

        android.widget.Toast.makeText(
                requireContext(),
                subject + " Exam selected",
                android.widget.Toast.LENGTH_SHORT
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