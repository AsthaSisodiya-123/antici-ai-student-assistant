
package com.anticai.studentassistant.fragments.ai;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.anticai.studentassistant.network.StudyPlanRequest;
import com.anticai.studentassistant.network.StudyPlanResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateStudyPlanFragment extends Fragment {

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
        // Required empty constructor
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

        apiService = RetrofitClient.getApiService(requireContext());

        configureSpinner(spDailyHours, dailyHoursOptions);
        configureSpinner(spSessionMinutes, sessionOptions);
        configureSpinner(spBreakMinutes, breakOptions);

        spDailyHours.setSelection(1);
        spSessionMinutes.setSelection(2);
        spBreakMinutes.setSelection(1);

        progressGeneratePlan.setVisibility(View.GONE);

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
                                new SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.US
                                );

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

        String[] subjects = input.split("[,;\\n]");
        String subject = subjects[0].trim();

        if (subject.isEmpty()) {
            etSubjects.setError("Enter a valid subject");
            return;
        }

        String examDate = etExamDates.getText().toString().trim();

        if (examDate.isEmpty()) {
            showMessage("Please select your exam date.");
            return;
        }

        int dailyHours = getSpinnerValue(spDailyHours, 2);
        int sessionMinutes = getSpinnerValue(spSessionMinutes, 45);
        int breakMinutes = getSpinnerValue(spBreakMinutes, 10);

        String difficulty =
                rgDifficulty.getCheckedRadioButtonId() == R.id.rbHard
                        ? "Hard" : "Easy";

        // Backend expects targetMinutes, not dailyStudyHours.
        int targetMinutes = dailyHours * 60;

        StudyPlanRequest request = new StudyPlanRequest(
                subject,
                subject,
                difficulty,
                examDate,
                targetMinutes
        );

        setLoading(true);
        tvGeneratedPlanResult.setText(
                "Saving your study plan..."
        );

        apiService.createStudyPlan(request).enqueue(
                new Callback<StudyPlanResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<StudyPlanResponse> call,
                            @NonNull Response<StudyPlanResponse> response) {

                        if (!isAdded()) return;

                        setLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null) {

                            StudyPlanResponse plan = response.body();

                            String result =
                                    "Study plan saved successfully!\n\n"
                                            + "Subject: " + plan.getSubject()
                                            + "\nTopic: " + plan.getTopic()
                                            + "\nDifficulty: " + plan.getDifficulty()
                                            + "\nExam date: " + plan.getExamDate()
                                            + "\nDaily target: " + dailyHours
                                            + " hours"
                                            + "\nStudy session: " + sessionMinutes
                                            + " minutes"
                                            + "\nBreak: " + breakMinutes
                                            + " minutes"
                                            + "\nStatus: " + plan.getStatus();

                            tvGeneratedPlanResult.setText(result);

                            showMessage("Study plan saved to the server.");

                        } else {

                            String error = "HTTP " + response.code();

                            try {
                                if (response.errorBody() != null) {
                                    error += ": "
                                            + response.errorBody().string();
                                }
                            } catch (IOException ignored) {
                                // Keep the HTTP error message.
                            }

                            tvGeneratedPlanResult.setText(
                                    "Could not save study plan."
                            );

                            showMessage(error);
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<StudyPlanResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        setLoading(false);

                        tvGeneratedPlanResult.setText(
                                "Connection failed. Please try again."
                        );

                        showMessage(
                                "Network error: " + t.getMessage()
                        );
                    }
                }
        );
    }

    private int getSpinnerValue(Spinner spinner, int fallback) {

        try {
            return Integer.parseInt(
                    spinner.getSelectedItem().toString()
            );
        } catch (Exception e) {
            return fallback;
        }
    }

    private void setLoading(boolean loading) {

        progressGeneratePlan.setVisibility(
                loading ? View.VISIBLE : View.GONE
        );

        btnGeneratePlan.setEnabled(!loading);
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
}
