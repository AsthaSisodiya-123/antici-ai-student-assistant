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

public class TimetableFragment extends Fragment {

    private TextView btnAddClass;

    private View mondayClass;
    private View tuesdayClass;
    private View wednesdayClass;
    private View thursdayClass;
    private View fridayClass;

    public TimetableFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_timetable,
                container,
                false
        );

        initializeViews(view);
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {

        btnAddClass =
                view.findViewById(R.id.btnAddClass);

        mondayClass =
                view.findViewById(R.id.mondayClass);

        tuesdayClass =
                view.findViewById(R.id.tuesdayClass);

        wednesdayClass =
                view.findViewById(R.id.wednesdayClass);

        thursdayClass =
                view.findViewById(R.id.thursdayClass);

        fridayClass =
                view.findViewById(R.id.fridayClass);
    }

    private void setupClickListeners() {

        // Add new class
        btnAddClass.setOnClickListener(v ->
                openFragment(new AddClassFragment())
        );

        // Existing timetable classes
        mondayClass.setOnClickListener(v ->
                showClass("Monday")
        );

        tuesdayClass.setOnClickListener(v ->
                showClass("Tuesday")
        );

        wednesdayClass.setOnClickListener(v ->
                showClass("Wednesday")
        );

        thursdayClass.setOnClickListener(v ->
                showClass("Thursday")
        );

        fridayClass.setOnClickListener(v ->
                showClass("Friday")
        );
    }

    private void showClass(String day) {

        Toast.makeText(
                requireContext(),
                day + " class selected",
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