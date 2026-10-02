package com.anticai.studentassistant.fragments.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class StudyTrackerFragment extends Fragment {

    public StudyTrackerFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_study_tracker,
                container,
                false
        );

        TextView btnStartSession =
                view.findViewById(R.id.btnStartSession);

        TextView btnHistory =
                view.findViewById(R.id.btnHistory);

        btnStartSession.setOnClickListener(v ->
                openFragment(new StudySessionFragment())
        );

        btnHistory.setOnClickListener(v ->
                openFragment(new StudyHistoryFragment())
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