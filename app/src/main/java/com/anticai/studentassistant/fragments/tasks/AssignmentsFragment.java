package com.anticai.studentassistant.fragments.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class AssignmentsFragment extends Fragment {

    private TextView btnAddAssignment;

    private View assignmentDbms;
    private View assignmentDsa;

    public AssignmentsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_assignments,
                container,
                false
        );

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {

        btnAddAssignment =
                view.findViewById(R.id.btnAddAssignment);

        assignmentDbms =
                view.findViewById(R.id.assignmentDbms);

        assignmentDsa =
                view.findViewById(R.id.assignmentDsa);
    }

    private void setupClickListeners() {

        // Add new assignment
        btnAddAssignment.setOnClickListener(v ->
                openFragment(new AddAssignmentFragment())
        );

        // DBMS assignment
        assignmentDbms.setOnClickListener(v ->
                openFragment(new AssignmentDetailFragment())
        );

        // DSA assignment
        assignmentDsa.setOnClickListener(v ->
                openFragment(new AssignmentDetailFragment())
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