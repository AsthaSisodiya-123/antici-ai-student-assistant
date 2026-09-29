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

        TextView btnAssignments =
                view.findViewById(R.id.btnAssignments);

        TextView taskDbms =
                view.findViewById(R.id.taskDbms);

        TextView taskDsa =
                view.findViewById(R.id.taskDsa);

        btnAssignments.setOnClickListener(v ->
                openFragment(new AssignmentsFragment())
        );

        taskDbms.setOnClickListener(v ->
                openFragment(new TaskDetailFragment())
        );

        taskDsa.setOnClickListener(v ->
                openFragment(new TaskDetailFragment())
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