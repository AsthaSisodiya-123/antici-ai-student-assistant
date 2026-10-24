
package com.anticai.studentassistant.fragments.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private CompoundButton switchNotifications;
    private CompoundButton switchReminders;
    private RadioGroup radioStudyGoal;
    private RadioGroup radioTheme;
    private Button btnSaveSettings;

    private SharedPreferences preferences;
    private ApiService apiService;

    private boolean loadingSettings = false;

    private static final String PREFS_NAME = "AnticiAISettings";
    private static final String KEY_THEME = "themeMode";
    private static final String KEY_GOAL = "dailyStudyGoal";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferences = requireContext().getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);

        apiService = RetrofitClient.getApiService(requireContext());

        switchNotifications = view.findViewById(R.id.switchNotifications);
        switchReminders = view.findViewById(R.id.switchReminders);
        radioStudyGoal = view.findViewById(R.id.radioStudyGoal);
        radioTheme = view.findViewById(R.id.radioTheme);
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings);

        View backButton = view.findViewById(R.id.btnSettingsBack);
        if (backButton != null) {
            backButton.setOnClickListener(v ->
                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack());
        }

        loadLocalSettings();
        setupListeners();
        loadServerSettings();
    }

    private void loadLocalSettings() {
        loadingSettings = true;

        switchNotifications.setChecked(
                preferences.getBoolean("notificationsEnabled", true));

        switchReminders.setChecked(
                preferences.getBoolean("remindersEnabled", true));

        int goal = preferences.getInt(KEY_GOAL, 60);
        selectStudyGoal(goal);

        String theme = preferences.getString(KEY_THEME, "dark");
        selectTheme(theme);

        loadingSettings = false;

        // Apply the saved theme immediately.
        applyTheme(theme);
    }

    private void setupListeners() {
        radioTheme.setOnCheckedChangeListener((group, checkedId) -> {
            if (loadingSettings) {
                return;
            }

            String theme = getSelectedTheme();
            if (theme == null) {
                return;
            }

            preferences.edit()
                    .putString(KEY_THEME, theme)
                    .apply();

            // Change the app theme immediately.
            applyTheme(theme);

            // Also save the selection on the backend.
            syncSettingsWithServer(false);
        });

        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void applyTheme(String theme) {
        int requestedMode;

        switch (theme) {
            case "light":
                requestedMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;

            case "dark":
                requestedMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;

            default:
                requestedMode =
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }

        if (AppCompatDelegate.getDefaultNightMode() != requestedMode) {
            AppCompatDelegate.setDefaultNightMode(requestedMode);
        }
    }

    private String getSelectedTheme() {
        int selectedId = radioTheme.getCheckedRadioButtonId();

        if (selectedId == R.id.radioDark) {
            return "dark";
        } else if (selectedId == R.id.radioLight) {
            return "light";
        } else if (selectedId == R.id.radioSystem) {
            return "system";
        }

        return null;
    }

    private void selectTheme(String theme) {
        if ("light".equals(theme)) {
            radioTheme.check(R.id.radioLight);
        } else if ("system".equals(theme)) {
            radioTheme.check(R.id.radioSystem);
        } else {
            radioTheme.check(R.id.radioDark);
        }
    }

    private void selectStudyGoal(int minutes) {
        switch (minutes) {
            case 30:
                radioStudyGoal.check(R.id.goal30);
                break;
            case 120:
                radioStudyGoal.check(R.id.goal120);
                break;
            case 180:
                radioStudyGoal.check(R.id.goal180);
                break;
            default:
                radioStudyGoal.check(R.id.goal60);
                break;
        }
    }

    private int getSelectedStudyGoal() {
        int selectedId = radioStudyGoal.getCheckedRadioButtonId();

        if (selectedId == R.id.goal30) return 30;
        if (selectedId == R.id.goal120) return 120;
        if (selectedId == R.id.goal180) return 180;

        return 60;
    }

    private void saveSettings() {
        String theme = getSelectedTheme();

        if (theme == null) {
            theme = "dark";
        }

        int dailyGoal = getSelectedStudyGoal();
        boolean notifications = switchNotifications.isChecked();
        boolean reminders = switchReminders.isChecked();

        preferences.edit()
                .putBoolean("notificationsEnabled", notifications)
                .putBoolean("remindersEnabled", reminders)
                .putString(KEY_THEME, theme)
                .putInt(KEY_GOAL, dailyGoal)
                .apply();

        applyTheme(theme);
        syncSettingsWithServer(true);
    }

    private void syncSettingsWithServer(boolean showMessage) {
        if (!isAdded()) {
            return;
        }

        JsonObject body = new JsonObject();
        body.addProperty(
                "notificationsEnabled", switchNotifications.isChecked());
        body.addProperty(
                "remindersEnabled", switchReminders.isChecked());

        String theme = getSelectedTheme();
        if (theme == null) {
            theme = preferences.getString(KEY_THEME, "dark");
        }
        body.addProperty("themeMode", theme);

        apiService.updateSettings(body).enqueue(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful()) {
                            if (showMessage) {
                                Toast.makeText(
                                        requireContext(),
                                        "Settings saved successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else if (showMessage) {
                            Toast.makeText(
                                    requireContext(),
                                    "Could not save settings. Server error: "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {

                        if (isAdded() && showMessage) {
                            Toast.makeText(
                                    requireContext(),
                                    "Settings saved on this device, but server sync failed",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
    }

    private void loadServerSettings() {
        apiService.getSettings().enqueue(
                new Callback<JsonObject>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded() || !response.isSuccessful()
                                || response.body() == null) {
                            return;
                        }

                        JsonObject data = response.body();

                        loadingSettings = true;

                        if (data.has("notificationsEnabled")
                                && !data.get("notificationsEnabled").isJsonNull()) {
                            switchNotifications.setChecked(
                                    data.get("notificationsEnabled").getAsBoolean());
                        }

                        if (data.has("remindersEnabled")
                                && !data.get("remindersEnabled").isJsonNull()) {
                            switchReminders.setChecked(
                                    data.get("remindersEnabled").getAsBoolean());
                        }

                        String theme = "dark";
                        if (data.has("themeMode")
                                && !data.get("themeMode").isJsonNull()) {
                            theme = data.get("themeMode").getAsString();
                        }

                        if (!"light".equals(theme)
                                && !"dark".equals(theme)
                                && !"system".equals(theme)) {
                            theme = "dark";
                        }

                        selectTheme(theme);

                        preferences.edit()
                                .putBoolean(
                                        "notificationsEnabled",
                                        switchNotifications.isChecked())
                                .putBoolean(
                                        "remindersEnabled",
                                        switchReminders.isChecked())
                                .putString(KEY_THEME, theme)
                                .apply();

                        loadingSettings = false;
                        applyTheme(theme);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {
                        // Local settings remain available offline.
                    }
                });
    }
}
