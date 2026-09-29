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

public class TasksFragment extends Fragment {

    public TasksFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_tasks,
                container,
                false
        );

        // Assignments button
        TextView btnAssignments =
                view.findViewById(R.id.btnAssignments);

        // Task cards are LinearLayouts in XML,
        // therefore use View instead of TextView.
        View taskDbms =
                view.findViewById(R.id.taskDbms);

        View taskDsa =
                view.findViewById(R.id.taskDsa);

        // Open Assignments screen
        btnAssignments.setOnClickListener(v ->
                openFragment(new AssignmentsFragment())
        );

        // Open DBMS task details
        taskDbms.setOnClickListener(v ->
                openFragment(new TaskDetailFragment())
        );

        // Open DSA task details
        taskDsa.setOnClickListener(v ->
                openFragment(new TaskDetailFragment())
        );

        return view;
    }

    private void openFragment(Fragment fragment) {

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}

