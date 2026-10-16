package com.anticai.studentassistant.fragments.ai;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.FinishStudySessionRequest;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.StartStudySessionRequest;
import com.anticai.studentassistant.network.StudySessionResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudyPlanFragment extends Fragment {

    private static final String PREFS_NAME = "anticai_saved_plans";
    private static final String PREFS_PLANS_KEY = "plans";

    private ApiService apiService;

    private TextView tvPlanTitle;
    private TextView tvPlanDescription;
    private TextView tvTimer;
    private TextView tvSession1Time;
    private TextView tvSession1Title;
    private TextView tvSession1Description;
    private TextView tvSession2Time;
    private TextView tvSession2Title;
    private TextView tvSession2Description;
    private TextView tvSession3Time;
    private TextView tvSession3Title;
    private TextView tvSession3Description;
    private TextView tvTotalStudyTime;
    private TextView tvCompletedSessions;
    private TextView tvHistoryStatus;

    private Button btnPauseResume;
    private Button btnFinishSession;

    // This view is a TextView in the supplied layout.
    private TextView btnStartPlan;

    private Spinner spStudySubject;
    private Spinner spStudyTopic;
    private Spinner spStudyDuration;
    private TextView tvSelectedPreparation;
    private Button btnStartSelectedPreparation;

    private View layoutStudyHistory;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());

    private long elapsedSeconds = 0;
    private long sessionStartMillis = 0;
    private Long currentSessionId = null;

    private boolean timerRunning = false;
    private boolean sessionStarting = false;
    private boolean sessionFinishing = false;

    private int selectedPlanIndex = -1;

    private JsonArray savedPlans = new JsonArray();
    private final List<JsonObject> sortedPlans = new ArrayList<>();
    private final List<StudySessionResponse> historySessions =
            new ArrayList<>();

    private Runnable timerRunnable;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public StudyPlanFragment() {
        // Required empty public constructor.
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_study_plan,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient
                .getInstance(requireContext())
                .create(ApiService.class);

        bindViews(view);
        setupTimer();
        setupOptionalSelectors();

        loadSavedPlans();
        loadStudyHistory();
    }

    private void bindViews(View view) {
        tvPlanTitle = view.findViewById(R.id.tvPlanTitle);
        tvPlanDescription = view.findViewById(R.id.tvPlanDescription);
        btnStartPlan = view.findViewById(R.id.btnStartPlan);
        tvTimer = view.findViewById(R.id.tvTimer);
        btnPauseResume = view.findViewById(R.id.btnPauseResume);
        btnFinishSession = view.findViewById(R.id.btnFinishSession);

        tvSession1Time = view.findViewById(R.id.tvSession1Time);
        tvSession1Title = view.findViewById(R.id.tvSession1Title);
        tvSession1Description =
                view.findViewById(R.id.tvSession1Description);

        tvSession2Time = view.findViewById(R.id.tvSession2Time);
        tvSession2Title = view.findViewById(R.id.tvSession2Title);
        tvSession2Description =
                view.findViewById(R.id.tvSession2Description);

        tvSession3Time = view.findViewById(R.id.tvSession3Time);
        tvSession3Title = view.findViewById(R.id.tvSession3Title);
        tvSession3Description =
                view.findViewById(R.id.tvSession3Description);

        tvTotalStudyTime = view.findViewById(R.id.tvTotalStudyTime);
        tvCompletedSessions = view.findViewById(R.id.tvCompletedSessions);
        tvHistoryStatus = view.findViewById(R.id.tvHistoryStatus);

        layoutStudyHistory = view.findViewById(R.id.layoutStudyHistory);

        // These views are optional if they exist in your XML.
        spStudySubject = view.findViewById(R.id.spStudySubject);
        spStudyTopic = view.findViewById(R.id.spStudyTopic);
        spStudyDuration = view.findViewById(R.id.spStudyDuration);
        tvSelectedPreparation =
                view.findViewById(R.id.tvSelectedPreparation);
        btnStartSelectedPreparation =
                view.findViewById(R.id.btnStartSelectedPreparation);

        btnStartPlan.setOnClickListener(v -> startSelectedPlan());

        btnPauseResume.setOnClickListener(v -> togglePauseResume());

        btnFinishSession.setOnClickListener(v -> finishCurrentSession());

        if (btnStartSelectedPreparation != null) {
            btnStartSelectedPreparation.setOnClickListener(
                    v -> startSelectedPlan()
            );
        }

        if (tvSession1Title != null) {
            tvSession1Title.setOnClickListener(
                    v -> selectPlanAt(0)
            );
        }

        if (tvSession2Title != null) {
            tvSession2Title.setOnClickListener(
                    v -> selectPlanAt(1)
            );
        }

        if (tvSession3Title != null) {
            tvSession3Title.setOnClickListener(
                    v -> selectPlanAt(2)
            );
        }
    }

    private void setupTimer() {
        updateTimerText();

        btnPauseResume.setEnabled(false);
        btnFinishSession.setEnabled(false);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || !timerRunning) {
                    return;
                }

                elapsedSeconds =
                        Math.max(0, (System.currentTimeMillis()
                                - sessionStartMillis) / 1000);

                updateTimerText();
                timerHandler.postDelayed(this, 1000);
            }
        };
    }

    private void setupOptionalSelectors() {
        if (spStudySubject != null) {
            spStudySubject.setOnItemSelectedListener(
                    new SimpleItemSelectedListener(position -> {
                        if (position >= 0 && position < sortedPlans.size()) {
                            selectPlanAt(position);
                        }
                    })
            );
        }

        if (spStudyTopic != null) {
            spStudyTopic.setOnItemSelectedListener(
                    new SimpleItemSelectedListener(position -> {
                        if (position >= 0 && position < sortedPlans.size()) {
                            selectPlanAt(position);
                        }
                    })
            );
        }

        if (spStudyDuration != null) {
            spStudyDuration.setOnItemSelectedListener(
                    new SimpleItemSelectedListener(position -> {
                        if (position >= 0 && position < sortedPlans.size()) {
                            selectPlanAt(position);
                        }
                    })
            );
        }
    }

    private void loadSavedPlans() {
        if (!isAdded()) {
            return;
        }

        SharedPreferences preferences = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String json = preferences.getString(PREFS_PLANS_KEY, "[]");

        try {
            savedPlans = JsonParser.parseString(json).getAsJsonArray();
        } catch (Exception e) {
            savedPlans = new JsonArray();
        }

        sortedPlans.clear();

        for (int i = 0; i < savedPlans.size(); i++) {
            if (savedPlans.get(i).isJsonObject()) {
                sortedPlans.add(savedPlans.get(i).getAsJsonObject());
            }
        }

        Collections.sort(sortedPlans, new Comparator<JsonObject>() {
            @Override
            public int compare(JsonObject first, JsonObject second) {
                boolean firstCompleted = getBoolean(first, "completed");
                boolean secondCompleted = getBoolean(second, "completed");

                if (firstCompleted != secondCompleted) {
                    return firstCompleted ? 1 : -1;
                }

                String firstDate = getString(first, "examDate");
                String secondDate = getString(second, "examDate");

                if (firstDate.isEmpty() && secondDate.isEmpty()) {
                    return 0;
                }

                if (firstDate.isEmpty()) {
                    return 1;
                }

                if (secondDate.isEmpty()) {
                    return -1;
                }

                return firstDate.compareTo(secondDate);
            }
        });

        populateOptionalSelectors();
        displaySavedPlans();
    }

    private void populateOptionalSelectors() {
        List<String> subjects = new ArrayList<>();
        List<String> topics = new ArrayList<>();
        List<String> durations = new ArrayList<>();

        for (JsonObject plan : sortedPlans) {
            String subject = getString(plan, "subject");
            String topic = getString(plan, "topic");
            String duration = getString(plan, "durationMinutes");

            if (subject.isEmpty()) {
                subject = "Untitled subject";
            }

            if (topic.isEmpty()) {
                topic = subject;
            }

            if (duration.isEmpty() || "0".equals(duration)) {
                duration = "25";
            }

            subjects.add(subject);
            topics.add(topic);
            durations.add(duration + " minutes");
        }

        if (spStudySubject != null) {
            setSpinnerItems(spStudySubject, subjects);
        }

        if (spStudyTopic != null) {
            setSpinnerItems(spStudyTopic, topics);
        }

        if (spStudyDuration != null) {
            setSpinnerItems(spStudyDuration, durations);
        }
    }

    private void setSpinnerItems(
            Spinner spinner,
            List<String> values
    ) {
        if (spinner == null || !isAdded()) {
            return;
        }

        if (values.isEmpty()) {
            values.add("No saved plans");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                values
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
    }

    private void displaySavedPlans() {
        if (!isAdded()) {
            return;
        }

        if (sortedPlans.isEmpty()) {
            selectedPlanIndex = -1;

            tvPlanTitle.setText("No study plans yet");
            tvPlanDescription.setText(
                    "Create a study plan to see it here."
            );

            btnStartPlan.setText("No plans available");
            btnStartPlan.setEnabled(false);

            setSessionRow(
                    tvSession1Time,
                    tvSession1Title,
                    tvSession1Description,
                    "",
                    "No saved plans",
                    "Create a plan to get started."
            );

            setSessionRow(
                    tvSession2Time,
                    tvSession2Title,
                    tvSession2Description,
                    "",
                    "",
                    ""
            );

            setSessionRow(
                    tvSession3Time,
                    tvSession3Title,
                    tvSession3Description,
                    "",
                    "",
                    ""
            );

            if (tvSelectedPreparation != null) {
                tvSelectedPreparation.setText("No plan selected");
            }

            if (btnStartSelectedPreparation != null) {
                btnStartSelectedPreparation.setEnabled(false);
            }

            return;
        }

        if (selectedPlanIndex < 0
                || selectedPlanIndex >= sortedPlans.size()) {
            selectedPlanIndex = findNextUncompletedPlan();

            if (selectedPlanIndex < 0) {
                selectedPlanIndex = 0;
            }
        }

        showSelectedPlan();
        updatePlanRows();
    }

    private int findNextUncompletedPlan() {
        for (int i = 0; i < sortedPlans.size(); i++) {
            if (!getBoolean(sortedPlans.get(i), "completed")) {
                return i;
            }
        }

        return -1;
    }

    private void showSelectedPlan() {
        if (selectedPlanIndex < 0
                || selectedPlanIndex >= sortedPlans.size()) {
            return;
        }

        JsonObject plan = sortedPlans.get(selectedPlanIndex);

        String subject = getString(plan, "subject");
        String topic = getString(plan, "topic");
        String examDate = getString(plan, "examDate");
        String duration = getString(plan, "durationMinutes");
        boolean completed = getBoolean(plan, "completed");

        if (subject.isEmpty()) {
            subject = "Untitled subject";
        }

        if (topic.isEmpty()) {
            topic = subject;
        }

        if (duration.isEmpty() || "0".equals(duration)) {
            duration = "25";
        }

        tvPlanTitle.setText(subject);

        StringBuilder description = new StringBuilder();
        description.append("Topic: ").append(topic);
        description.append("\nDuration: ").append(duration).append(" minutes");

        if (!examDate.isEmpty()) {
            description.append("\nExam date: ").append(examDate);
        }

        if (completed) {
            description.append("\nStatus: Completed");
            btnStartPlan.setText("Plan completed");
            btnStartPlan.setEnabled(false);
        } else {
            description.append("\nStatus: Not completed");
            btnStartPlan.setText("Start this plan");
            btnStartPlan.setEnabled(!timerRunning
                    && !sessionStarting
                    && !sessionFinishing);
        }

        tvPlanDescription.setText(description.toString());

        if (tvSelectedPreparation != null) {
            tvSelectedPreparation.setText(
                    subject + " — " + topic + " (" + duration + " min)"
            );
        }

        if (btnStartSelectedPreparation != null) {
            btnStartSelectedPreparation.setEnabled(
                    !completed
                            && !timerRunning
                            && !sessionStarting
                            && !sessionFinishing
            );
        }

        syncOptionalSpinners();
    }

    private void syncOptionalSpinners() {
        if (selectedPlanIndex < 0
                || selectedPlanIndex >= sortedPlans.size()) {
            return;
        }

        if (spStudySubject != null
                && spStudySubject.getCount() > selectedPlanIndex) {
            spStudySubject.setSelection(selectedPlanIndex);
        }

        if (spStudyTopic != null
                && spStudyTopic.getCount() > selectedPlanIndex) {
            spStudyTopic.setSelection(selectedPlanIndex);
        }

        if (spStudyDuration != null
                && spStudyDuration.getCount() > selectedPlanIndex) {
            spStudyDuration.setSelection(selectedPlanIndex);
        }
    }

    private void updatePlanRows() {
        for (int i = 0; i < 3; i++) {
            TextView time;
            TextView title;
            TextView description;

            if (i == 0) {
                time = tvSession1Time;
                title = tvSession1Title;
                description = tvSession1Description;
            } else if (i == 1) {
                time = tvSession2Time;
                title = tvSession2Title;
                description = tvSession2Description;
            } else {
                time = tvSession3Time;
                title = tvSession3Title;
                description = tvSession3Description;
            }

            if (i >= sortedPlans.size()) {
                setSessionRow(time, title, description, "", "", "");
                continue;
            }

            JsonObject plan = sortedPlans.get(i);
            String subject = getString(plan, "subject");
            String topic = getString(plan, "topic");
            String examDate = getString(plan, "examDate");
            String duration = getString(plan, "durationMinutes");

            if (subject.isEmpty()) {
                subject = "Untitled subject";
            }

            if (topic.isEmpty()) {
                topic = subject;
            }

            String status = getBoolean(plan, "completed")
                    ? "Completed"
                    : "Upcoming";

            if (i == selectedPlanIndex) {
                status += " • Selected";
            }

            setSessionRow(
                    time,
                    title,
                    description,
                    examDate.isEmpty() ? duration + " min" : examDate,
                    subject,
                    topic + " • " + status
            );
        }
    }

    private void setSessionRow(
            TextView time,
            TextView title,
            TextView description,
            String timeText,
            String titleText,
            String descriptionText
    ) {
        if (time != null) {
            time.setText(timeText);
        }

        if (title != null) {
            title.setText(titleText);
        }

        if (description != null) {
            description.setText(descriptionText);
        }
    }

    private void selectPlanAt(int index) {
        if (index < 0 || index >= sortedPlans.size()) {
            return;
        }

        if (timerRunning || sessionStarting || sessionFinishing) {
            showToast("Finish the current study session first.");
            return;
        }

        selectedPlanIndex = index;
        showSelectedPlan();
        updatePlanRows();
    }

    private void startSelectedPlan() {
        if (!isAdded()) {
            return;
        }

        if (timerRunning || sessionStarting || sessionFinishing) {
            showToast("A study session is already active.");
            return;
        }

        if (selectedPlanIndex < 0
                || selectedPlanIndex >= sortedPlans.size()) {
            showToast("Select a study plan first.");
            return;
        }

        JsonObject selectedPlan = sortedPlans.get(selectedPlanIndex);

        if (getBoolean(selectedPlan, "completed")) {
            showToast("This plan is already completed.");
            return;
        }

        String subject = getString(selectedPlan, "subject");

        if (subject.trim().isEmpty()) {
            subject = "General Study";
        }

        sessionStarting = true;
        updateActionButtons();

        apiService.startStudySession(
                new StartStudySessionRequest(subject)
        ).enqueue(new Callback<StudySessionResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Response<StudySessionResponse> response
            ) {
                if (!isAdded()) {
                    return;
                }

                sessionStarting = false;

                StudySessionResponse session = response.body();

                if (response.isSuccessful()
                        && session != null
                        && session.getId() != null) {

                    currentSessionId = session.getId();
                    elapsedSeconds = 0;
                    sessionStartMillis = System.currentTimeMillis();
                    timerRunning = true;

                    timerHandler.removeCallbacks(timerRunnable);
                    timerHandler.post(timerRunnable);

                    updateTimerText();
                    updateActionButtons();

                    showToast("Study session started: " + subject);
                } else {
                    updateActionButtons();
                    showToast(
                            "Could not start session. HTTP "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                sessionStarting = false;
                updateActionButtons();

                showToast("Network error starting session: "
                        + t.getMessage());
            }
        });
    }

    private void togglePauseResume() {
        if (currentSessionId == null || sessionFinishing) {
            return;
        }

        if (timerRunning) {
            elapsedSeconds = Math.max(
                    0,
                    (System.currentTimeMillis() - sessionStartMillis) / 1000
            );

            timerRunning = false;
            timerHandler.removeCallbacks(timerRunnable);
            btnPauseResume.setText("Resume");
            updateTimerText();
        } else {
            sessionStartMillis =
                    System.currentTimeMillis() - elapsedSeconds * 1000;

            timerRunning = true;
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.post(timerRunnable);
            btnPauseResume.setText("Pause");
        }

        updateActionButtons();
    }

    private void finishCurrentSession() {
        if (!isAdded()) {
            return;
        }

        if (currentSessionId == null) {
            showToast("Start a study session first.");
            return;
        }

        if (sessionFinishing) {
            return;
        }

        timerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);

        elapsedSeconds = Math.max(
                elapsedSeconds,
                Math.max(
                        0,
                        (System.currentTimeMillis() - sessionStartMillis) / 1000
                )
        );

        // The backend accepts integer minutes. Send at least one minute.
        int durationMinutes = (int) Math.max(
                1,
                (elapsedSeconds + 59) / 60
        );

        final long sessionIdToFinish = currentSessionId;

        sessionFinishing = true;
        updateActionButtons();

        apiService.finishStudySession(
                sessionIdToFinish,
                new FinishStudySessionRequest(durationMinutes)
        ).enqueue(new Callback<StudySessionResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Response<StudySessionResponse> response
            ) {
                if (!isAdded()) {
                    return;
                }

                sessionFinishing = false;

                if (response.isSuccessful()) {
                    markSelectedPlanCompleted();

                    currentSessionId = null;
                    timerRunning = false;
                    elapsedSeconds = 0;
                    sessionStartMillis = 0;

                    timerHandler.removeCallbacks(timerRunnable);
                    updateTimerText();
                    updateActionButtons();

                    showToast("Study session completed!");
                    loadSavedPlans();
                    loadStudyHistory();
                } else {
                    // Keep the session ID so the user can retry finishing it.
                    updateActionButtons();
                    showToast(
                            "Could not finish session. HTTP "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Throwable t
            ) {
                if (!isAdded()) {
                    return;
                }

                sessionFinishing = false;
                updateActionButtons();

                showToast("Network error finishing session: "
                        + t.getMessage());
            }
        });
    }

    private void markSelectedPlanCompleted() {
        if (selectedPlanIndex < 0
                || selectedPlanIndex >= sortedPlans.size()) {
            return;
        }

        JsonObject selectedPlan = sortedPlans.get(selectedPlanIndex);
        String selectedId = getString(selectedPlan, "id");

        // Update the matching plan in the original saved array.
        for (int i = 0; i < savedPlans.size(); i++) {
            if (!savedPlans.get(i).isJsonObject()) {
                continue;
            }

            JsonObject plan = savedPlans.get(i).getAsJsonObject();

            if (getString(plan, "id").equals(selectedId)) {
                plan.addProperty("completed", true);
                break;
            }
        }

        savePlansToPreferences();
    }

    private void savePlansToPreferences() {
        if (!isAdded()) {
            return;
        }

        SharedPreferences preferences = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(PREFS_PLANS_KEY, savedPlans.toString())
                .apply();
    }

    private void updateActionButtons() {
        if (!isAdded()) {
            return;
        }

        boolean hasActiveSession = currentSessionId != null;

        btnPauseResume.setEnabled(
                hasActiveSession && !sessionFinishing
        );

        btnFinishSession.setEnabled(
                hasActiveSession && !sessionFinishing
        );

        if (hasActiveSession) {
            btnPauseResume.setText(timerRunning ? "Pause" : "Resume");
        } else {
            btnPauseResume.setText("Pause");
        }

        boolean selectedPlanCompleted =
                selectedPlanIndex >= 0
                        && selectedPlanIndex < sortedPlans.size()
                        && getBoolean(
                        sortedPlans.get(selectedPlanIndex),
                        "completed"
                );

        btnStartPlan.setEnabled(
                !hasActiveSession
                        && !sessionStarting
                        && !sessionFinishing
                        && selectedPlanIndex >= 0
                        && !selectedPlanCompleted
        );

        if (btnStartSelectedPreparation != null) {
            btnStartSelectedPreparation.setEnabled(
                    !hasActiveSession
                            && !sessionStarting
                            && !sessionFinishing
                            && selectedPlanIndex >= 0
                            && !selectedPlanCompleted
            );
        }
    }

    private void updateTimerText() {
        if (tvTimer == null) {
            return;
        }

        long totalSeconds = Math.max(0, elapsedSeconds);

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        tvTimer.setText(String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
        ));
    }

    private void loadStudyHistory() {
        if (!isAdded()) {
            return;
        }

        apiService.getStudySessions().enqueue(
                new Callback<List<StudySessionResponse>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<StudySessionResponse>> call,
                            @NonNull Response<List<StudySessionResponse>> response
                    ) {
                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null) {
                            historySessions.clear();
                            historySessions.addAll(response.body());
                            displayStudyHistory();
                        } else if (tvHistoryStatus != null) {
                            tvHistoryStatus.setText(
                                    "Unable to load study history."
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<StudySessionResponse>> call,
                            @NonNull Throwable t
                    ) {
                        if (!isAdded()) {
                            return;
                        }

                        if (tvHistoryStatus != null) {
                            tvHistoryStatus.setText(
                                    "Study history unavailable. Check connection."
                            );
                        }
                    }
                }
        );
    }

    private void displayStudyHistory() {
        long totalMinutes = 0;
        int completedCount = 0;

        for (StudySessionResponse session : historySessions) {
            if (session.getDurationMinutes() != null) {
                totalMinutes += session.getDurationMinutes();
            }

            if (session.getEndTime() != null
                    && !session.getEndTime().trim().isEmpty()) {
                completedCount++;
            }
        }

        if (tvTotalStudyTime != null) {
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;

            tvTotalStudyTime.setText(
                    hours + "h " + minutes + "m"
            );
        }

        if (tvCompletedSessions != null) {
            tvCompletedSessions.setText(
                    String.valueOf(completedCount)
            );
        }

        if (tvHistoryStatus != null) {
            if (historySessions.isEmpty()) {
                tvHistoryStatus.setText("No study history yet.");
            } else {
                tvHistoryStatus.setText(
                        "Completed sessions: " + completedCount
                                + " • Total recorded time: "
                                + totalMinutes + " minutes"
                );
            }
        }

        if (layoutStudyHistory != null) {
            layoutStudyHistory.setVisibility(View.VISIBLE);
        }
    }

    private String getString(JsonObject object, String key) {
        if (object == null
                || !object.has(key)
                || object.get(key).isJsonNull()) {
            return "";
        }

        try {
            return object.get(key).getAsString();
        } catch (Exception e) {
            return "";
        }
    }

    private boolean getBoolean(JsonObject object, String key) {
        if (object == null
                || !object.has(key)
                || object.get(key).isJsonNull()) {
            return false;
        }

        try {
            return object.get(key).getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(
                    requireContext(),
                    message,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh local plans in case the Create Plan screen added some.
        loadSavedPlans();
        loadStudyHistory();
    }

    @Override
    public void onDestroyView() {
        timerHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    /**
     * Small listener adapter so optional spinners can be wired without
     * duplicating listener code.
     */
    private static class SimpleItemSelectedListener
            implements android.widget.AdapterView.OnItemSelectedListener {

        interface SelectionCallback {
            void onSelected(int position);
        }

        private final SelectionCallback callback;

        SimpleItemSelectedListener(SelectionCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onItemSelected(
                android.widget.AdapterView<?> parent,
                View view,
                int position,
                long id
        ) {
            callback.onSelected(position);
        }

        @Override
        public void onNothingSelected(
                android.widget.AdapterView<?> parent
        ) {
            // Nothing to do.
        }
    }
}