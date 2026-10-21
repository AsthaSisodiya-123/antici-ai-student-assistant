package com.anticai.studentassistant.fragments.home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.TaskResponse;
import com.anticai.studentassistant.network.StudyPlanResponse;
import com.anticai.studentassistant.network.StartStudySessionRequest;
import com.anticai.studentassistant.network.StudySessionResponse;
import com.anticai.studentassistant.network.FinishStudySessionRequest;
import com.anticai.studentassistant.network.DashboardResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TextView tvWelcome;
    private TextView tvDashboardDate;
    private TextView tvStudyHours;
    private TextView tvCompleted;
    private TextView tvPlans;
    private TextView tvPending;
    private TextView tvGoalPercent;
    private TextView tvGoalMessage;

    private TextView tvPriorityTitle;
    private TextView tvPrioritySubject;
    private TextView tvPriorityLevel;
    private TextView tvPriorityReason;
    private TextView tvPriorityDuration;

    private TextView tvPriorityTasksEmpty;
    private TextView btnStartPriorityTask;
    private TextView btnRefreshPriority;

    private ProgressBar progressGoal;
    private LinearLayout layoutPriorityTasks;

    private ApiService apiService;

    private final List<TaskResponse> tasks = new ArrayList<>();

    private TaskResponse recommendedTask;
    private Long activeSessionId;

    private CountDownTimer focusTimer;

    private long sessionStartMillis;
    private long remainingMillis = 25 * 60 * 1000L;

    private boolean sessionRunning = false;
    private boolean sessionStarting = false;
    private boolean sessionFinishing = false;

    private static final long FOCUS_DURATION =
            25 * 60 * 1000L;

    private int studyMinutes = 0;

    public DashboardFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_dashboard,
                container,
                false
        );

        bindViews(view);

        apiService = RetrofitClient.getApiService(
                requireContext()
        );

        showDate();
        setInitialValues();
        setupActions();

        loadTasks();
        loadStudyPlans();
        loadDashboardStats();

        return view;
    }

    private void bindViews(View view) {

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvDashboardDate = view.findViewById(R.id.tvDashboardDate);

        tvStudyHours = view.findViewById(
                R.id.tvDashboardStudyHours
        );

        tvCompleted = view.findViewById(
                R.id.tvDashboardCompleted
        );

        tvPlans = view.findViewById(
                R.id.tvDashboardPlans
        );

        tvPending = view.findViewById(
                R.id.tvDashboardPending
        );

        tvGoalPercent = view.findViewById(
                R.id.tvDashboardGoalPercent
        );

        progressGoal = view.findViewById(
                R.id.progressDashboardGoal
        );

        tvGoalMessage = view.findViewById(
                R.id.tvDashboardGoalMessage
        );

        tvPriorityTitle = view.findViewById(
                R.id.tvPriorityTitle
        );

        tvPrioritySubject = view.findViewById(
                R.id.tvPrioritySubject
        );

        tvPriorityLevel = view.findViewById(
                R.id.tvPriorityLevel
        );

        tvPriorityReason = view.findViewById(
                R.id.tvPriorityReason
        );

        tvPriorityDuration = view.findViewById(
                R.id.tvPriorityDuration
        );

        btnStartPriorityTask = view.findViewById(
                R.id.btnStartPriorityTask
        );

        btnRefreshPriority = view.findViewById(
                R.id.btnRefreshPriority
        );

        tvPriorityTasksEmpty = view.findViewById(
                R.id.tvPriorityTasksEmpty
        );

        layoutPriorityTasks = view.findViewById(
                R.id.layoutPriorityTasks
        );
    }

    private void setInitialValues() {

        tvPriorityTitle.setText("Finding your next task...");
        tvPrioritySubject.setText(
                "Checking pending tasks and deadlines."
        );

        tvPriorityLevel.setText("Priority: Calculating...");
        tvPriorityReason.setText(
                "Antici AI is reviewing your tasks."
        );

        tvPriorityDuration.setText(
                "Suggested focus time: 25 minutes"
        );

        tvStudyHours.setText("0 min");
        tvCompleted.setText("0");
        tvPending.setText("0");
        tvPlans.setText("0");
        tvGoalPercent.setText("0%");
        progressGoal.setProgress(0);
    }

    private void showDate() {

        String date = LocalDate.now().format(
                DateTimeFormatter.ofPattern(
                        "EEEE, dd MMMM yyyy",
                        Locale.getDefault()
                )
        );

        tvDashboardDate.setText(
                date + "\nLet's make today productive."
        );
    }

    private void setupActions() {

        btnStartPriorityTask.setOnClickListener(v -> {

            if (sessionStarting || sessionFinishing) {
                return;
            }

            if (sessionRunning) {
                finishStudySession();
                return;
            }

            if (recommendedTask == null) {
                showMessage(
                        "No pending task available. Add a task first."
                );
                return;
            }

            startStudySession();
        });

        btnRefreshPriority.setOnClickListener(v -> {

            if (sessionRunning || sessionStarting
                    || sessionFinishing) {

                showMessage(
                        "Finish your current study session first."
                );

                return;
            }

            tvPriorityTitle.setText(
                    "Recalculating priorities..."
            );

            loadTasks();
            loadStudyPlans();
            loadDashboardStats();
        });
    }

    // ---------------------------------------------------------
    // LOAD TASKS
    // ---------------------------------------------------------

    private void loadTasks() {

        apiService.getTasks().enqueue(
                new Callback<List<TaskResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<TaskResponse>> call,
                            @NonNull Response<List<TaskResponse>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            tasks.clear();
                            tasks.addAll(response.body());

                            sortTasksByPriority();
                            updateTaskStatistics();
                            displayRecommendedTask();
                            displayTaskList();

                        } else {

                            showMessage(
                                    "Could not load tasks. HTTP "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<TaskResponse>> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        showMessage(
                                "Task loading failed: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    private boolean isCompleted(TaskResponse task) {

        String status = task.getStatus();

        if (status == null) return false;

        return status.equalsIgnoreCase("COMPLETED")
                || status.equalsIgnoreCase("DONE");
    }

    private void updateTaskStatistics() {

        int completed = 0;
        int pending = 0;

        for (TaskResponse task : tasks) {

            if (isCompleted(task)) {
                completed++;
            } else {
                pending++;
            }
        }

        tvCompleted.setText(String.valueOf(completed));
        tvPending.setText(String.valueOf(pending));
    }

    // ---------------------------------------------------------
    // PRIORITY CALCULATION
    // ---------------------------------------------------------

    private void sortTasksByPriority() {

        Collections.sort(
                tasks,
                new Comparator<TaskResponse>() {

                    @Override
                    public int compare(
                            TaskResponse first,
                            TaskResponse second) {

                        if (isCompleted(first)
                                && !isCompleted(second)) {
                            return 1;
                        }

                        if (!isCompleted(first)
                                && isCompleted(second)) {
                            return -1;
                        }

                        return Integer.compare(
                                calculatePriorityScore(second),
                                calculatePriorityScore(first)
                        );
                    }
                }
        );
    }

    private int calculatePriorityScore(TaskResponse task) {

        int score = 0;

        String priority = task.getPriority();

        if (priority != null) {

            switch (priority.toUpperCase(Locale.ROOT)) {

                case "URGENT":
                    score += 400;
                    break;

                case "HIGH":
                    score += 300;
                    break;

                case "MEDIUM":
                    score += 200;
                    break;

                case "LOW":
                    score += 100;
                    break;
            }
        }

        LocalDate dueDate = parseDueDate(
                task.getDueDate()
        );

        if (dueDate != null) {

            long daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    dueDate
            );

            if (daysLeft < 0) {
                score += 500;
            } else if (daysLeft == 0) {
                score += 400;
            } else if (daysLeft <= 2) {
                score += 250;
            } else if (daysLeft <= 7) {
                score += 100;
            }
        }

        return score;
    }

    private LocalDate parseDueDate(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(value.trim());
        } catch (Exception ignored) {

            try {
                return LocalDate.parse(
                        value.trim().substring(0, 10)
                );
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
    }

    private String getPriorityReason(TaskResponse task) {

        LocalDate dueDate = parseDueDate(
                task.getDueDate()
        );

        if (dueDate != null) {

            long daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    dueDate
            );

            if (daysLeft < 0) {
                return "This task is overdue and needs attention first.";
            }

            if (daysLeft == 0) {
                return "This task is due today.";
            }

            if (daysLeft <= 2) {
                return "Its deadline is approaching.";
            }
        }

        String priority = task.getPriority();

        if ("URGENT".equalsIgnoreCase(priority)
                || "HIGH".equalsIgnoreCase(priority)) {

            return "This task has high assigned priority.";
        }

        return "Selected using task priority and deadline.";
    }

    private void displayRecommendedTask() {

        if (sessionRunning) return;

        recommendedTask = null;

        for (TaskResponse task : tasks) {

            if (!isCompleted(task)) {
                recommendedTask = task;
                break;
            }
        }

        if (recommendedTask == null) {

            tvPriorityTitle.setText(
                    "You're all caught up! 🎉"
            );

            tvPrioritySubject.setText(
                    "No pending tasks. Add a task to plan your next session."
            );

            tvPriorityLevel.setText(
                    "Priority: All clear"
            );

            tvPriorityReason.setText(
                    "Great work! Keep up your study routine."
            );

            tvPriorityDuration.setText(
                    "Suggested next step: Create a new task"
            );

            btnStartPriorityTask.setText(
                    "No Pending Task"
            );

            return;
        }

        String title = recommendedTask.getTitle();

        tvPriorityTitle.setText(
                title == null ? "Your next task" : title
        );

        String description =
                recommendedTask.getDescription();

        tvPrioritySubject.setText(
                description == null
                        || description.trim().isEmpty()
                        ? "Work on this task now."
                        : description
        );

        String priority = recommendedTask.getPriority();

        if (priority == null || priority.trim().isEmpty()) {
            priority = "MEDIUM";
        }

        tvPriorityLevel.setText(
                "Priority: "
                        + priority.toUpperCase(Locale.ROOT)
        );

        tvPriorityReason.setText(
                getPriorityReason(recommendedTask)
        );

        tvPriorityDuration.setText(
                "Suggested focus time: 25 minutes"
        );

        btnStartPriorityTask.setText(
                "Start This Task  →"
        );
    }

    // ---------------------------------------------------------
    // TASK LIST WITH A GREEN BUTTON BELOW EVERY TASK
    // ---------------------------------------------------------

    private void displayTaskList() {

        layoutPriorityTasks.removeAllViews();

        boolean hasPending = false;

        for (TaskResponse task : tasks) {

            if (isCompleted(task)) {
                continue;
            }

            hasPending = true;

            // Task card
            LinearLayout card = new LinearLayout(
                    requireContext()
            );

            card.setOrientation(
                    LinearLayout.VERTICAL
            );

            card.setPadding(18, 16, 18, 16);

            card.setBackgroundResource(
                    R.drawable.bg_main_card
            );

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, 16);

            card.setLayoutParams(cardParams);

            // Task title
            TextView title = new TextView(
                    requireContext()
            );

            title.setText(
                    task.getTitle() == null
                            ? "Untitled Task"
                            : task.getTitle()
            );

            title.setTextColor(
                    Color.WHITE
            );

            title.setTextSize(17);

            title.setTypeface(
                    null,
                    Typeface.BOLD
            );

            card.addView(title);

            // Priority and deadline
            String priority = task.getPriority();

            if (priority == null || priority.trim().isEmpty()) {
                priority = "MEDIUM";
            }

            String dueDate = task.getDueDate();

            if (dueDate == null || dueDate.trim().isEmpty()) {
                dueDate = "No deadline";
            }

            TextView details = new TextView(
                    requireContext()
            );

            details.setText(
                    "Priority: " + priority
                            + "\nDeadline: " + dueDate
            );

            details.setTextColor(
                    Color.rgb(185, 191, 210)
            );

            details.setTextSize(13);

            details.setLineSpacing(5, 1.0f);

            LinearLayout.LayoutParams detailsParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            detailsParams.setMargins(0, 8, 0, 14);

            details.setLayoutParams(detailsParams);

            card.addView(details);

            // Green completion button
            TextView completeButton = new TextView(
                    requireContext()
            );

            completeButton.setText(
                    "✓   Mark as Completed"
            );

            completeButton.setTextColor(
                    Color.rgb(8, 20, 18)
            );

            completeButton.setTextSize(14);

            completeButton.setTypeface(
                    null,
                    Typeface.BOLD
            );

            completeButton.setGravity(
                    Gravity.CENTER
            );

            completeButton.setPadding(
                    14, 14, 14, 14
            );

            GradientDrawable buttonBackground =
                    new GradientDrawable();

            buttonBackground.setColor(
                    Color.rgb(97, 230, 167)
            );

            buttonBackground.setCornerRadius(
                    14 * getResources().getDisplayMetrics().density
            );

            completeButton.setBackground(
                    buttonBackground
            );

            completeButton.setClickable(true);
            completeButton.setFocusable(true);

            card.addView(completeButton);

            // Clicking the task selects it as the recommendation.
            card.setOnClickListener(v -> {

                if (sessionRunning || sessionStarting) {
                    showMessage(
                            "Finish your current session first."
                    );
                    return;
                }

                recommendedTask = task;

                displayRecommendedTask();
            });

            // Clicking the button completes only this task.
            completeButton.setOnClickListener(v -> {

                if (sessionRunning || sessionStarting
                        || sessionFinishing) {

                    showMessage(
                            "Finish your study session first."
                    );

                    return;
                }

                completeButton.setEnabled(false);
                completeButton.setText("Updating...");

                completeTask(task, completeButton);
            });

            // Add this card to the vertical task list.
            layoutPriorityTasks.addView(card);
        }

        if (!hasPending) {

            tvPriorityTasksEmpty.setVisibility(
                    View.VISIBLE
            );

            tvPriorityTasksEmpty.setText(
                    "✓  No pending tasks. Great work!"
            );

            layoutPriorityTasks.addView(
                    tvPriorityTasksEmpty
            );

        } else {

            tvPriorityTasksEmpty.setVisibility(
                    View.GONE
            );
        }
    }

    // ---------------------------------------------------------
    // COMPLETE TASK THROUGH BACKEND
    // ---------------------------------------------------------

    private void completeTask(
            TaskResponse task,
            TextView completeButton) {

        if (task.getId() == null) {

            completeButton.setEnabled(true);

            completeButton.setText(
                    "✓   Mark as Completed"
            );

            showMessage(
                    "This task has no valid ID."
            );

            return;
        }

        apiService.completeTask(
                task.getId()
        ).enqueue(new Callback<TaskResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<TaskResponse> call,
                    @NonNull Response<TaskResponse> response) {

                if (!isAdded()) return;

                if (response.isSuccessful()) {

                    showMessage(
                            "Task completed successfully!"
                    );

                    // Reload from the backend to get the true status.
                    loadTasks();
                    loadDashboardStats();

                } else {

                    completeButton.setEnabled(true);

                    completeButton.setText(
                            "✓   Mark as Completed"
                    );

                    showMessage(
                            "Could not complete task. HTTP "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<TaskResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;

                completeButton.setEnabled(true);

                completeButton.setText(
                        "✓   Mark as Completed"
                );

                showMessage(
                        "Network error: " + t.getMessage()
                );
            }
        });
    }

    // ---------------------------------------------------------
    // START STUDY SESSION
    // ---------------------------------------------------------

    private void startStudySession() {

        if (recommendedTask == null) return;

        sessionStarting = true;

        btnStartPriorityTask.setEnabled(false);

        btnStartPriorityTask.setText(
                "Starting Session..."
        );

        String subject = recommendedTask.getTitle();

        if (subject == null || subject.trim().isEmpty()) {
            subject = "Focused Study";
        }

        apiService.startStudySession(
                new StartStudySessionRequest(subject)
        ).enqueue(new Callback<StudySessionResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Response<StudySessionResponse> response) {

                if (!isAdded()) return;

                sessionStarting = false;

                btnStartPriorityTask.setEnabled(true);

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getId() != null) {

                    activeSessionId =
                            response.body().getId();

                    sessionRunning = true;

                    sessionStartMillis =
                            System.currentTimeMillis();

                    remainingMillis = FOCUS_DURATION;

                    btnStartPriorityTask.setText(
                            "Finish Study Session"
                    );

                    tvPriorityReason.setText(
                            "Focus on this task. Avoid distractions."
                    );

                    startCountdown();

                    showMessage(
                            "Study session started!"
                    );

                } else {

                    btnStartPriorityTask.setText(
                            "Start This Task  →"
                    );

                    showMessage(
                            "Could not start session. HTTP "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;

                sessionStarting = false;

                btnStartPriorityTask.setEnabled(true);

                btnStartPriorityTask.setText(
                        "Start This Task  →"
                );

                showMessage(
                        "Session error: " + t.getMessage()
                );
            }
        });
    }

    private void startCountdown() {

        if (focusTimer != null) {
            focusTimer.cancel();
        }

        focusTimer = new CountDownTimer(
                remainingMillis,
                1000
        ) {

            @Override
            public void onTick(long millisUntilFinished) {

                remainingMillis = millisUntilFinished;

                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;

                tvPriorityDuration.setText(
                        String.format(
                                Locale.getDefault(),
                                "Focus time remaining: %02d:%02d",
                                minutes,
                                seconds
                        )
                );
            }

            @Override
            public void onFinish() {

                remainingMillis = 0;

                tvPriorityDuration.setText(
                        "Focus time finished! Save your session."
                );

                showMessage(
                        "25 minutes completed. Tap Finish Study Session to save."
                );
            }
        }.start();
    }

    // ---------------------------------------------------------
    // FINISH AND SAVE SESSION
    // ---------------------------------------------------------

    private void finishStudySession() {

        if (activeSessionId == null) {

            showMessage(
                    "No active study session was found."
            );

            return;
        }

        if (sessionFinishing) return;

        sessionFinishing = true;

        if (focusTimer != null) {
            focusTimer.cancel();
            focusTimer = null;
        }

        long elapsedMillis =
                System.currentTimeMillis()
                        - sessionStartMillis;

        int elapsedMinutes =
                (int) (elapsedMillis / 60000L);

        btnStartPriorityTask.setEnabled(false);

        btnStartPriorityTask.setText(
                "Saving Session..."
        );

        apiService.finishStudySession(
                activeSessionId,
                new FinishStudySessionRequest(
                        elapsedMinutes
                )
        ).enqueue(new Callback<StudySessionResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Response<StudySessionResponse> response) {

                if (!isAdded()) return;

                sessionFinishing = false;

                btnStartPriorityTask.setEnabled(true);

                if (response.isSuccessful()) {

                    sessionRunning = false;
                    activeSessionId = null;

                    remainingMillis = FOCUS_DURATION;

                    btnStartPriorityTask.setText(
                            "Start This Task  →"
                    );

                    tvPriorityDuration.setText(
                            "Suggested focus time: 25 minutes"
                    );

                    showMessage(
                            "Study session saved successfully!"
                    );

                    loadDashboardStats();
                    displayRecommendedTask();

                } else {

                    // Retain session ID so finishing can be retried.
                    btnStartPriorityTask.setText(
                            "Retry Finish Session"
                    );

                    showMessage(
                            "Could not save session. HTTP "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<StudySessionResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;

                sessionFinishing = false;

                btnStartPriorityTask.setEnabled(true);

                btnStartPriorityTask.setText(
                        "Retry Finish Session"
                );

                showMessage(
                        "Could not save session: "
                                + t.getMessage()
                );
            }
        });
    }

    // ---------------------------------------------------------
    // ACTIVE STUDY PLANS
    // ---------------------------------------------------------

    private void loadStudyPlans() {

        apiService.getActiveStudyPlans().enqueue(
                new Callback<List<StudyPlanResponse>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<StudyPlanResponse>> call,
                            @NonNull Response<List<StudyPlanResponse>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            tvPlans.setText(
                                    String.valueOf(
                                            response.body().size()
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<StudyPlanResponse>> call,
                            @NonNull Throwable t) {
                        // Keep the remaining dashboard working.
                    }
                }
        );
    }

    // ---------------------------------------------------------
    // DASHBOARD STATISTICS
    // ---------------------------------------------------------

    private void loadDashboardStats() {

        apiService.getDashboard().enqueue(
                new Callback<DashboardResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<DashboardResponse> call,
                            @NonNull Response<DashboardResponse> response) {

                        if (!isAdded()) return;

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            return;
                        }

                        JsonObject data = new Gson().toJsonTree(
                                response.body()
                        ).getAsJsonObject();

                        String minutesValue = getValue(
                                data,
                                "studyMinutes",
                                "totalStudyMinutes"
                        );

                        String hoursValue = getValue(
                                data,
                                "studyHours",
                                "totalStudyHours"
                        );

                        if (minutesValue != null) {

                            studyMinutes = parseInt(
                                    minutesValue,
                                    0
                            );

                            tvStudyHours.setText(
                                    formatStudyTime(studyMinutes)
                            );

                        } else if (hoursValue != null) {

                            tvStudyHours.setText(
                                    hoursValue + " hrs"
                            );
                        }

                        int percent = parseInt(
                                getValue(
                                        data,
                                        "goalPercent",
                                        "goalProgress"
                                ),
                                -1
                        );

                        if (percent < 0) {

                            int goalMinutes = parseInt(
                                    getValue(
                                            data,
                                            "dailyGoalMinutes",
                                            "goalMinutes"
                                    ),
                                    120
                            );

                            percent = goalMinutes > 0
                                    ? studyMinutes * 100 / goalMinutes
                                    : 0;
                        }

                        percent = Math.max(
                                0,
                                Math.min(100, percent)
                        );

                        progressGoal.setProgress(percent);

                        tvGoalPercent.setText(
                                percent + "%"
                        );

                        if (percent >= 100) {

                            tvGoalMessage.setText(
                                    "Daily study goal achieved! Excellent work."
                            );

                        } else {

                            tvGoalMessage.setText(
                                    percent
                                            + "% of your daily study goal completed."
                            );
                        }

                        String name = getValue(
                                data,
                                "name",
                                "studentName",
                                "userName"
                        );

                        if (name != null
                                && !name.trim().isEmpty()) {

                            tvWelcome.setText(
                                    "Welcome back, "
                                            + name + "! 👋"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DashboardResponse> call,
                            @NonNull Throwable t) {
                        // Other dashboard sections remain available.
                    }
                }
        );
    }

    private String getValue(
            JsonObject data,
            String... keys) {

        for (String key : keys) {

            try {

                if (data.has(key)
                        && !data.get(key).isJsonNull()) {

                    return data.get(key).getAsString();
                }

            } catch (Exception ignored) {
                // Try the next field.
            }
        }

        return null;
    }

    private int parseInt(
            String value,
            int fallback) {

        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private String formatStudyTime(int minutes) {

        if (minutes < 60) {
            return minutes + " min";
        }

        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        if (remainingMinutes == 0) {
            return hours + " hr";
        }

        return hours + " hr "
                + remainingMinutes + " min";
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

        if (focusTimer != null) {
            focusTimer.cancel();
            focusTimer = null;
        }

        // The backend session is not automatically finished here.
        // Do not leave the dashboard during an active session.

        super.onDestroyView();
    }
}
