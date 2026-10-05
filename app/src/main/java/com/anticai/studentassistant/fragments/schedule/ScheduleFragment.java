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

public class ScheduleFragment extends Fragment {

    public ScheduleFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_schedule,
                container,
                false
        );

        TextView btnExams = view.findViewById(R.id.btnExams);
        TextView btnSubjects = view.findViewById(R.id.btnSubjects);
        TextView btnTimetable = view.findViewById(R.id.btnTimetable);

        btnExams.setOnClickListener(v -> openFragment(new ExamsFragment()));

        btnSubjects.setOnClickListener(v -> openFragment(new SubjectsFragment()));

        btnTimetable.setOnClickListener(v -> openFragment(new TimetableFragment()));

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