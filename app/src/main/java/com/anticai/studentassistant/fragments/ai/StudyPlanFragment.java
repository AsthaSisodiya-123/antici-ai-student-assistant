package com.anticai.studentassistant.fragments.ai;

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
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.StudyPlanResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudyPlanFragment extends Fragment {

    private TextView tvPlanTitle;
    private TextView tvPlanDescription;
    private TextView btnStartPlan;

    private TextView tvSession1Time;
    private TextView tvSession1Title;
    private TextView tvSession1Description;

    private TextView tvSession2Time;
    private TextView tvSession2Title;
    private TextView tvSession2Description;

    private TextView tvSession3Time;
    private TextView tvSession3Title;
    private TextView tvSession3Description;

    public StudyPlanFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_study_plan,
                container,
                false
        );

        bindViews(view);

        btnStartPlan.setOnClickListener(
                v -> startStudyPlan()
        );

        loadStudyPlan();

        return view;
    }

    private void bindViews(View view) {

        tvPlanTitle =
                view.findViewById(R.id.tvPlanTitle);

        tvPlanDescription =
                view.findViewById(R.id.tvPlanDescription);

        btnStartPlan =
                view.findViewById(R.id.btnStartPlan);

        tvSession1Time =
                view.findViewById(R.id.tvSession1Time);

        tvSession1Title =
                view.findViewById(R.id.tvSession1Title);

        tvSession1Description =
                view.findViewById(R.id.tvSession1Description);

        tvSession2Time =
                view.findViewById(R.id.tvSession2Time);

        tvSession2Title =
                view.findViewById(R.id.tvSession2Title);

        tvSession2Description =
                view.findViewById(R.id.tvSession2Description);

        tvSession3Time =
                view.findViewById(R.id.tvSession3Time);

        tvSession3Title =
                view.findViewById(R.id.tvSession3Title);

        tvSession3Description =
                view.findViewById(R.id.tvSession3Description);
    }

    private void loadStudyPlan() {

        ApiService apiService =
                RetrofitClient
                        .getInstance(requireContext())
                        .create(ApiService.class);

        Call<StudyPlanResponse> call =
                apiService.getStudyPlan();

        call.enqueue(new Callback<StudyPlanResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<StudyPlanResponse> call,
                    @NonNull Response<StudyPlanResponse> response) {

                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful()
                        && response.body() != null) {

                    displayStudyPlan(response.body());

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Could not load study plan",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<StudyPlanResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Unable to connect to Study Plan",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void displayStudyPlan(
            StudyPlanResponse plan) {

        if (plan.getTitle() != null) {
            tvPlanTitle.setText(plan.getTitle());
        }

        if (plan.getDescription() != null) {
            tvPlanDescription.setText(
                    plan.getDescription()
            );
        }

        List<StudyPlanResponse.StudyPlanItem> sessions =
                plan.getSessions();

        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        if (sessions.size() >= 1) {

            StudyPlanResponse.StudyPlanItem session =
                    sessions.get(0);

            tvSession1Time.setText(
                    formatTime(session)
            );

            tvSession1Title.setText(
                    safeText(session.getSubject())
            );

            tvSession1Description.setText(
                    safeText(session.getTopic())
            );
        }

        if (sessions.size() >= 2) {

            StudyPlanResponse.StudyPlanItem session =
                    sessions.get(1);

            tvSession2Time.setText(
                    formatTime(session)
            );

            tvSession2Title.setText(
                    safeText(session.getSubject())
            );

            tvSession2Description.setText(
                    safeText(session.getTopic())
            );
        }

        if (sessions.size() >= 3) {

            StudyPlanResponse.StudyPlanItem session =
                    sessions.get(2);

            tvSession3Time.setText(
                    formatTime(session)
            );

            tvSession3Title.setText(
                    safeText(session.getSubject())
            );

            tvSession3Description.setText(
                    safeText(session.getTopic())
            );
        }
    }

    private String formatTime(
            StudyPlanResponse.StudyPlanItem session) {

        String time = safeText(session.getTime());

        Integer duration =
                session.getDurationMinutes();

        if (duration != null) {
            return time + " • " + duration + " MIN";
        }

        return time;
    }

    private String safeText(String value) {

        if (value == null || value.isBlank()) {
            return "";
        }

        return value;
    }

    private void startStudyPlan() {

        Toast.makeText(
                requireContext(),
                "Study plan started",
                Toast.LENGTH_SHORT
        ).show();

        btnStartPlan.setText(
                "STUDY PLAN ACTIVE  ✓"
        );

        btnStartPlan.setEnabled(false);
    }
}
