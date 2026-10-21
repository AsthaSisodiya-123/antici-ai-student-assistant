package com.anticai.studentassistant.fragments.ai;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.StudyPlanRequest;
import com.anticai.studentassistant.network.StudyPlanResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudyPlanFragment extends Fragment {

    // Summary views
    private TextView tvActivePlans;
    private TextView tvStudyTimeToday;
    private TextView tvDailyGoalProgress;
    private ProgressBar progressDailyGoal;

    // Create plan views
    private EditText etPlanSubject;
    private EditText etPlanTopic;
    private EditText etPlanExamDate;
    private EditText etPlanMinutes;
    private Spinner spPlanDifficulty;
    private TextView btnCreatePlan;
    private ProgressBar progressCreatePlan;
    private TextView tvPlanStatus;

    // Plan list
    private LinearLayout layoutPlans;
    private TextView tvPlansEmpty;

    // Focus session views
    private TextView tvFocusSubject;
    private TextView tvFocusTimer;
    private ProgressBar progressFocusTimer;
    private TextView btnStartSession;
    private TextView btnPauseSession;
    private TextView btnFinishSession;

    // History views
    private TextView btnViewHistory;
    private TextView tvHistorySummary;
    private LinearLayout layoutHistory;

    // API
    private ApiService apiService;

    // Local session history
    private SharedPreferences historyPreferences;
    private static final String HISTORY_KEY = "study_session_history";

    // Timer
    private final Handler timerHandler =
            new Handler(Looper.getMainLooper());

    private int totalSeconds = 25 * 60;
    private int remainingSeconds = 25 * 60;

    private boolean isSessionRunning = false;
    private boolean isSessionPaused = false;
    private boolean isSessionFinished = false;

    private String sessionSubject = "Focus Session";

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            if (!isAdded() || !isSessionRunning || isSessionPaused) {
                return;
            }

            if (remainingSeconds > 0) {
                remainingSeconds--;
                updateTimerDisplay();
                timerHandler.postDelayed(this, 1000);
            } else {
                finishSession(true);
            }
        }
    };

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

        // Summary
        tvActivePlans = view.findViewById(R.id.tvActivePlans);
        tvStudyTimeToday = view.findViewById(R.id.tvStudyTimeToday);
        tvDailyGoalProgress = view.findViewById(R.id.tvDailyGoalProgress);
        progressDailyGoal = view.findViewById(R.id.progressDailyGoal);

        // Plan form
        etPlanSubject = view.findViewById(R.id.etPlanSubject);
        etPlanTopic = view.findViewById(R.id.etPlanTopic);
        etPlanExamDate = view.findViewById(R.id.etPlanExamDate);
        etPlanMinutes = view.findViewById(R.id.etPlanMinutes);
        spPlanDifficulty = view.findViewById(R.id.spPlanDifficulty);

        btnCreatePlan = view.findViewById(R.id.btnCreatePlan);
        progressCreatePlan = view.findViewById(R.id.progressCreatePlan);
        tvPlanStatus = view.findViewById(R.id.tvPlanStatus);

        // Plans list
        layoutPlans = view.findViewById(R.id.layoutPlans);
        tvPlansEmpty = view.findViewById(R.id.tvPlansEmpty);

        // Focus session
        tvFocusSubject = view.findViewById(R.id.tvFocusSubject);
        tvFocusTimer = view.findViewById(R.id.tvFocusTimer);
        progressFocusTimer = view.findViewById(R.id.progressFocusTimer);

        btnStartSession = view.findViewById(R.id.btnStartSession);
        btnPauseSession = view.findViewById(R.id.btnPauseSession);
        btnFinishSession = view.findViewById(R.id.btnFinishSession);

        // Study history
        btnViewHistory = view.findViewById(R.id.btnViewHistory);
        tvHistorySummary = view.findViewById(R.id.tvHistorySummary);
        layoutHistory = view.findViewById(R.id.layoutHistory);

        historyPreferences = requireContext().getSharedPreferences(
                "AnticiStudyHistory",
                Context.MODE_PRIVATE
        );

        apiService = RetrofitClient.getApiService(requireContext());

        setupDifficultySpinner();
        setupExamDatePicker();
        setupFocusSession();
        setupStudyHistory();

        progressCreatePlan.setVisibility(View.GONE);
        tvPlanStatus.setVisibility(View.GONE);

        btnCreatePlan.setOnClickListener(v -> createPlan());

        loadActivePlans();

        return view;
    }

    // ==========================================
    // DIFFICULTY SPINNER
    // ==========================================

    private void setupDifficultySpinner() {

        String[] difficulties = {"Easy", "Medium", "Hard"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                difficulties
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spPlanDifficulty.setAdapter(adapter);
    }

    // ==========================================
    // EXAM DATE PICKER
    // ==========================================

    private void setupExamDatePicker() {

        etPlanExamDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (picker, year, month, day) -> {

                        String date = String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                day
                        );

                        etPlanExamDate.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.getDatePicker().setMinDate(
                    System.currentTimeMillis() - 1000
            );

            dialog.show();
        });
    }

    // ==========================================
    // CREATE STUDY PLAN
    // ==========================================

    private void createPlan() {

        String subject = etPlanSubject.getText().toString().trim();
        String topic = etPlanTopic.getText().toString().trim();
        String examDate = etPlanExamDate.getText().toString().trim();
        String minutesText = etPlanMinutes.getText().toString().trim();

        String difficulty = spPlanDifficulty.getSelectedItem() == null
                ? "Easy"
                : spPlanDifficulty.getSelectedItem().toString();

        if (subject.isEmpty() || topic.isEmpty() || minutesText.isEmpty()) {
            showMessage("Enter subject, topic and study minutes.");
            return;
        }

        int minutes;

        try {
            minutes = Integer.parseInt(minutesText);

            if (minutes <= 0) {
                showMessage("Study minutes must be greater than zero.");
                return;
            }

        } catch (NumberFormatException e) {
            showMessage("Enter valid study minutes.");
            return;
        }

        if (!examDate.isEmpty() && !isValidDate(examDate)) {
            showMessage("Select a valid exam date.");
            return;
        }

        StudyPlanRequest request = new StudyPlanRequest(
                subject,
                topic,
                difficulty,
                examDate.isEmpty() ? null : examDate,
                minutes
        );

        btnCreatePlan.setEnabled(false);
        progressCreatePlan.setVisibility(View.VISIBLE);
        tvPlanStatus.setVisibility(View.VISIBLE);
        tvPlanStatus.setText("Creating study plan...");

        apiService.createStudyPlan(request).enqueue(
                new Callback<StudyPlanResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<StudyPlanResponse> call,
                            @NonNull Response<StudyPlanResponse> response) {

                        if (!isAdded()) {
                            return;
                        }

                        btnCreatePlan.setEnabled(true);
                        progressCreatePlan.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {

                            tvPlanStatus.setText(
                                    "Study plan created successfully!"
                            );

                            showMessage("Study plan saved successfully.");

                            etPlanSubject.setText("");
                            etPlanTopic.setText("");
                            etPlanExamDate.setText("");
                            etPlanMinutes.setText("");
                            spPlanDifficulty.setSelection(0);

                            loadActivePlans();

                        } else {

                            tvPlanStatus.setText(
                                    "Unable to create study plan."
                            );

                            showMessage(
                                    "Create failed. HTTP " + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<StudyPlanResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) {
                            return;
                        }

                        btnCreatePlan.setEnabled(true);
                        progressCreatePlan.setVisibility(View.GONE);
                        tvPlanStatus.setText("Connection failed.");

                        showMessage("Network error: " + t.getMessage());
                    }
                }
        );
    }

    private boolean isValidDate(String date) {

        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            format.setLenient(false);

            Date parsed = format.parse(date);

            return parsed != null && format.format(parsed).equals(date);

        } catch (Exception e) {
            return false;
        }
    }

    // ==========================================
    // LOAD ACTIVE STUDY PLANS
    // ==========================================

    private void loadActivePlans() {

        apiService.getActiveStudyPlans().enqueue(
                new Callback<java.util.List<StudyPlanResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<java.util.List<StudyPlanResponse>> call,
                            @NonNull Response<java.util.List<StudyPlanResponse>> response) {

                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful() && response.body() != null) {

                            java.util.List<StudyPlanResponse> plans =
                                    response.body();

                            tvActivePlans.setText(
                                    String.valueOf(plans.size())
                            );

                            layoutPlans.removeAllViews();

                            boolean empty = plans.isEmpty();

                            tvPlansEmpty.setVisibility(
                                    empty ? View.VISIBLE : View.GONE
                            );

                            layoutPlans.setVisibility(
                                    empty ? View.GONE : View.VISIBLE
                            );

                            for (StudyPlanResponse plan : plans) {
                                addPlanCard(plan);
                            }

                        } else {
                            showMessage(
                                    "Unable to load plans. HTTP "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<java.util.List<StudyPlanResponse>> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            showMessage("Could not load plans: " + t.getMessage());
                        }
                    }
                }
        );
    }

    private void addPlanCard(StudyPlanResponse plan) {

        TextView card = new TextView(requireContext());

        String subject = plan.getSubject() == null
                ? "Untitled subject" : plan.getSubject();

        String topic = plan.getTopic() == null
                ? "Not specified" : plan.getTopic();

        String difficulty = plan.getDifficulty() == null
                ? "Not specified" : plan.getDifficulty();

        String examDate = plan.getExamDate() == null
                || plan.getExamDate().trim().isEmpty()
                ? "Not set" : plan.getExamDate();

        Integer targetMinutes = plan.getTargetMinutes();

        String target = targetMinutes == null
                ? "Not set" : targetMinutes + " minutes";

        card.setText(
                subject
                        + "\nTopic: " + topic
                        + "\nDifficulty: " + difficulty
                        + "\nExam date: " + examDate
                        + "\nDaily target: " + target
        );

        card.setTextColor(0xFFF5F7FF);
        card.setTextSize(14);
        card.setLineSpacing(dp(4), 1.0f);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackgroundResource(R.drawable.bg_main_card);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.bottomMargin = dp(10);

        layoutPlans.addView(card, params);
    }

    // ==========================================
    // FOCUS SESSION
    // ==========================================

    private void setupFocusSession() {

        tvFocusSubject.setText("Ready to focus?");

        btnStartSession.setText("Start Session");
        btnPauseSession.setText("Pause Session");
        btnFinishSession.setText("Finish Session");

        btnPauseSession.setEnabled(false);
        btnFinishSession.setEnabled(false);

        updateTimerDisplay();

        btnStartSession.setOnClickListener(v -> startSession());
        btnPauseSession.setOnClickListener(v -> pauseSession());
        btnFinishSession.setOnClickListener(v -> finishSession(false));
    }

    private void startSession() {

        if (isSessionPaused) {

            isSessionPaused = false;
            isSessionRunning = true;

            btnStartSession.setText("Resume Session");
            btnPauseSession.setEnabled(true);
            btnFinishSession.setEnabled(true);

            tvFocusSubject.setText("Studying: " + sessionSubject);

            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, 1000);

            return;
        }

        if (isSessionRunning) {
            return;
        }

        int minutes = 25;

        String minutesText = etPlanMinutes.getText().toString().trim();

        if (!minutesText.isEmpty()) {
            try {
                minutes = Integer.parseInt(minutesText);
            } catch (NumberFormatException ignored) {
                minutes = 25;
            }
        }

        if (minutes <= 0) {
            minutes = 25;
        }

        totalSeconds = minutes * 60;
        remainingSeconds = totalSeconds;

        sessionSubject = etPlanSubject.getText().toString().trim();

        if (sessionSubject.isEmpty()) {
            sessionSubject = "Focus Session";
        }

        isSessionRunning = true;
        isSessionPaused = false;
        isSessionFinished = false;

        btnStartSession.setText("Resume Session");
        btnPauseSession.setEnabled(true);
        btnFinishSession.setEnabled(true);

        tvFocusSubject.setText("Studying: " + sessionSubject);

        updateTimerDisplay();

        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void pauseSession() {

        if (!isSessionRunning) {
            return;
        }

        isSessionRunning = false;
        isSessionPaused = true;

        timerHandler.removeCallbacks(timerRunnable);

        btnStartSession.setText("Resume Session");
        btnPauseSession.setEnabled(false);
        btnFinishSession.setEnabled(true);

        tvFocusSubject.setText("Session paused");

        updateTimerDisplay();
    }

    private void finishSession(boolean completedNaturally) {

        if (isSessionFinished) {
            return;
        }

        timerHandler.removeCallbacks(timerRunnable);

        isSessionRunning = false;
        isSessionPaused = false;
        isSessionFinished = true;

        int elapsedSeconds = totalSeconds - remainingSeconds;

        // Save this session to local history.
        saveStudyHistory(
                sessionSubject,
                elapsedSeconds,
                completedNaturally
        );

        btnStartSession.setText("Start Session");
        btnPauseSession.setEnabled(false);
        btnFinishSession.setEnabled(false);

        if (completedNaturally) {

            remainingSeconds = 0;
            tvFocusSubject.setText("Session completed!");

            showMessage("Great work! Your focus session is complete.");

        } else {

            tvFocusSubject.setText("Session finished");

            showMessage(
                    "Focus time: " + formatDuration(elapsedSeconds)
            );
        }

        updateTimerDisplay();

        // Refresh history immediately if it is open.
        if (layoutHistory != null
                && layoutHistory.getVisibility() == View.VISIBLE) {
            loadStudyHistory();
        }
    }

    private void updateTimerDisplay() {

        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        tvFocusTimer.setText(
                String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );

        if (progressFocusTimer != null) {

            progressFocusTimer.setMax(Math.max(totalSeconds, 1));

            progressFocusTimer.setProgress(
                    Math.max(0, totalSeconds - remainingSeconds)
            );
        }
    }

    // ==========================================
    // STUDY HISTORY
    // ==========================================

    private void setupStudyHistory() {

        layoutHistory.setVisibility(View.GONE);

        btnViewHistory.setText("View Study History");

        btnViewHistory.setOnClickListener(v -> {

            if (layoutHistory.getVisibility() == View.VISIBLE) {

                layoutHistory.setVisibility(View.GONE);
                btnViewHistory.setText("View Study History");

            } else {

                loadStudyHistory();

                layoutHistory.setVisibility(View.VISIBLE);
                btnViewHistory.setText("Hide Study History");
            }
        });
    }

    private void saveStudyHistory(
            String subject,
            int elapsedSeconds,
            boolean completedNaturally) {

        try {

            JSONArray history = new JSONArray(
                    historyPreferences.getString(HISTORY_KEY, "[]")
            );

            JSONObject session = new JSONObject();

            session.put("subject", subject);
            session.put("durationSeconds", elapsedSeconds);

            session.put(
                    "status",
                    completedNaturally ? "Completed" : "Stopped"
            );

            String date = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
            ).format(new Date());

            session.put("date", date);

            JSONArray updatedHistory = new JSONArray();
            updatedHistory.put(session);

            for (int i = 0; i < history.length(); i++) {
                updatedHistory.put(history.getJSONObject(i));
            }

            historyPreferences.edit()
                    .putString(HISTORY_KEY, updatedHistory.toString())
                    .apply();

        } catch (Exception e) {
            showMessage("Could not save study history.");
        }
    }

    private void loadStudyHistory() {

        layoutHistory.removeAllViews();

        try {

            JSONArray history = new JSONArray(
                    historyPreferences.getString(HISTORY_KEY, "[]")
            );

            int count = history.length();
            int totalFocusSeconds = 0;
            int completedCount = 0;

            if (count == 0) {

                tvHistorySummary.setText("No study sessions yet.");

                TextView emptyView = new TextView(requireContext());

                emptyView.setText(
                        "Your finished study sessions will appear here."
                );

                emptyView.setTextColor(0xFFB8BED1);
                emptyView.setTextSize(14);
                emptyView.setPadding(
                        dp(12), dp(12), dp(12), dp(12)
                );

                layoutHistory.addView(emptyView);

                return;
            }

            for (int i = 0; i < count; i++) {

                JSONObject session = history.getJSONObject(i);

                String subject = session.optString(
                        "subject", "Study Session"
                );

                int seconds = session.optInt(
                        "durationSeconds", 0
                );

                String status = session.optString(
                        "status", "Stopped"
                );

                String date = session.optString(
                        "date", "Date unavailable"
                );

                totalFocusSeconds += seconds;

                if ("Completed".equals(status)) {
                    completedCount++;
                }

                TextView sessionView = new TextView(requireContext());

                sessionView.setText(
                        subject
                                + "\nDuration: " + formatDuration(seconds)
                                + "\nStatus: " + status
                                + "\nDate: " + date
                );

                sessionView.setTextColor(0xFFF5F7FF);
                sessionView.setTextSize(14);
                sessionView.setLineSpacing(dp(4), 1.0f);
                sessionView.setPadding(
                        dp(14), dp(14), dp(14), dp(14)
                );

                sessionView.setBackgroundResource(
                        R.drawable.bg_main_card
                );

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                params.bottomMargin = dp(10);

                layoutHistory.addView(sessionView, params);
            }

            tvHistorySummary.setText(
                    "Sessions: " + count
                            + " | Completed: " + completedCount
                            + " | Total focus time: "
                            + formatDuration(totalFocusSeconds)
            );

        } catch (Exception e) {

            tvHistorySummary.setText(
                    "Unable to display study history."
            );
        }
    }

    private String formatDuration(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remaining = seconds % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }

        if (minutes > 0) {
            return minutes + "m " + remaining + "s";
        }

        return remaining + "s";
    }

    // ==========================================
    // GENERAL HELPERS
    // ==========================================

    private int dp(int value) {

        return (int) (
                value * requireContext()
                        .getResources()
                        .getDisplayMetrics()
                        .density
        );
    }

    private void showMessage(String message) {

        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onDestroyView() {

        timerHandler.removeCallbacks(timerRunnable);

        super.onDestroyView();
    }
}
