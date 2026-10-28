
package com.anticai.studentassistant.fragments.profile;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.PrivacyPasswordRequest;
import com.anticai.studentassistant.network.PrivacyPasswordResponse;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrivacyFragment extends Fragment {

    private View cardPersonalInformation;
    private View cardPrivacyHistory;
    private TextView tvPersonalPrivacyStatus;
    private TextView tvPrivacyHistoryStatus;

    private LinearLayout historyContainer;
    private LinearLayout historyActions;
    private TextView historyCountView;
    private Button deleteSelectedButton;
    private Button clearHistoryButton;

    private final List<HistoryItem> historyItems = new ArrayList<>();
    private final Set<String> selectedItems = new HashSet<>();

    private boolean historyLoading = false;
    private boolean deletionInProgress = false;

    private int textPrimary = Color.rgb(240, 243, 250);
    private int textSecondary = Color.rgb(155, 167, 190);
    private int accentBlue = Color.rgb(77, 141, 255);
    private int cardColor = Color.rgb(20, 27, 43);
    private int borderColor = Color.rgb(48, 62, 88);

    public PrivacyFragment() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(
                R.layout.fragment_privacy, container, false);

        loadThemeColors();

        View back = root.findViewById(R.id.btnPrivacyBack);
        if (back != null) {
            back.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager()
                            .popBackStack());
        }

        cardPersonalInformation =
                root.findViewById(R.id.cardPersonalInformation);
        cardPrivacyHistory = root.findViewById(R.id.cardPrivacyHistory);
        tvPersonalPrivacyStatus =
                root.findViewById(R.id.tvPersonalPrivacyStatus);
        tvPrivacyHistoryStatus =
                root.findViewById(R.id.tvPrivacyHistoryStatus);

        if (cardPersonalInformation != null) {
            cardPersonalInformation.setOnClickListener(v ->
                    showPasswordDialog("Personal Information"));
        }

        if (cardPrivacyHistory != null) {
            cardPrivacyHistory.setOnClickListener(v ->
                    showPasswordDialog("History"));
        }

        return root;
    }

    private void loadThemeColors() {
        if (!isAdded()) return;

        try {
            textPrimary = getResources().getColor(
                    R.color.text_primary, requireContext().getTheme());
            textSecondary = getResources().getColor(
                    R.color.text_secondary, requireContext().getTheme());
            accentBlue = getResources().getColor(
                    R.color.accent_blue, requireContext().getTheme());
            cardColor = getResources().getColor(
                    R.color.surface_color, requireContext().getTheme());
            borderColor = getResources().getColor(
                    R.color.border_color, requireContext().getTheme());
        } catch (Exception ignored) {
            // Fallback colors remain available.
        }
    }

    private void showPasswordDialog(String section) {
        if (!isAdded()) return;

        if (section.equals("History") && historyLoading) {
            toast("History is loading. Please wait.");
            return;
        }

        EditText input = new EditText(requireContext());
        input.setSingleLine(true);
        input.setHint("Enter your account password");
        input.setTextColor(textPrimary);
        input.setHintTextColor(textSecondary);
        input.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setPadding(dp(16), dp(12), dp(16), dp(12));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Verify Your Identity")
                .setMessage("Enter your password to access " + section + ".")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Verify", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String password = input.getText().toString();

                            if (password.trim().isEmpty()) {
                                input.setError("Enter your password");
                                return;
                            }

                            verifyPassword(section, password, dialog);
                        }));

        dialog.show();
    }

    private void verifyPassword(
            String section, String password, AlertDialog dialog) {

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        RetrofitClient.getApiService(requireContext())
                .verifyPrivacyPassword(new PrivacyPasswordRequest(password))
                .enqueue(new Callback<PrivacyPasswordResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<PrivacyPasswordResponse> call,
                            @NonNull Response<PrivacyPasswordResponse> response) {

                        if (!isAdded()) return;

                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(true);

                        PrivacyPasswordResponse result = response.body();

                        if (response.isSuccessful()
                                && result != null && result.isVerified()) {
                            dialog.dismiss();

                            if (section.equals("History")) {
                                openHistory();
                            } else {
                                loadPersonalInformation();
                            }
                        } else {
                            toast(response.code() == 401
                                    ? "Incorrect password or session expired."
                                    : "Password verification failed. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PrivacyPasswordResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(true);
                        toast("Could not verify password. Check your connection.");
                    }
                });
    }

    private void loadPersonalInformation() {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyProfile()
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            if (tvPersonalPrivacyStatus != null) {
                                tvPersonalPrivacyStatus.setText("✓ Information loaded");
                            }

                            showJsonDialog("Personal Information", response.body());
                        } else {
                            toast("Unable to load personal information. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {
                        if (isAdded()) toast("Connection error loading profile.");
                    }
                });
    }

    private void openHistory() {
        if (!isAdded() || historyLoading) return;

        historyItems.clear();
        selectedItems.clear();

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), dp(16));
        scroll.addView(root);

        TextView description = new TextView(requireContext());
        description.setText(
                "Select records to delete, or clear all your study history.");
        description.setTextColor(textSecondary);
        description.setTextSize(14);
        root.addView(description);

        historyCountView = new TextView(requireContext());
        historyCountView.setTextColor(accentBlue);
        historyCountView.setTextSize(13);
        historyCountView.setPadding(0, dp(12), 0, dp(8));
        root.addView(historyCountView);

        historyContainer = new LinearLayout(requireContext());
        historyContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(historyContainer);

        historyActions = new LinearLayout(requireContext());
        historyActions.setOrientation(LinearLayout.VERTICAL);
        historyActions.setPadding(0, dp(12), 0, 0);

        deleteSelectedButton = new Button(requireContext());
        deleteSelectedButton.setText("Delete Selected");
        historyActions.addView(deleteSelectedButton);

        clearHistoryButton = new Button(requireContext());
        clearHistoryButton.setText("Clear All History");
        historyActions.addView(clearHistoryButton);

        root.addView(historyActions);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Your History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .create();

        dialog.setOnDismissListener(ignored -> {
            historyContainer = null;
            historyActions = null;
            historyCountView = null;
            deleteSelectedButton = null;
            clearHistoryButton = null;
        });

        deleteSelectedButton.setOnClickListener(v -> deleteSelected(dialog));
        clearHistoryButton.setOnClickListener(v -> confirmClearAll(dialog));

        dialog.show();
        loadHistory(dialog);
    }

    private void loadHistory(AlertDialog dialog) {
        if (!isAdded()) return;

        historyLoading = true;
        setHistoryStatus("Loading history...");

        historyContainer.removeAllViews();
        TextView loading = new TextView(requireContext());
        loading.setText("Loading your records...");
        loading.setTextColor(textSecondary);
        historyContainer.addView(loading);

        loadTasks(dialog);
    }

    private void loadTasks(AlertDialog dialog) {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyTasks()
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful() || response.body() == null) {
                            historyFailed("Unable to load tasks. HTTP "
                                    + response.code());
                            return;
                        }

                        addItems("TASK", "Task", response.body());
                        loadPlans(dialog);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {
                        if (isAdded()) historyFailed("Could not load tasks.");
                    }
                });
    }

    private void loadPlans(AlertDialog dialog) {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyStudyPlans()
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful() || response.body() == null) {
                            historyFailed("Unable to load study plans. HTTP "
                                    + response.code());
                            return;
                        }

                        addItems("STUDY_PLAN", "Study Plan", response.body());
                        loadSessions(dialog);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {
                        if (isAdded()) historyFailed("Could not load study plans.");
                    }
                });
    }

    private void loadSessions(AlertDialog dialog) {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyStudySessions()
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful() || response.body() == null) {
                            historyFailed("Unable to load sessions. HTTP "
                                    + response.code());
                            return;
                        }

                        addItems("STUDY_SESSION", "Study Session", response.body());
                        historyLoading = false;
                        setHistoryStatus("✓ History loaded");
                        renderHistory();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {
                        if (isAdded()) historyFailed("Could not load study sessions.");
                    }
                });
    }

    private void addItems(
            String type, String label, JsonElement data) {

        if (data == null || data.isJsonNull()) return;

        JsonArray array;

        if (data.isJsonArray()) {
            array = data.getAsJsonArray();
        } else {
            // Some APIs may wrap their array in a "data" property.
            if (data.isJsonObject() && data.getAsJsonObject().has("data")
                    && data.getAsJsonObject().get("data").isJsonArray()) {
                array = data.getAsJsonObject().getAsJsonArray("data");
            } else {
                return;
            }
        }

        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;

            JsonObject object = element.getAsJsonObject();

            if (!object.has("id") || object.get("id").isJsonNull()) {
                continue;
            }

            long id;
            try {
                id = object.get("id").getAsLong();
            } catch (Exception ignored) {
                continue;
            }

            String title = firstValue(object,
                    "title", "subject", "topic", "name");
            if (title.isEmpty()) title = label;

            String detail = firstValue(object,
                    "dueDate", "examDate", "startTime", "createdAt", "status");

            historyItems.add(new HistoryItem(
                    type, id, label, title, detail));
        }
    }

    private String firstValue(JsonObject object, String... keys) {
        for (String key : keys) {
            if (object.has(key) && !object.get(key).isJsonNull()) {
                String value = object.get(key).getAsString();
                if (!value.trim().isEmpty()) return value;
            }
        }
        return "";
    }

    private void renderHistory() {
        if (!isAdded() || historyContainer == null) return;

        historyContainer.removeAllViews();

        if (historyItems.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("No history records found.");
            empty.setTextColor(textSecondary);
            empty.setPadding(0, dp(20), 0, dp(20));
            historyContainer.addView(empty);
        }

        for (HistoryItem item : historyItems) {
            LinearLayout card = new LinearLayout(requireContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(12), dp(8), dp(12), dp(8));
            card.setBackground(createCardBackground());

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            cardParams.bottomMargin = dp(10);
            historyContainer.addView(card, cardParams);

            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(item.typeLabel + "  •  " + item.title);
            checkBox.setTextColor(textPrimary);
            checkBox.setButtonTintList(
                    android.content.res.ColorStateList.valueOf(accentBlue));
            checkBox.setChecked(selectedItems.contains(item.key()));

            if (!item.detail.isEmpty()) {
                checkBox.append("\n" + item.detail);
            }

            checkBox.setOnCheckedChangeListener((button, checked) -> {
                if (checked) selectedItems.add(item.key());
                else selectedItems.remove(item.key());

                updateHistoryCount();
            });

            card.addView(checkBox);
        }

        updateHistoryCount();
    }

    private void updateHistoryCount() {
        if (historyCountView != null) {
            historyCountView.setText(
                    historyItems.size() + " records • "
                            + selectedItems.size() + " selected");
        }

        if (deleteSelectedButton != null) {
            deleteSelectedButton.setEnabled(
                    !selectedItems.isEmpty() && !deletionInProgress);
        }

        if (clearHistoryButton != null) {
            clearHistoryButton.setEnabled(
                    !historyItems.isEmpty() && !deletionInProgress);
        }
    }

    private void deleteSelected(AlertDialog historyDialog) {
        if (selectedItems.isEmpty()) {
            toast("Select at least one record.");
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Selected?")
                .setMessage("This permanently deletes "
                        + selectedItems.size() + " selected records.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (d, w) ->
                        performDeleteSelected(historyDialog))
                .show();
    }

    private void performDeleteSelected(AlertDialog historyDialog) {
        JsonObject request = new JsonObject();
        JsonArray records = new JsonArray();

        for (HistoryItem item : historyItems) {
            if (!selectedItems.contains(item.key())) continue;

            JsonObject record = new JsonObject();
            record.addProperty("type", item.type);
            record.addProperty("id", item.id);
            records.add(record);
        }

        request.add("records", records);
        setDeletionState(true);

        RetrofitClient.getApiService(requireContext())
                .deleteSelectedHistory(request)
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {
                        if (!isAdded()) return;

                        setDeletionState(false);

                        if (response.isSuccessful()) {
                            toast("Selected history deleted.");
                            loadHistory(historyDialog);
                        } else {
                            toast("Delete failed. HTTP " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setDeletionState(false);
                        toast("Connection error. Please try again.");
                    }
                });
    }

    private void confirmClearAll(AlertDialog historyDialog) {
        if (historyItems.isEmpty()) {
            toast("There is no history to clear.");
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All History?")
                .setMessage("All your tasks, study plans and study sessions "
                        + "will be permanently deleted. Your account will remain.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear All", (d, w) ->
                        performClearAll(historyDialog))
                .show();
    }

    private void performClearAll(AlertDialog historyDialog) {
        setDeletionState(true);

        RetrofitClient.getApiService(requireContext())
                .clearAllHistory()
                .enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {
                        if (!isAdded()) return;

                        setDeletionState(false);

                        if (response.isSuccessful()) {
                            selectedItems.clear();
                            toast("All study history cleared.");
                            loadHistory(historyDialog);
                        } else {
                            toast("Clear history failed. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {
                        if (!isAdded()) return;
                        setDeletionState(false);
                        toast("Connection error. Please try again.");
                    }
                });
    }

    private void setDeletionState(boolean busy) {
        deletionInProgress = busy;
        updateHistoryCount();
    }

    private void historyFailed(String message) {
        historyLoading = false;
        setHistoryStatus("Unable to load history");
        toast(message);

        if (historyContainer != null) {
            historyContainer.removeAllViews();

            TextView error = new TextView(requireContext());
            error.setText(message);
            error.setTextColor(textSecondary);
            historyContainer.addView(error);

            Button retry = new Button(requireContext());
            retry.setText("Retry");
            retry.setOnClickListener(v -> {
                if (historyActions != null) historyActions.setVisibility(View.VISIBLE);
                loadHistory(null);
            });
            historyContainer.addView(retry);
        }
    }

    private void setHistoryStatus(String message) {
        if (tvPrivacyHistoryStatus != null) {
            tvPrivacyHistoryStatus.setText(message);
        }
    }

    private void showJsonDialog(String title, JsonElement data) {
        if (!isAdded()) return;

        ScrollView scroll = new ScrollView(requireContext());
        TextView content = new TextView(requireContext());
        content.setText(prettyJson(data));
        content.setTextColor(textPrimary);
        content.setTextSize(14);
        content.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(content);

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setView(scroll)
                .setPositiveButton("Close", null)
                .setNeutralButton("Verify Again",
                        (d, w) -> showPasswordDialog(title))
                .show();
    }

    private String prettyJson(JsonElement data) {
        if (data == null || data.isJsonNull()) return "No information available.";

        if (data.isJsonObject()) {
            StringBuilder output = new StringBuilder();
            for (String key : data.getAsJsonObject().keySet()) {
                String lower = key.toLowerCase(Locale.ROOT);
                if (lower.contains("password") || lower.contains("token")
                        || lower.contains("secret")) continue;

                JsonElement value = data.getAsJsonObject().get(key);
                output.append(friendlyLabel(key)).append("\n")
                        .append(value == null || value.isJsonNull()
                                ? "Not available" : value.toString())
                        .append("\n\n");
            }
            return output.length() == 0
                    ? "No information available." : output.toString();
        }

        return data.toString();
    }

    private String friendlyLabel(String key) {
        String spaced = key.replaceAll("([a-z])([A-Z])", "$1 $2")
                .replace('_', ' ').replace('-', ' ');
        if (spaced.isEmpty()) return spaced;
        return spaced.substring(0, 1).toUpperCase(Locale.ROOT)
                + spaced.substring(1);
    }

    private GradientDrawable createCardBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(cardColor);
        drawable.setCornerRadius(dp(14));
        drawable.setStroke(dp(1), borderColor);
        return drawable;
    }

    private int dp(float value) {
        if (!isAdded()) return (int) value;
        return (int) (value * requireContext()
                .getResources().getDisplayMetrics().density + 0.5f);
    }

    private void toast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message,
                    Toast.LENGTH_LONG).show();
        }
    }

    private static class HistoryItem {
        final String type;
        final long id;
        final String typeLabel;
        final String title;
        final String detail;

        HistoryItem(String type, long id, String typeLabel,
                    String title, String detail) {
            this.type = type;
            this.id = id;
            this.typeLabel = typeLabel;
            this.title = title;
            this.detail = detail;
        }

        String key() {
            return type + ":" + id;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cardPersonalInformation = null;
        cardPrivacyHistory = null;
        tvPersonalPrivacyStatus = null;
        tvPrivacyHistoryStatus = null;

        historyContainer = null;
        historyActions = null;
        historyCountView = null;
        deleteSelectedButton = null;
        clearHistoryButton = null;

        historyItems.clear();
        selectedItems.clear();
        historyLoading = false;
        deletionInProgress = false;
    }
}
