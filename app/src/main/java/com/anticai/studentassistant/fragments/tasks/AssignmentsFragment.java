package com.anticai.studentassistant.fragments.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class AssignmentsFragment extends Fragment {

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

        TextView btnBack = view.findViewById(R.id.btnBack);

        LinearLayout assignmentDbms =
                view.findViewById(R.id.assignmentDbms);

        LinearLayout assignmentDsa =
                view.findViewById(R.id.assignmentDsa);

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        assignmentDbms.setOnClickListener(v ->
                openFragment(new AssignmentDetailFragment())
        );

        assignmentDsa.setOnClickListener(v ->
                openFragment(new AssignmentDetailFragment())
        );

        return view;
    }

    private void openFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}