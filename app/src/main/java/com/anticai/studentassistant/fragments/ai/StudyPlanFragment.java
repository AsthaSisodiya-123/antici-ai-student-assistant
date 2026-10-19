package com.anticai.studentassistant.fragments.ai;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.anticai.studentassistant.network.FinishStudySessionRequest;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.StartStudySessionRequest;
import com.anticai.studentassistant.network.StudySessionResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudyPlanFragment extends Fragment {

    private static final String PREFS_NAME = "anticai_saved_plans";
    private static final String PREFS_PLANS_KEY = "plans";
    private static final int DAILY_GOAL_MINUTES = 120;
    private static final int DEFAULT_FOCUS_SECONDS = 25 * 60;

    private ApiService api;

    private TextView tvActivePlans;
    private TextView tvStudyTimeToday;
    private TextView tvDailyGoalProgress;
    private ProgressBar progressDailyGoal;

    private EditText etPlanSubject;
    private EditText etPlanTopic;
    private EditText etPlanExamDate;
    private EditText etPlanMinutes;
    private Spinner spPlanDifficulty;
    private TextView btnCreatePlan;
    private ProgressBar progressCreatePlan;
    private TextView tvPlanStatus;

    private TextView btnViewAllPlans;
    private LinearLayout layoutPlans;
    private TextView tvPlansEmpty;

    private TextView tvFocusSubject;
    private TextView tvFocusTimer;
    private ProgressBar progressFocusTimer;
    private TextView btnStartSession;
    private TextView btnPauseSession;
    private TextView btnFinishSession;

    private TextView btnViewHistory;
    private TextView tvHistorySummary;
    private LinearLayout layoutHistory;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final List<JsonObject> plans = new ArrayList<>();
    private final List<JsonObject> history = new ArrayList<>();

    private Long currentSessionId;
    private String currentSubject = "";
    private int currentPlanIndex = -1;

    private long elapsedSeconds;
    private long lastTickMillis;
    private int focusDurationSeconds = DEFAULT_FOCUS_SECONDS;

    private boolean timerRunning;
    private boolean sessionStarting;
    private boolean sessionFinishing;
    private boolean showingAllPlans;
    private boolean showingHistory;

    private Runnable timerRunnable;

    public StudyPlanFragment() {
        // Required empty constructor.
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

        api = RetrofitClient.getInstance(requireContext())
                .create(ApiService.class);

        bindViews(view);
        setupDifficultySpinner();
        setupDatePicker();
        setupTimer();

        loadSavedPlans();
        loadActivePlansFromServer();
        loadStudyHistory();

        updateDashboard();
        updateTimerDisplay();
        updateButtons();
    }

    private void bindViews(@NonNull View view) {
        tvActivePlans = view.findViewById(R.id.tvActivePlans);
        tvStudyTimeToday = view.findViewById(R.id.tvStudyTimeToday);
        tvDailyGoalProgress = view.findViewById(R.id.tvDailyGoalProgress);
        progressDailyGoal = view.findViewById(R.id.progressDailyGoal);

        etPlanSubject = view.findViewById(R.id.etPlanSubject);
        etPlanTopic = view.findViewById(R.id.etPlanTopic);
        etPlanExamDate = view.findViewById(R.id.etPlanExamDate);
        etPlanMinutes = view.findViewById(R.id.etPlanMinutes);
        spPlanDifficulty = view.findViewById(R.id.spPlanDifficulty);
        btnCreatePlan = view.findViewById(R.id.btnCreatePlan);
        progressCreatePlan = view.findViewById(R.id.progressCreatePlan);
        tvPlanStatus = view.findViewById(R.id.tvPlanStatus);

        btnViewAllPlans = view.findViewById(R.id.btnViewAllPlans);
        layoutPlans = view.findViewById(R.id.layoutPlans);
        tvPlansEmpty = view.findViewById(R.id.tvPlansEmpty);

        tvFocusSubject = view.findViewById(R.id.tvFocusSubject);
        tvFocusTimer = view.findViewById(R.id.tvFocusTimer);
        progressFocusTimer = view.findViewById(R.id.progressFocusTimer);
        btnStartSession = view.findViewById(R.id.btnStartSession);
        btnPauseSession = view.findViewById(R.id.btnPauseSession);
        btnFinishSession = view.findViewById(R.id.btnFinishSession);

        btnViewHistory = view.findViewById(R.id.btnViewHistory);
        tvHistorySummary = view.findViewById(R.id.tvHistorySummary);
        layoutHistory = view.findViewById(R.id.layoutHistory);

        btnCreatePlan.setOnClickListener(v -> createPlan());

        btnViewAllPlans.setOnClickListener(v -> {
            showingAllPlans = !showingAllPlans;
            renderPlans();
        });

        btnStartSession.setOnClickListener(v -> startSession());
        btnPauseSession.setOnClickListener(v -> togglePause());
        btnFinishSession.setOnClickListener(v -> finishSession());

        btnViewHistory.setOnClickListener(v -> {
            showingHistory = !showingHistory;
            renderHistory();
        });
    }

    private void setupDifficultySpinner() {
        if (spPlanDifficulty == null || !isAdded()) return;

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.plan_difficulties,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spPlanDifficulty.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etPlanExamDate.setFocusable(false);
        etPlanExamDate.setClickable(true);

        etPlanExamDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (picker, year, month, day) -> {
                        etPlanExamDate.setText(String.format(
                                Locale.US,
                                "%04d-%02d-%02d",
                                year,
                                month + 1,
                                day
                        ));
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

    private void setupTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || !timerRunning) return;

                long now = System.currentTimeMillis();

                elapsedSeconds += Math.max(
                        0L,
                        (now - lastTickMillis) / 1000L
                );

                lastTickMillis = now;

                updateTimerDisplay();
                handler.postDelayed(this, 1000L);
            }
        };
    }

    // ---------------------------------------------------------
    // PLAN CREATION
    // ---------------------------------------------------------

    private void createPlan() {
        if (!isAdded()) return;

        String subject = etPlanSubject.getText().toString().trim();
        String topic = etPlanTopic.getText().toString().trim();
        String examDate = etPlanExamDate.getText().toString().trim();
        String minutesText = etPlanMinutes.getText().toString().trim();

        if (subject.isEmpty()) {
            etPlanSubject.setError("Enter a subject");
            etPlanSubject.requestFocus();
            return;
        }

        if (topic.isEmpty()) {
            etPlanTopic.setError("Enter a topic");
            etPlanTopic.requestFocus();
            return;
        }

        if (examDate.isEmpty()) {
            etPlanExamDate.setError("Select an exam date");
            return;
        }

        try {
            LocalDate date = LocalDate.parse(examDate);

            if (date.isBefore(LocalDate.now())) {
                etPlanExamDate.setError("Date cannot be in the past");
                return;
            }
        } catch (DateTimeParseException e) {
            etPlanExamDate.setError("Choose a valid date");
            return;
        }

        int minutes;

        try {
            minutes = Integer.parseInt(minutesText);
        } catch (NumberFormatException e) {
            etPlanMinutes.setError("Enter study time in minutes");
            return;
        }

        if (minutes < 5 || minutes > 1440) {
            etPlanMinutes.setError("Enter between 5 and 1440 minutes");
            return;
        }

        String difficulty = spPlanDifficulty.getSelectedItem() == null
                ? "Medium"
                : spPlanDifficulty.getSelectedItem().toString();

        JsonObject request = new JsonObject();
        request.addProperty("subject", subject);
        request.addProperty("topic", topic);
        request.addProperty("difficulty", difficulty);
        request.addProperty("examDate", examDate);
        request.addProperty("targetMinutes", minutes);
        request.addProperty("dailyStudyMinutes", minutes);

        setCreateLoading(true);
        setPlanStatus("Generating your study plan…", false);

        api.generateStudyPlan(request).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ResponseBody> call,
                            @NonNull Response<ResponseBody> response
                    ) {
                        if (!isAdded()) {
                            closeBody(response.body());
                            closeBody(response.errorBody());
                            return;
                        }

                        setCreateLoading(false);

                        if (!response.isSuccessful()) {
                            closeBody(response.errorBody());

                            setPlanStatus(
                                    "Plan generation failed (HTTP "
                                            + response.code() + ").",
                                    true
                            );
                            return;
                        }

                        String responseJson = readBody(response.body());

                        JsonObject saved = makePlan(
                                subject,
                                topic,
                                difficulty,
                                examDate,
                                minutes
                        );

                        if (!responseJson.isEmpty()) {
                            try {
                                JsonElement element =
                                        JsonParser.parseString(responseJson);

                                JsonObject serverPlan =
                                        findPlanObject(element);

                                if (serverPlan != null) {
                                    for (String key : serverPlan.keySet()) {
                                        saved.add(key, serverPlan.get(key));
                                    }
                                }
                            } catch (Exception ignored) {
                                // Ignore a non-JSON response.
                            }
                        }

                        upsertPlan(saved);
                        savePlans();
                        hideKeyboard();
                        clearPlanForm();
                        renderPlans();
                        updateDashboard();

                        setPlanStatus(
                                "Your study plan is ready.",
                                false
                        );

                        showToast("Study plan created successfully.");

                        // Refresh backend plans after creating a plan.
                        loadActivePlansFromServer();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ResponseBody> call,
                            @NonNull Throwable t
                    ) {
                        if (!isAdded()) return;

                        setCreateLoading(false);

                        setPlanStatus(
                                "Could not connect to the server. "
                                        + safeMessage(t),
                                true
                        );
                    }
                }
        );
    }

    private JsonObject makePlan(
            String subject,
            String topic,
            String difficulty,
            String examDate,
            int minutes
    ) {
        JsonObject plan = new JsonObject();

        plan.addProperty("id", "local_" + System.currentTimeMillis());
        plan.addProperty("subject", subject);
        plan.addProperty("topic", topic);
        plan.addProperty("difficulty", difficulty);
        plan.addProperty("examDate", examDate);
        plan.addProperty("targetMinutes", minutes);
        plan.addProperty("durationMinutes", minutes);
        plan.addProperty("completedMinutes", 0);
        plan.addProperty("completed", false);
        plan.addProperty("status", "ACTIVE");
        plan.addProperty("createdAt", System.currentTimeMillis());

        return plan;
    }

    private JsonObject findPlanObject(JsonElement element) {
        if (element == null || element.isJsonNull()) return null;

        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();

            if (object.has("subject") || object.has("topic")) {
                return object;
            }

            String[] possibleKeys = {
                    "plan", "data", "studyPlan", "result"
            };

            for (String key : possibleKeys) {
                if (object.has(key)) {
                    JsonObject found = findPlanObject(object.get(key));

                    if (found != null) return found;
                }
            }
        }

        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();

            for (JsonElement item : array) {
                JsonObject found = findPlanObject(item);

                if (found != null) return found;
            }
        }

        return null;
    }

    private void upsertPlan(JsonObject newPlan) {
        String id = string(newPlan, "id");

        for (int i = 0; i < plans.size(); i++) {
            if (!id.isEmpty()
                    && id.equals(string(plans.get(i), "id"))) {
                plans.set(i, newPlan);
                return;
            }
        }

        plans.add(0, newPlan);
    }

    private void clearPlanForm() {
        etPlanSubject.setText("");
        etPlanTopic.setText("");
        etPlanExamDate.setText("");
        etPlanMinutes.setText("");
        spPlanDifficulty.setSelection(0);
    }

    private void setCreateLoading(boolean loading) {
        if (btnCreatePlan == null) return;

        btnCreatePlan.setEnabled(!loading);
        btnCreatePlan.setAlpha(loading ? 0.65f : 1f);

        btnCreatePlan.setText(
                loading ? "GENERATING PLAN…" : "GENERATE STUDY PLAN  →"
        );

        progressCreatePlan.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );
    }

    private void setPlanStatus(String message, boolean error) {
        tvPlanStatus.setText(message);

        tvPlanStatus.setTextColor(
                Color.parseColor(error ? "#FF8585" : "#4D8DFF")
        );

        tvPlanStatus.setVisibility(View.VISIBLE);
    }

    // ---------------------------------------------------------
    // LOCAL PLAN STORAGE AND BACKEND LOADING
    // ---------------------------------------------------------

    private void loadSavedPlans() {
        if (!isAdded()) return;

        SharedPreferences preferences = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String json = preferences.getString(PREFS_PLANS_KEY, "[]");

        plans.clear();

        try {
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement element : array) {
                if (element.isJsonObject()) {
                    plans.add(element.getAsJsonObject());
                }
            }
        } catch (Exception ignored) {
            plans.clear();
        }

        renderPlans();
    }

    private void loadActivePlansFromServer() {
        if (!isAdded() || api == null) return;

        api.getActiveStudyPlans().enqueue(
                new Callback<List<JsonObject>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<JsonObject>> call,
                            @NonNull Response<List<JsonObject>> response
                    ) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            return;
                        }

                        for (JsonObject serverPlan : response.body()) {
                            if (serverPlan == null) continue;

                            JsonObject plan = serverPlan.deepCopy();

                            if (!plan.has("completed")
                                    || plan.get("completed").isJsonNull()) {
                                plan.addProperty(
                                        "completed",
                                        "COMPLETED".equalsIgnoreCase(
                                                string(plan, "status")
                                        )
                                );
                            }

                            if (!plan.has("targetMinutes")
                                    || plan.get("targetMinutes").isJsonNull()) {
                                plan.addProperty(
                                        "targetMinutes",
                                        number(plan, "durationMinutes", 25)
                                );
                            }

                            // Merge backend plans with local plans by ID.
                            upsertPlan(plan);
                        }

                        savePlans();
                        renderPlans();
                        updateDashboard();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<JsonObject>> call,
                            @NonNull Throwable t
                    ) {
                        // Keep locally saved plans visible if the server
                        // cannot be reached.
                    }
                }
        );
    }

    private void savePlans() {
        if (!isAdded()) return;

        JsonArray array = new JsonArray();

        for (JsonObject plan : plans) {
            array.add(plan);
        }

        requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFS_PLANS_KEY, array.toString())
                .apply();
    }

    private void renderPlans() {
        if (layoutPlans == null || !isAdded()) return;

        layoutPlans.removeAllViews();

        tvPlansEmpty.setVisibility(
                plans.isEmpty() ? View.VISIBLE : View.GONE
        );

        int visibleCount = showingAllPlans
                ? plans.size()
                : Math.min(plans.size(), 3);

        for (int i = 0; i < visibleCount; i++) {
            final int index = i;
            JsonObject plan = plans.get(i);

            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(17), dp(16), dp(17), dp(16));
            card.setBackgroundResource(R.drawable.bg_main_card);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            cardParams.bottomMargin = dp(9);
            card.setLayoutParams(cardParams);

            TextView title = makeText(
                    string(plan, "subject") + " — " + string(plan, "topic"),
                    14,
                    "#F5F7FF",
                    true
            );

            card.addView(title);

            String details = number(
                    plan,
                    "targetMinutes",
                    number(plan, "durationMinutes", 25)
            ) + " min"
                    + "  ·  "
                    + stringOr(plan, "difficulty", "Medium")
                    + " difficulty";

            TextView subtitle = makeText(
                    details,
                    10,
                    "#858DA5",
                    false
            );

            LinearLayout.LayoutParams subtitleParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            subtitleParams.topMargin = dp(7);
            subtitle.setLayoutParams(subtitleParams);
            card.addView(subtitle);

            int target = Math.max(
                    1,
                    number(
                            plan,
                            "targetMinutes",
                            number(plan, "durationMinutes", 25)
                    )
            );

            int completed = Math.max(
                    0,
                    number(plan, "completedMinutes", 0)
            );

            int progress = isCompleted(plan)
                    ? 100
                    : Math.min(100, completed * 100 / target);

            ProgressBar bar = new ProgressBar(
                    requireContext(),
                    null,
                    android.R.attr.progressBarStyleHorizontal
            );

            bar.setMax(100);
            bar.setProgress(progress);

            bar.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#4D8DFF")
                    )
            );

            bar.setProgressBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#252B3A")
                    )
            );

            LinearLayout.LayoutParams barParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dp(4)
                    );

            barParams.topMargin = dp(13);
            bar.setLayoutParams(barParams);
            card.addView(bar);

            TextView status = makeText(
                    (isCompleted(plan) ? "COMPLETED" : "ACTIVE")
                            + "  ·  Exam: "
                            + stringOr(plan, "examDate", "Not set"),
                    10,
                    isCompleted(plan) ? "#61DDAA" : "#858DA5",
                    true
            );

            LinearLayout.LayoutParams statusParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            statusParams.topMargin = dp(9);
            status.setLayoutParams(statusParams);
            card.addView(status);

            TextView action = makeText(
                    isCompleted(plan)
                            ? "COMPLETED"
                            : "STUDY THIS PLAN  →",
                    10,
                    "#4D8DFF",
                    true
            );

            action.setGravity(android.view.Gravity.CENTER);
            action.setPadding(0, dp(12), 0, dp(4));

            action.setOnClickListener(v -> {
                if (isCompleted(plan)) {
                    showToast("This plan is already completed.");
                    return;
                }

                currentPlanIndex = index;
                currentSubject = string(plan, "subject");

                tvFocusSubject.setText(
                        (currentSubject + " — " + string(plan, "topic"))
                                .toUpperCase(Locale.getDefault())
                );

                focusDurationSeconds = Math.max(
                        60,
                        number(
                                plan,
                                "targetMinutes",
                                number(plan, "durationMinutes", 25)
                        ) * 60
                );

                elapsedSeconds = 0;
                updateTimerDisplay();
                startSession();
            });

            card.addView(action);
            layoutPlans.addView(card);
        }

        btnViewAllPlans.setText(
                showingAllPlans ? "SHOW LESS  ↑" : "VIEW ALL  →"
        );

        updateDashboard();
    }

    // ---------------------------------------------------------
    // FOCUS TIMER AND BACKEND SESSIONS
    // ---------------------------------------------------------

    private void startSession() {
        if (!isAdded() || sessionStarting || sessionFinishing) return;

        if (currentSessionId != null) {
            showToast("A study session is already active.");
            return;
        }

        if (currentSubject.trim().isEmpty()) {
            if (currentPlanIndex >= 0 && currentPlanIndex < plans.size()) {
                currentSubject = string(
                        plans.get(currentPlanIndex),
                        "subject"
                );
            } else {
                showToast("Choose a study plan first.");
                return;
            }
        }

        final String subject = currentSubject;

        sessionStarting = true;
        updateButtons();

        api.startStudySession(
                new StartStudySessionRequest(subject)
        ).enqueue(new Callback<StudySessionResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Response<StudySessionResponse> response
            ) {
                if (!isAdded()) return;

                sessionStarting = false;
                StudySessionResponse body = response.body();

                if (response.isSuccessful()
                        && body != null
                        && body.getId() != null) {

                    currentSessionId = body.getId();
                    elapsedSeconds = 0;
                    lastTickMillis = System.currentTimeMillis();
                    timerRunning = true;

                    handler.removeCallbacks(timerRunnable);
                    handler.postDelayed(timerRunnable, 1000L);

                    tvFocusSubject.setText(
                            subject.toUpperCase(Locale.getDefault())
                    );

                    updateTimerDisplay();
                    updateButtons();
                    showToast("Focus session started.");
                } else {
                    updateButtons();

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
                if (!isAdded()) return;

                sessionStarting = false;
                updateButtons();

                showToast("Network error: " + safeMessage(t));
            }
        });
    }

    private void togglePause() {
        if (currentSessionId == null || sessionFinishing) return;

        if (timerRunning) {
            elapsedSeconds += Math.max(
                    0L,
                    (System.currentTimeMillis() - lastTickMillis) / 1000L
            );

            timerRunning = false;
            handler.removeCallbacks(timerRunnable);
        } else {
            timerRunning = true;
            lastTickMillis = System.currentTimeMillis();

            handler.removeCallbacks(timerRunnable);
            handler.postDelayed(timerRunnable, 1000L);
        }

        updateTimerDisplay();
        updateButtons();
    }

    private void finishSession() {
        if (!isAdded() || currentSessionId == null || sessionFinishing) {
            if (currentSessionId == null) {
                showToast("Start a session first.");
            }
            return;
        }

        if (timerRunning) {
            elapsedSeconds += Math.max(
                    0L,
                    (System.currentTimeMillis() - lastTickMillis) / 1000L
            );
        }

        timerRunning = false;
        handler.removeCallbacks(timerRunnable);

        final long sessionId = currentSessionId;

        final int minutes = (int) Math.max(
                1L,
                (elapsedSeconds + 59L) / 60L
        );

        sessionFinishing = true;
        updateTimerDisplay();
        updateButtons();

        api.finishStudySession(
                sessionId,
                new FinishStudySessionRequest(minutes)
        ).enqueue(new Callback<StudySessionResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Response<StudySessionResponse> response
            ) {
                if (!isAdded()) return;

                sessionFinishing = false;

                if (response.isSuccessful()) {
                    currentSessionId = null;
                    timerRunning = false;

                    if (currentPlanIndex >= 0
                            && currentPlanIndex < plans.size()) {

                        JsonObject plan = plans.get(currentPlanIndex);

                        int oldMinutes = number(
                                plan,
                                "completedMinutes",
                                0
                        );

                        plan.addProperty(
                                "completedMinutes",
                                oldMinutes + minutes
                        );

                        int target = number(
                                plan,
                                "targetMinutes",
                                number(plan, "durationMinutes", 25)
                        );

                        if (oldMinutes + minutes >= target) {
                            plan.addProperty("completed", true);
                            plan.addProperty("status", "COMPLETED");
                        }

                        savePlans();
                    }

                    elapsedSeconds = 0;
                    currentSubject = "";
                    currentPlanIndex = -1;

                    tvFocusSubject.setText(
                            "READY FOR YOUR NEXT SESSION?"
                    );

                    focusDurationSeconds = DEFAULT_FOCUS_SECONDS;

                    updateTimerDisplay();
                    updateButtons();
                    renderPlans();
                    loadStudyHistory();

                    showToast(
                            "Study session saved: " + minutes + " min"
                    );
                } else {
                    updateButtons();

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
                if (!isAdded()) return;

                sessionFinishing = false;
                updateButtons();

                showToast("Network error: " + safeMessage(t));
            }
        });
    }

    private void updateTimerDisplay() {
        if (tvFocusTimer == null) return;

        long remaining = Math.max(
                0,
                focusDurationSeconds - elapsedSeconds
        );

        long minutes = remaining / 60;
        long seconds = remaining % 60;

        tvFocusTimer.setText(String.format(
                Locale.US,
                "%02d:%02d",
                minutes,
                seconds
        ));

        if (progressFocusTimer != null) {
            progressFocusTimer.setMax(
                    Math.max(1, focusDurationSeconds)
            );

            progressFocusTimer.setProgress((int) Math.max(
                    0,
                    focusDurationSeconds - elapsedSeconds
            ));
        }
    }

    private void updateButtons() {
        if (btnStartSession == null) return;

        boolean active = currentSessionId != null;

        btnStartSession.setEnabled(
                !active && !sessionStarting && !sessionFinishing
        );

        btnStartSession.setText(
                sessionStarting
                        ? "STARTING SESSION…"
                        : "▶   START SESSION"
        );

        btnPauseSession.setEnabled(active && !sessionFinishing);

        btnPauseSession.setText(
                timerRunning ? "Ⅱ   PAUSE" : "▶   RESUME"
        );

        btnFinishSession.setEnabled(active && !sessionFinishing);

        btnFinishSession.setText(
                sessionFinishing ? "SAVING…" : "✓   FINISH"
        );
    }

    // ---------------------------------------------------------
    // STUDY HISTORY
    // ---------------------------------------------------------

    private void loadStudyHistory() {
        if (!isAdded() || api == null) return;

        api.getStudySessions().enqueue(
                new Callback<List<StudySessionResponse>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<StudySessionResponse>> call,
                            @NonNull Response<List<StudySessionResponse>> response
                    ) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            history.clear();

                            Gson gson = new Gson();

                            for (StudySessionResponse session : response.body()) {
                                try {
                                    JsonObject object =
                                            JsonParser.parseString(
                                                    gson.toJson(session)
                                            ).getAsJsonObject();

                                    history.add(object);
                                } catch (Exception ignored) {
                                    // Skip malformed history records.
                                }
                            }

                            renderHistory();
                            updateDashboard();
                        } else {
                            tvHistorySummary.setText(
                                    "Study history could not be loaded."
                            );

                            tvHistorySummary.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<StudySessionResponse>> call,
                            @NonNull Throwable t
                    ) {
                        if (!isAdded()) return;

                        tvHistorySummary.setText(
                                "History unavailable. Check your connection."
                        );

                        tvHistorySummary.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void renderHistory() {
        if (!isAdded() || layoutHistory == null) return;

        layoutHistory.removeAllViews();

        layoutHistory.setVisibility(
                showingHistory ? View.VISIBLE : View.GONE
        );

        btnViewHistory.setText(
                showingHistory
                        ? "HIDE STUDY HISTORY  ↑"
                        : "VIEW STUDY HISTORY  →"
        );

        int totalMinutes = 0;

        for (JsonObject session : history) {
            totalMinutes += number(
                    session,
                    "durationMinutes",
                    number(session, "minutes", 0)
            );
        }

        tvHistorySummary.setText(
                history.size() + " sessions  ·  "
                        + totalMinutes + " minutes recorded"
        );

        tvHistorySummary.setVisibility(View.VISIBLE);

        if (!showingHistory) return;

        if (history.isEmpty()) {
            layoutHistory.addView(makeText(
                    "No completed sessions found yet.",
                    11,
                    "#858DA5",
                    false
            ));

            return;
        }

        for (JsonObject session : history) {
            String subject = stringOr(
                    session,
                    "subject",
                    "Study session"
            );

            int minutes = number(
                    session,
                    "durationMinutes",
                    number(session, "minutes", 0)
            );

            TextView row = makeText(
                    subject + "\n" + minutes + " minutes",
                    12,
                    "#F5F7FF",
                    false
            );

            row.setPadding(dp(12), dp(12), dp(12), dp(12));
            row.setBackgroundResource(R.drawable.bg_main_card);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            params.bottomMargin = dp(7);
            row.setLayoutParams(params);

            layoutHistory.addView(row);
        }
    }

    // ---------------------------------------------------------
    // SUMMARY
    // ---------------------------------------------------------

    private void updateDashboard() {
        if (!isAdded()) return;

        int activePlans = 0;
        int todayMinutes = 0;

        String today = LocalDate.now().toString();

        for (JsonObject plan : plans) {
            if (!isCompleted(plan)) activePlans++;
        }

        for (JsonObject session : history) {
            String date = stringOr(
                    session,
                    "date",
                    stringOr(
                            session,
                            "createdAt",
                            stringOr(session, "startTime", "")
                    )
            );

            if (date.startsWith(today)) {
                todayMinutes += number(
                        session,
                        "durationMinutes",
                        number(session, "minutes", 0)
                );
            }
        }

        tvActivePlans.setText(String.valueOf(activePlans));
        tvStudyTimeToday.setText(todayMinutes + " min");

        tvDailyGoalProgress.setText(
                todayMinutes + " / " + DAILY_GOAL_MINUTES + " min"
        );

        progressDailyGoal.setMax(DAILY_GOAL_MINUTES);

        progressDailyGoal.setProgress(
                Math.min(todayMinutes, DAILY_GOAL_MINUTES)
        );
    }

    // ---------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------

    private TextView makeText(
            String text,
            int sizeSp,
            String color,
            boolean bold
    ) {
        TextView view = new TextView(requireContext());

        view.setText(text);
        view.setTextSize(sizeSp);
        view.setTextColor(Color.parseColor(color));

        if (bold) {
            view.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );
        }

        return view;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private boolean isCompleted(JsonObject plan) {
        if (plan.has("completed")
                && !plan.get("completed").isJsonNull()
                && plan.get("completed").getAsBoolean()) {
            return true;
        }

        return "COMPLETED".equalsIgnoreCase(
                string(plan, "status")
        );
    }

    private String string(JsonObject object, String key) {
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

    private String stringOr(
            JsonObject object,
            String key,
            String fallback
    ) {
        String value = string(object, key);

        return value.trim().isEmpty() ? fallback : value;
    }

    private int number(
            JsonObject object,
            String key,
            int fallback
    ) {
        if (object == null
                || !object.has(key)
                || object.get(key).isJsonNull()) {
            return fallback;
        }

        try {
            return object.get(key).getAsInt();
        } catch (Exception e) {
            return fallback;
        }
    }

    private String readBody(@Nullable ResponseBody body) {
        if (body == null) return "";

        try {
            return body.string();
        } catch (IOException e) {
            return "";
        }
    }

    private void closeBody(@Nullable ResponseBody body) {
        if (body != null) {
            body.close();
        }
    }

    private String safeMessage(Throwable t) {
        if (t == null || t.getMessage() == null) {
            return "Unknown error";
        }

        return t.getMessage();
    }

    private void hideKeyboard() {
        if (!isAdded()) return;

        View focused = requireActivity().getCurrentFocus();

        if (focused == null) return;

        InputMethodManager manager = (InputMethodManager)
                requireContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        if (manager != null) {
            manager.hideSoftInputFromWindow(
                    focused.getWindowToken(),
                    0
            );
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
    public void onDestroyView() {
        timerRunning = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}