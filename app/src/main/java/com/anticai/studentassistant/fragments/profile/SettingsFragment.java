package com.anticai.studentassistant.fragments.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class SettingsFragment extends Fragment {

    private Switch switchNotifications;
    private Switch switchReminders;
    private RadioGroup radioStudyGoal;
    private RadioGroup radioTheme;
    private Button btnSaveSettings;
    private TextView btnSettingsBack;

    private SharedPreferences preferences;

    public SettingsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_settings,
                container,
                false
        );

        preferences = requireContext().getSharedPreferences(
                "AnticiAISettings",
                Context.MODE_PRIVATE
        );

        initializeViews(view);
        loadPreferences();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        switchNotifications =
                view.findViewById(R.id.switchNotifications);

        switchReminders =
                view.findViewById(R.id.switchReminders);

        radioStudyGoal =
                view.findViewById(R.id.radioStudyGoal);

        radioTheme =
                view.findViewById(R.id.radioTheme);

        btnSaveSettings =
                view.findViewById(R.id.btnSaveSettings);

        btnSettingsBack =
                view.findViewById(R.id.btnSettingsBack);
    }

    private void loadPreferences() {
        switchNotifications.setChecked(
                preferences.getBoolean("notifications", true)
        );

        switchReminders.setChecked(
                preferences.getBoolean("reminders", true)
        );

        int goal = preferences.getInt("studyGoal", 60);

        if (goal == 30) {
            radioStudyGoal.check(R.id.goal30);
        } else if (goal == 120) {
            radioStudyGoal.check(R.id.goal120);
        } else if (goal == 180) {
            radioStudyGoal.check(R.id.goal180);
        } else {
            radioStudyGoal.check(R.id.goal60);
        }

        String theme = preferences.getString("theme", "dark");

        if ("light".equals(theme)) {
            radioTheme.check(R.id.radioLight);
        } else if ("system".equals(theme)) {
            radioTheme.check(R.id.radioSystem);
        } else {
            radioTheme.check(R.id.radioDark);
        }
    }

    private void setupClickListeners() {
        btnSettingsBack.setOnClickListener(v -> goBack());

        btnSaveSettings.setOnClickListener(v -> savePreferences());
    }

    private void savePreferences() {
        int selectedGoalId = radioStudyGoal.getCheckedRadioButtonId();

        if (selectedGoalId == -1) {
            Toast.makeText(
                    requireContext(),
                    "Please select a daily study goal.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int studyGoal;

        if (selectedGoalId == R.id.goal30) {
            studyGoal = 30;
        } else if (selectedGoalId == R.id.goal120) {
            studyGoal = 120;
        } else if (selectedGoalId == R.id.goal180) {
            studyGoal = 180;
        } else {
            studyGoal = 60;
        }

        int selectedThemeId = radioTheme.getCheckedRadioButtonId();

        if (selectedThemeId == -1) {
            Toast.makeText(
                    requireContext(),
                    "Please select an app theme.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String theme;

        if (selectedThemeId == R.id.radioLight) {
            theme = "light";
        } else if (selectedThemeId == R.id.radioSystem) {
            theme = "system";
        } else {
            theme = "dark";
        }

        boolean saved = preferences.edit()
                .putBoolean(
                        "notifications",
                        switchNotifications.isChecked()
                )
                .putBoolean(
                        "reminders",
                        switchReminders.isChecked()
                )
                .putInt("studyGoal", studyGoal)
                .putString("theme", theme)
                .commit();

        if (!saved) {
            Toast.makeText(
                    requireContext(),
                    "Could not save settings. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        applyTheme(theme);

        Toast.makeText(
                requireContext(),
                "Settings saved successfully!",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void applyTheme(String theme) {
        int mode;

        switch (theme) {
            case "light":
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;

            case "system":
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;

            default:
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
        }

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    private void goBack() {
        if (!isAdded()) {
            return;
        }

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}
