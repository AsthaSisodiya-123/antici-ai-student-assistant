
package com.anticai.studentassistant.fragments.ai;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateStudyPlanFragment extends Fragment {

    private static final String PREFS_NAME = "anticai_saved_plans";
    private static final String PLANS_KEY = "plans";

    private EditText etSubjects;
    private EditText etExamDates;
    private RadioGroup rgDifficulty;
    private Spinner spDailyHours;
    private Spinner spSessionMinutes;
    private Spinner spBreakMinutes;
    private Button btnGeneratePlan;
    private ProgressBar progressGeneratePlan;
    private TextView tvGeneratedPlanResult;

    private ApiService apiService;

    private final String[] dailyHoursOptions =
            {"1", "2", "3", "4", "5", "6", "7", "8"};

    private final String[] sessionOptions =
            {"25", "30", "45", "50", "60", "90"};

    private final String[] breakOptions =
            {"5", "10", "15", "20", "30"};

    public CreateStudyPlanFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_create_study_plan,
                container,
                false
        );

        etSubjects = view.findViewById(R.id.etSubjects);
        etExamDates = view.findViewById(R.id.etExamDates);
        rgDifficulty = view.findViewById(R.id.rgDifficulty);
        spDailyHours = view.findViewById(R.id.spDailyHours);
        spSessionMinutes = view.findViewById(R.id.spSessionMinutes);
        spBreakMinutes = view.findViewById(R.id.spBreakMinutes);
        btnGeneratePlan = view.findViewById(R.id.btnGeneratePlan);
        progressGeneratePlan =
                view.findViewById(R.id.progressGeneratePlan);
        tvGeneratedPlanResult =
                view.findViewById(R.id.tvGeneratedPlanResult);

        apiService = RetrofitClient.getInstance(requireContext())
                .create(ApiService.class);

        configureSpinner(spDailyHours, dailyHoursOptions);
        configureSpinner(spSessionMinutes, sessionOptions);
        configureSpinner(spBreakMinutes, breakOptions);

        spDailyHours.setSelection(1);
        spSessionMinutes.setSelection(2);
        spBreakMinutes.setSelection(1);

        configureDatePicker();

        btnGeneratePlan.setOnClickListener(v -> generatePlan());

        return view;
    }

    private void configureSpinner(Spinner spinner, String[] values) {
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

    private void configureDatePicker() {
        etExamDates.setFocusable(false);
        etExamDates.setClickable(true);

        etExamDates.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog picker = new DatePickerDialog(
                    requireContext(),
                    (datePicker, year, month, day) -> {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, day);

                        SimpleDateFormat formatter =
                                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                        etExamDates.setText(
                                formatter.format(chosen.getTime())
                        );
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            picker.show();
        });
    }

    private void generatePlan() {
        String input = etSubjects.getText().toString().trim();

        if (input.isEmpty()) {
            etSubjects.setError("Enter at least one subject");
            etSubjects.requestFocus();
            return;
        }

        List<String> subjects = parseSubjects(input);

        if (subjects.isEmpty()) {
            etSubjects.setError("Enter a valid subject");
            return;
        }

        final int dailyHours = spinnerInt(spDailyHours, 2);
        final int sessionMinutes = spinnerInt(spSessionMinutes, 45);
        final int breakMinutes = spinnerInt(spBreakMinutes, 10);

        final String examDate = etExamDates.getText().toString().trim();

        final String difficulty =
                rgDifficulty.getCheckedRadioButtonId() == R.id.rbHard
                        ? "Hard" : "Easy";

        JsonArray subjectArray = new JsonArray();

        for (String subject : subjects) {
            JsonObject item = new JsonObject();
            item.addProperty("name", subject);
            item.addProperty("difficulty", difficulty);

            if (!examDate.isEmpty()) {
                item.addProperty("examDate", examDate);
            }

            subjectArray.add(item);
        }

        JsonObject request = new JsonObject();
        request.add("subjects", subjectArray);
        request.addProperty("dailyStudyHours", dailyHours);
        request.addProperty("sessionMinutes", sessionMinutes);
        request.addProperty("breakMinutes", breakMinutes);

        setLoading(true);
        tvGeneratedPlanResult.setText("Generating your study plan...");

        apiService.generateStudyPlan(request).enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ResponseBody> call,
                            @NonNull Response<ResponseBody> response) {

                        if (!isAdded()) return;

                        setLoading(false);

                        if (!response.isSuccessful()) {
                            tvGeneratedPlanResult.setText(
                                    "Plan generation failed. HTTP "
                                            + response.code()
                            );
                            return;
                        }

                        savePlans(
                                subjects,
                                examDate,
                                difficulty,
                                dailyHours,
                                sessionMinutes,
                                breakMinutes
                        );

                        tvGeneratedPlanResult.setText(
                                "Study plan created successfully!\n\n"
                                        + "Subjects: "
                                        + TextUtils.join(", ", subjects)
                                        + "\nExam date: "
                                        + (examDate.isEmpty()
                                        ? "Not set" : examDate)
                                        + "\nDifficulty: " + difficulty
                                        + "\nDaily hours: " + dailyHours
                                        + "\nSession: " + sessionMinutes
                                        + " minutes\nBreak: " + breakMinutes
                                        + " minutes\n\n"
                                        + "Open Study Plan to start."
                        );

                        Toast.makeText(
                                requireContext(),
                                "Study plan saved",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ResponseBody> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        setLoading(false);
                        tvGeneratedPlanResult.setText(
                                "Could not connect to the server: "
                                        + t.getMessage()
                        );
                    }
                }
        );
    }

    private List<String> parseSubjects(String input) {
        List<String> subjects = new ArrayList<>();

        for (String part : input.split("[,;\\n]")) {
            String subject = part.trim();

            if (!subject.isEmpty() && !subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        return subjects;
    }

    private int spinnerInt(Spinner spinner, int fallback) {
        try {
            return Integer.parseInt(
                    spinner.getSelectedItem().toString()
            );
        } catch (Exception e) {
            return fallback;
        }
    }

    private void savePlans(
            List<String> subjects,
            String examDate,
            String difficulty,
            int dailyHours,
            int sessionMinutes,
            int breakMinutes) {

        String oldJson = requireContext()
                .getSharedPreferences(PREFS_NAME, 0)
                .getString(PLANS_KEY, "[]");

        JsonArray plans;

        try {
            plans = JsonParser.parseString(oldJson).getAsJsonArray();
        } catch (Exception e) {
            plans = new JsonArray();
        }

        long createdAt = System.currentTimeMillis();

        for (String subject : subjects) {
            JsonObject plan = new JsonObject();

            plan.addProperty(
                    "id",
                    subject + "_" + createdAt + "_" + plans.size()
            );
            plan.addProperty("subject", subject);
            plan.addProperty("topic", subject);
            plan.addProperty("examDate", examDate);
            plan.addProperty("difficulty", difficulty);
            plan.addProperty("dailyStudyHours", dailyHours);
            plan.addProperty("durationMinutes", sessionMinutes);
            plan.addProperty("breakMinutes", breakMinutes);
            plan.addProperty("completed", false);
            plan.addProperty("createdAt", createdAt);

            plans.add(plan);
        }

        requireContext()
                .getSharedPreferences(PREFS_NAME, 0)
                .edit()
                .putString(PLANS_KEY, plans.toString())
                .apply();
    }

    private void setLoading(boolean loading) {
        progressGeneratePlan.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );
        btnGeneratePlan.setEnabled(!loading);
    }
}
