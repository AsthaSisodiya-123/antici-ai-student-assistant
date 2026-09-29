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

public class TaskDetailFragment extends Fragment {

    public TaskDetailFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_task_detail,
                container,
                false
        );

        TextView btnBack = view.findViewById(R.id.btnBack);
        TextView btnComplete = view.findViewById(R.id.btnComplete);

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        btnComplete.setOnClickListener(v ->
                btnComplete.setText("TASK COMPLETED")
        );

        return view;
    }
}