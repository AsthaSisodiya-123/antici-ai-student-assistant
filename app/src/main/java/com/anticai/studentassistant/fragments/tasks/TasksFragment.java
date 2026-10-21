package com.anticai.studentassistant.fragments.tasks;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.TaskRequest;
import com.anticai.studentassistant.network.TaskResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksFragment extends Fragment {

    private ApiService apiService;

    private TextView tvActiveTasks;
    private TextView tvDueSoonTasks;
    private TextView tvCompletedTasks;
    private TextView tvTaskCompletionSummary;
    private TextView tvTaskListCount;

    private ProgressBar progressTaskCompletion;

    private RadioGroup rgTaskFilter;
    private LinearLayout layoutTaskList;
    private LinearLayout tvTasksEmpty;

    private final List<TaskResponse> allTasks = new ArrayList<>();

    private String currentFilter = "ALL";

    public TasksFragment() {
        super(R.layout.fragment_tasks);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());

        tvActiveTasks = view.findViewById(R.id.tvActiveTasks);
        tvDueSoonTasks = view.findViewById(R.id.tvDueSoonTasks);
        tvCompletedTasks = view.findViewById(R.id.tvCompletedTasks);
        tvTaskCompletionSummary =
                view.findViewById(R.id.tvTaskCompletionSummary);
        tvTaskListCount = view.findViewById(R.id.tvTaskListCount);

        progressTaskCompletion =
                view.findViewById(R.id.progressTaskCompletion);

        rgTaskFilter = view.findViewById(R.id.rgTaskFilter);
        layoutTaskList = view.findViewById(R.id.layoutTaskList);
        tvTasksEmpty = view.findViewById(R.id.tvTasksEmpty);

        TextView btnAddTask = view.findViewById(R.id.btnAddTask);
        TextView btnAssignments = view.findViewById(R.id.btnAssignments);

        btnAddTask.setOnClickListener(v -> showAddTaskDialog("PERSONAL"));

        btnAssignments.setOnClickListener(
                v -> showAddTaskDialog("ASSIGNMENT"));

        rgTaskFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbTaskPending) {
                currentFilter = "PENDING";
            } else if (checkedId == R.id.rbTaskCompleted) {
                currentFilter = "COMPLETED";
            } else {
                currentFilter = "ALL";
            }

            renderTasks();
        });

        loadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (apiService != null) {
            loadTasks();
        }
    }

    private void loadTasks() {
        apiService.getTasks().enqueue(new Callback<List<TaskResponse>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<TaskResponse>> call,
                    @NonNull Response<List<TaskResponse>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    allTasks.clear();
                    allTasks.addAll(response.body());

                    updateSummary();
                    renderTasks();
                } else {
                    showMessage("Could not load tasks. HTTP "
                            + response.code());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<TaskResponse>> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;

                showMessage("Connection failed: " + t.getMessage());
            }
        });
    }

    private void showAddTaskDialog(String taskType) {
        if (!isAdded()) return;

        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(40, 12, 40, 8);

        EditText etTitle = new EditText(requireContext());
        etTitle.setHint("Task title *");
        form.addView(etTitle);

        EditText etDescription = new EditText(requireContext());
        etDescription.setHint("Description (optional)");
        etDescription.setMinLines(2);
        etDescription.setGravity(Gravity.TOP);
        form.addView(etDescription);

        EditText etDueDate = new EditText(requireContext());
        etDueDate.setHint("Due date: YYYY-MM-DD (optional)");
        form.addView(etDueDate);

        EditText etPriority = new EditText(requireContext());
        etPriority.setHint("Priority: LOW, MEDIUM, HIGH");
        etPriority.setText("MEDIUM");
        form.addView(etPriority);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(taskType.equals("ASSIGNMENT")
                        ? "Add Assignment" : "Add Task")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String title = etTitle.getText()
                                    .toString().trim();
                            String description = etDescription.getText()
                                    .toString().trim();
                            String dueDate = etDueDate.getText()
                                    .toString().trim();
                            String priority = etPriority.getText()
                                    .toString().trim().toUpperCase(
                                            Locale.ROOT);

                            if (title.isEmpty()) {
                                etTitle.setError("Enter a task title");
                                return;
                            }

                            if (!dueDate.isEmpty()
                                    && !dueDate.matches(
                                    "\\d{4}-\\d{2}-\\d{2}")) {
                                etDueDate.setError(
                                        "Use YYYY-MM-DD format");
                                return;
                            }

                            if (!priority.equals("LOW")
                                    && !priority.equals("MEDIUM")
                                    && !priority.equals("HIGH")) {
                                etPriority.setError(
                                        "Enter LOW, MEDIUM, or HIGH");
                                return;
                            }

                            TaskRequest request = new TaskRequest(
                                    title,
                                    description,
                                    dueDate.isEmpty() ? null : dueDate,
                                    "PENDING",
                                    priority,
                                    taskType
                            );

                            saveTask(request, dialog);
                        }));

        dialog.show();
    }

    private void saveTask(TaskRequest request, AlertDialog dialog) {
        apiService.createTask(request).enqueue(
                new Callback<TaskResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<TaskResponse> call,
                            @NonNull Response<TaskResponse> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {
                            dialog.dismiss();
                            showMessage("Task added successfully");
                            loadTasks();
                        } else {
                            showMessage("Could not save task. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<TaskResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        showMessage("Save failed: " + t.getMessage());
                    }
                });
    }

    private void renderTasks() {
        if (!isAdded() || layoutTaskList == null) return;

        layoutTaskList.removeAllViews();

        List<TaskResponse> filtered = new ArrayList<>();

        for (TaskResponse task : allTasks) {
            String status = task.getStatus() == null
                    ? "PENDING" : task.getStatus();

            if (currentFilter.equals("ALL")
                    || currentFilter.equalsIgnoreCase(status)) {
                filtered.add(task);
            }
        }

        tvTaskListCount.setText(filtered.size() + " tasks");

        boolean empty = filtered.isEmpty();
        tvTasksEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        layoutTaskList.setVisibility(empty ? View.GONE : View.VISIBLE);

        for (TaskResponse task : filtered) {
            layoutTaskList.addView(createTaskCard(task));
        }
    }

    private View createTaskCard(TaskResponse task) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 24, 28, 24);
        card.setBackgroundColor(0xFF171B2A);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);

        TextView title = new TextView(requireContext());
        title.setText(task.getTitle() == null
                ? "Untitled task" : task.getTitle());
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(17);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);

        if (task.getDescription() != null
                && !task.getDescription().trim().isEmpty()) {
            TextView description = new TextView(requireContext());
            description.setText(task.getDescription());
            description.setTextColor(0xFFB8BED0);
            description.setTextSize(14);
            card.addView(description);
        }

        String dueDate = task.getDueDate() == null
                ? "No due date" : "Due: " + task.getDueDate();

        String priority = task.getPriority() == null
                ? "MEDIUM" : task.getPriority();

        String type = task.getTaskType() == null
                ? "PERSONAL" : task.getTaskType();

        TextView details = new TextView(requireContext());
        details.setText(dueDate + "  •  " + priority + "  •  " + type);
        details.setTextColor(0xFF8F9BB3);
        details.setTextSize(12);
        details.setPadding(0, 10, 0, 10);
        card.addView(details);

        String status = task.getStatus() == null
                ? "PENDING" : task.getStatus();

        TextView statusView = new TextView(requireContext());
        statusView.setText("Status: " + status);
        statusView.setTextColor(status.equalsIgnoreCase("COMPLETED")
                ? 0xFF63D9A0 : 0xFFFFC66D);
        card.addView(statusView);

        LinearLayout actions = new LinearLayout(requireContext());
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);
        actions.setPadding(0, 12, 0, 0);

        if (!status.equalsIgnoreCase("COMPLETED")) {
            Button completeButton = new Button(requireContext());
            completeButton.setText("Complete");
            completeButton.setOnClickListener(v ->
                    completeTask(task.getId()));
            actions.addView(completeButton);
        }

        Button deleteButton = new Button(requireContext());
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v ->
                confirmDelete(task));
        actions.addView(deleteButton);

        card.addView(actions);

        return card;
    }

    private void completeTask(Long id) {
        if (id == null) return;

        apiService.completeTask(id).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<TaskResponse> call,
                    @NonNull Response<TaskResponse> response) {

                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    showMessage("Task completed");
                    loadTasks();
                } else {
                    showMessage("Could not complete task. HTTP "
                            + response.code());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<TaskResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;
                showMessage("Request failed: " + t.getMessage());
            }
        });
    }

    private void confirmDelete(TaskResponse task) {
        if (!isAdded() || task.getId() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete task?")
                .setMessage("Delete \"" + task.getTitle() + "\"?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteTask(task.getId()))
                .show();
    }

    private void deleteTask(Long id) {
        apiService.deleteTask(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(
                    @NonNull Call<Void> call,
                    @NonNull Response<Void> response) {

                if (!isAdded()) return;

                if (response.isSuccessful()) {
                    showMessage("Task deleted");
                    loadTasks();
                } else {
                    showMessage("Could not delete task. HTTP "
                            + response.code());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<Void> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;
                showMessage("Delete failed: " + t.getMessage());
            }
        });
    }

    private void updateSummary() {
        int completed = 0;
        int pending = 0;
        int dueSoon = 0;

        String today = java.time.LocalDate.now().toString();
        String weekLater = java.time.LocalDate.now()
                .plusDays(7).toString();

        for (TaskResponse task : allTasks) {
            String status = task.getStatus() == null
                    ? "PENDING" : task.getStatus();

            if (status.equalsIgnoreCase("COMPLETED")) {
                completed++;
            } else {
                pending++;

                String dueDate = task.getDueDate();
                if (dueDate != null
                        && dueDate.compareTo(today) >= 0
                        && dueDate.compareTo(weekLater) <= 0) {
                    dueSoon++;
                }
            }
        }

        tvActiveTasks.setText(String.valueOf(pending));
        tvDueSoonTasks.setText(String.valueOf(dueSoon));
        tvCompletedTasks.setText(String.valueOf(completed));

        int total = allTasks.size();
        int percent = total == 0 ? 0 : (completed * 100 / total);

        progressTaskCompletion.setMax(100);
        progressTaskCompletion.setProgress(percent);

        tvTaskCompletionSummary.setText(
                completed + " of " + total
                        + " tasks completed (" + percent + "%)");
    }

    private void showMessage(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
