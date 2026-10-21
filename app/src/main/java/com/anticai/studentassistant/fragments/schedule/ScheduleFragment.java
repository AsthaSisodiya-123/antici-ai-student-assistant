package com.anticai.studentassistant.fragments.schedule;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.anticai.studentassistant.network.ScheduleRequest;
import com.anticai.studentassistant.network.ScheduleResponse;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleFragment extends Fragment {

    private ApiService apiService;

    private RadioGroup rgScheduleType;
    private TextView txtScheduleDate;
    private TextView txtScheduleSummary;
    private TextView txtSelectedDay;
    private TextView txtSelectedDaySubtitle;
    private ViewGroup scheduleList;
    private View layoutScheduleEmpty;

    private String selectedType = "CLASS";

    private final List<ScheduleResponse> schedules =
            new ArrayList<>();

    public ScheduleFragment() {
        super(R.layout.fragment_schedule);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());

        rgScheduleType = view.findViewById(R.id.rgScheduleType);
        txtScheduleDate = view.findViewById(R.id.txtScheduleDate);
        txtScheduleSummary = view.findViewById(R.id.txtScheduleSummary);
        txtSelectedDay = view.findViewById(R.id.txtSelectedDay);
        txtSelectedDaySubtitle =
                view.findViewById(R.id.txtSelectedDaySubtitle);

        scheduleList = view.findViewById(R.id.scheduleList);
        layoutScheduleEmpty =
                view.findViewById(R.id.layoutScheduleEmpty);

        View btnAddClass = view.findViewById(R.id.btnAddClass);

        updateHeader();

        rgScheduleType.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    selectedType = checkedId == R.id.rbExams
                            ? "EXAM" : "CLASS";

                    updateHeader();
                    loadSchedules();
                });

        btnAddClass.setOnClickListener(v -> showAddDialog());

        loadSchedules();
    }

    private void updateHeader() {
        if (!isAdded()) return;

        LocalDate today = LocalDate.now();

        txtScheduleDate.setText(today.toString());

        txtSelectedDay.setText(
                today.getDayOfWeek().getDisplayName(
                        TextStyle.FULL, Locale.getDefault()));

        if (selectedType.equals("EXAM")) {
            txtSelectedDaySubtitle.setText("Your exam schedule");
            txtScheduleSummary.setText("Upcoming exams");
        } else {
            txtSelectedDaySubtitle.setText("Your class timetable");
            txtScheduleSummary.setText("Scheduled classes");
        }
    }

    private void loadSchedules() {
        apiService.getSchedulesByType(selectedType)
                .enqueue(new Callback<List<ScheduleResponse>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<List<ScheduleResponse>> call,
                            @NonNull Response<List<ScheduleResponse>> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {
                            schedules.clear();
                            schedules.addAll(response.body());
                            renderSchedules();
                        } else {
                            showMessage("Could not load schedule. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<ScheduleResponse>> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        showMessage("Connection failed: "
                                + t.getMessage());
                    }
                });
    }

    private void renderSchedules() {
        if (!isAdded() || scheduleList == null) return;

        scheduleList.removeAllViews();

        boolean empty = schedules.isEmpty();

        layoutScheduleEmpty.setVisibility(
                empty ? View.VISIBLE : View.GONE);
        scheduleList.setVisibility(
                empty ? View.GONE : View.VISIBLE);

        for (ScheduleResponse schedule : schedules) {
            scheduleList.addView(createScheduleCard(schedule));
        }
    }

    private View createScheduleCard(ScheduleResponse schedule) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 24, 28, 24);
        card.setBackgroundColor(0xFF171B2A);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 20);
        card.setLayoutParams(params);

        TextView title = new TextView(requireContext());
        title.setText(schedule.getTitle() == null
                ? "Schedule" : schedule.getTitle());
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(17);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);

        if (schedule.getSubject() != null
                && !schedule.getSubject().isBlank()) {
            addCardText(card, "Subject: " + schedule.getSubject());
        }

        if (selectedType.equals("CLASS")) {
            addCardText(card, "Day: " + safe(schedule.getDayOfWeek()));
        } else {
            addCardText(card,
                    "Date: " + safe(schedule.getScheduleDate()));
        }

        String time = safe(schedule.getStartTime());

        if (schedule.getEndTime() != null
                && !schedule.getEndTime().isBlank()) {
            time += " - " + schedule.getEndTime();
        }

        if (!time.isBlank()) {
            addCardText(card, "Time: " + time);
        }

        if (schedule.getLocation() != null
                && !schedule.getLocation().isBlank()) {
            addCardText(card, "Location: " + schedule.getLocation());
        }

        if (schedule.getNotes() != null
                && !schedule.getNotes().isBlank()) {
            addCardText(card, "Notes: " + schedule.getNotes());
        }

        TextView delete = new TextView(requireContext());
        delete.setText("Delete schedule");
        delete.setTextColor(0xFFFF8A8A);
        delete.setTextSize(14);
        delete.setPadding(0, 16, 0, 0);
        delete.setGravity(Gravity.END);
        delete.setOnClickListener(v -> confirmDelete(schedule));

        card.addView(delete);

        return card;
    }

    private void addCardText(LinearLayout card, String text) {
        TextView item = new TextView(requireContext());
        item.setText(text);
        item.setTextColor(0xFFB8BED0);
        item.setTextSize(14);
        item.setPadding(0, 10, 0, 0);
        card.addView(item);
    }

    private void showAddDialog() {
        if (!isAdded()) return;

        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(40, 12, 40, 8);

        EditText title = field("Title *");
        EditText subject = field("Subject");
        EditText day = field("Day for class, e.g. MONDAY");
        EditText date = field("Exam date: YYYY-MM-DD");
        EditText start = field("Start time: HH:mm");
        EditText end = field("End time: HH:mm");
        EditText location = field("Classroom or exam hall");
        EditText notes = field("Notes (optional)");

        form.addView(title);
        form.addView(subject);

        if (selectedType.equals("CLASS")) {
            form.addView(day);
        } else {
            form.addView(date);
        }

        form.addView(start);
        form.addView(end);
        form.addView(location);
        form.addView(notes);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(selectedType.equals("CLASS")
                        ? "Add Class" : "Add Exam")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            String titleText =
                                    title.getText().toString().trim();
                            String subjectText =
                                    subject.getText().toString().trim();
                            String dayText =
                                    day.getText().toString().trim()
                                            .toUpperCase(Locale.ROOT);
                            String dateText =
                                    date.getText().toString().trim();
                            String startText =
                                    start.getText().toString().trim();
                            String endText =
                                    end.getText().toString().trim();

                            if (titleText.isEmpty()) {
                                title.setError("Enter a title");
                                return;
                            }

                            if (selectedType.equals("CLASS")
                                    && dayText.isEmpty()) {
                                day.setError("Enter the class day");
                                return;
                            }

                            if (selectedType.equals("EXAM")) {
                                try {
                                    LocalDate.parse(dateText);
                                } catch (Exception e) {
                                    date.setError(
                                            "Use YYYY-MM-DD format");
                                    return;
                                }
                            }

                            if (!validTime(startText)
                                    || !validTime(endText)) {
                                showMessage(
                                        "Enter time in HH:mm format");
                                return;
                            }

                            String startValue =
                                    normalizeTime(startText);
                            String endValue =
                                    normalizeTime(endText);

                            if (startValue.compareTo(endValue) >= 0) {
                                end.setError(
                                        "End time must be after start time");
                                return;
                            }

                            ScheduleRequest request =
                                    new ScheduleRequest(
                                            titleText,
                                            subjectText,
                                            selectedType,
                                            selectedType.equals("CLASS")
                                                    ? dayText : null,
                                            selectedType.equals("EXAM")
                                                    ? dateText : null,
                                            startValue,
                                            endValue,
                                            location.getText()
                                                    .toString().trim(),
                                            notes.getText()
                                                    .toString().trim()
                                    );

                            saveSchedule(request, dialog);
                        }));

        dialog.show();
    }

    private EditText field(String hint) {
        EditText input = new EditText(requireContext());
        input.setHint(hint);
        input.setSingleLine(true);
        return input;
    }

    private boolean validTime(String value) {
        if (value == null || !value.matches("\\d{2}:\\d{2}")) {
            return false;
        }

        try {
            java.time.LocalTime.parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String normalizeTime(String value) {
        return value + ":00";
    }

    private void saveSchedule(
            ScheduleRequest request, AlertDialog dialog) {

        apiService.createSchedule(request)
                .enqueue(new Callback<ScheduleResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ScheduleResponse> call,
                            @NonNull Response<ScheduleResponse> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {
                            dialog.dismiss();
                            showMessage("Schedule saved");
                            loadSchedules();
                        } else {
                            showMessage("Save failed. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ScheduleResponse> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        showMessage("Save failed: " + t.getMessage());
                    }
                });
    }

    private void confirmDelete(ScheduleResponse schedule) {
        if (!isAdded() || schedule.getId() == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete schedule?")
                .setMessage("Delete " + schedule.getTitle() + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteSchedule(schedule.getId()))
                .show();
    }

    private void deleteSchedule(Long id) {
        apiService.deleteSchedule(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Void> call,
                            @NonNull Response<Void> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            showMessage("Schedule deleted");
                            loadSchedules();
                        } else {
                            showMessage("Delete failed. HTTP "
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

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showMessage(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message,
                    Toast.LENGTH_SHORT).show();
        }
    }
}
