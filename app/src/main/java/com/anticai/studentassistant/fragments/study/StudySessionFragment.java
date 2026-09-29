package com.anticai.studentassistant.fragments.study;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class StudySessionFragment extends Fragment {

    private TextView tvTimer;
    private TextView btnSession;

    private Handler handler = new Handler();
    private int seconds = 0;
    private boolean isRunning = false;

    public StudySessionFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_study_session,
                container,
                false
        );

        tvTimer = view.findViewById(R.id.tvTimer);
        btnSession = view.findViewById(R.id.btnSession);

        TextView btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        btnSession.setOnClickListener(v -> toggleSession());

        return view;
    }

    private void toggleSession() {

        if (!isRunning) {

            isRunning = true;
            btnSession.setText("END STUDY SESSION");

            handler.post(timerRunnable);

        } else {

            isRunning = false;
            btnSession.setText("SESSION COMPLETED");

            handler.removeCallbacks(timerRunnable);
        }
    }

    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            if (isRunning) {

                seconds++;

                int minutes = seconds / 60;
                int remainingSeconds = seconds % 60;

                String time = String.format(
                        "%02d:%02d",
                        minutes,
                        remainingSeconds
                );

                tvTimer.setText(time);

                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        handler.removeCallbacks(timerRunnable);
    }
}