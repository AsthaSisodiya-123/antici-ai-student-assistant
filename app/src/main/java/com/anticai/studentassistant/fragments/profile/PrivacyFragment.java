
package com.anticai.studentassistant.fragments.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.anticai.studentassistant.network.StudentResponse;
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
    private View cardChangePassword;
    private View cardForgotPassword;

    private TextView tvPersonalPrivacyStatus;
    private TextView tvPrivacyHistoryStatus;

    private LinearLayout historyContainer;
    private TextView historyCountView;
    private Button deleteSelectedButton;
    private Button clearHistoryButton;

    private final List<HistoryItem> historyItems = new ArrayList<>();
    private final Set<String> selectedItems = new HashSet<>();

    private boolean historyLoading;
    private boolean deletionInProgress;

    private int textPrimary = Color.rgb(240, 243, 250);
    private int textSecondary = Color.rgb(155, 167, 190);
    private int accentBlue = Color.rgb(77, 141, 255);
    private int cardColor = Color.rgb(20, 27, 43);
    private int borderColor = Color.rgb(48, 62, 88);

    public PrivacyFragment() {
    }

    @SuppressLint("MissingInflatedId")
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
        cardPrivacyHistory =
                root.findViewById(R.id.cardPrivacyHistory);
        cardChangePassword =
                root.findViewById(R.id.cardChangePassword);
        cardForgotPassword =
                root.findViewById(R.id.cardForgotPassword);

        tvPersonalPrivacyStatus =
                root.findViewById(R.id.tvPersonalPrivacyStatus);
        tvPrivacyHistoryStatus =
                root.findViewById(R.id.tvPrivacyHistoryStatus);

        if (cardPersonalInformation != null) {
            cardPersonalInformation.setOnClickListener(
                    v -> showPasswordDialog("Personal Information"));
        }

        if (cardPrivacyHistory != null) {
            cardPrivacyHistory.setOnClickListener(
                    v -> showPasswordDialog("History"));
        }

        if (cardChangePassword != null) {
            cardChangePassword.setOnClickListener(
                    v -> showChangePasswordDialog());
        }

        if (cardForgotPassword != null) {
            cardForgotPassword.setOnClickListener(
                    v -> showForgotPasswordDialog());
        }

        return root;
    }

    // =========================================================
    // FORGOT PASSWORD
    // =========================================================

    private void showForgotPasswordDialog() {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Forgot Password")
                .setMessage(
                        "A verification OTP will be sent to your " +
                                "registered email address.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send OTP",
                        (dialog, which) ->
                                fetchRegisteredEmailAndSendOtp())
                .show();
    }

    private void fetchRegisteredEmailAndSendOtp() {
        if (!isAdded()) return;

        toast("Fetching registered email...");

        RetrofitClient.getApiService(requireContext())
                .getCurrentStudent()
                .enqueue(new Callback<StudentResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<StudentResponse> call,
                            @NonNull Response<StudentResponse> response) {

                        if (!isAdded()) return;

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            toast("Unable to fetch your email. " +
                                    "Please log in again.");
                            return;
                        }

                        String email = response.body().getEmail();

                        if (email == null || email.trim().isEmpty()) {
                            toast("No registered email was found.");
                            return;
                        }

                        sendPasswordResetOtp(email);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<StudentResponse> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            toast("Unable to fetch your email. " +
                                    "Check your connection.");
                        }
                    }
                });
    }

    private void sendPasswordResetOtp(String email) {
        if (!isAdded()) return;

        if (email == null || email.trim().isEmpty()) {
            toast("Registered email not found.");
            return;
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        JsonObject request = new JsonObject();
        request.addProperty("email", normalizedEmail);

        toast("Requesting password reset OTP...");

        RetrofitClient.getApiService(requireContext())
                .forgotPassword(request)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded()) return;

                        if (!response.isSuccessful()) {
                            toast("OTP failed (HTTP "
                                    + response.code() + "): "
                                    + readApiError(
                                    response,
                                    "Unable to send OTP."));
                            return;
                        }

                        JsonObject result = response.body();

                        if (result == null) {
                            toast("Server returned an empty response.");
                            return;
                        }

                        boolean success = result.has("success")
                                && !result.get("success").isJsonNull()
                                && result.get("success").getAsBoolean();

                        if (success) {
                            showOtpVerificationDialog(normalizedEmail);
                        } else {
                            toast(readSuccessMessage(
                                    result, "Unable to send OTP."));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {

                        if (!isAdded()) return;

                        toast("Network error: "
                                + safeErrorMessage(t));
                    }
                });
    }

    private void showOtpVerificationDialog(String email) {
        if (!isAdded()) return;

        LinearLayout form = createForm();

        TextView info = new TextView(requireContext());
        info.setText("Enter the 6-digit OTP sent to your registered email.");
        info.setTextColor(textSecondary);
        info.setTextSize(14);
        form.addView(info);

        EditText otpInput = new EditText(requireContext());
        otpInput.setSingleLine(true);
        otpInput.setHint("6-digit OTP");
        otpInput.setTextColor(textPrimary);
        otpInput.setHintTextColor(textSecondary);
        otpInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        otpInput.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(6)
        });
        form.addView(otpInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Verify OTP")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Resend OTP", null)
                .setPositiveButton("Verify", null)
                .create();

        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        String otp =
                                otpInput.getText().toString().trim();

                        if (!otp.matches("\\d{6}")) {
                            otpInput.setError("Enter the 6-digit OTP");
                            return;
                        }

                        verifyPasswordResetOtp(email, otp, dialog);
                    });

            dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setOnClickListener(v -> {
                        dialog.dismiss();
                        sendPasswordResetOtp(email);
                    });
        });

        dialog.show();
    }

    private void verifyPasswordResetOtp(
            String email,
            String otp,
            AlertDialog dialog) {

        if (!isAdded() || !dialog.isShowing()) return;

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(false);
        button.setText("Verifying...");

        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("otp", otp);

        RetrofitClient.getApiService(requireContext())
                .verifyForgotPasswordOtp(request)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        button.setEnabled(true);
                        button.setText("Verify");

                        JsonObject result = response.body();

                        if (response.isSuccessful()
                                && result != null
                                && result.has("resetToken")
                                && !result.get("resetToken").isJsonNull()
                                && !result.get("resetToken")
                                .getAsString().trim().isEmpty()) {

                            String token = result.get("resetToken")
                                    .getAsString();

                            dialog.dismiss();
                            showNewPasswordDialog(email, token);
                        } else {
                            toast(readApiError(
                                    response,
                                    "OTP verification failed."));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        button.setEnabled(true);
                        button.setText("Verify");
                        toast("Connection error while verifying OTP.");
                    }
                });
    }

    private void showNewPasswordDialog(
            String email,
            String resetToken) {

        if (!isAdded()) return;

        LinearLayout form = createForm();

        EditText newPassword =
                createPasswordField("New password (minimum 8 characters)");
        EditText confirmPassword =
                createPasswordField("Confirm new password");

        form.addView(newPassword);
        addInputSpacing(form);
        form.addView(confirmPassword);

        CheckBox showPasswords = createShowPasswordsCheckBox();
        form.addView(showPasswords);

        showPasswords.setOnCheckedChangeListener((button, checked) -> {
            setPasswordVisibility(newPassword, checked);
            setPasswordVisibility(confirmPassword, checked);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Create New Password")
                .setMessage("Choose a strong password for your account.")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Reset Password", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String password =
                                    newPassword.getText().toString();
                            String confirmation =
                                    confirmPassword.getText().toString();

                            if (password.length() < 8) {
                                newPassword.setError(
                                        "Use at least 8 characters");
                                return;
                            }

                            if (password.length() > 72) {
                                newPassword.setError(
                                        "Password must not exceed 72 characters");
                                return;
                            }

                            if (!password.equals(confirmation)) {
                                confirmPassword.setError(
                                        "Passwords do not match");
                                return;
                            }

                            submitForgotPasswordReset(
                                    email, resetToken, password, dialog);
                        }));

        dialog.show();
    }

    private void submitForgotPasswordReset(
            String email,
            String resetToken,
            String newPassword,
            AlertDialog dialog) {

        if (!isAdded() || !dialog.isShowing()) return;

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(false);
        button.setText("Resetting...");

        JsonObject request = new JsonObject();
        request.addProperty("email", email);
        request.addProperty("resetToken", resetToken);
        request.addProperty("newPassword", newPassword);

        RetrofitClient.getApiService(requireContext())
                .resetPassword(request)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            toast("Password reset successfully. " +
                                    "Log in using your new password.");
                        } else {
                            button.setEnabled(true);
                            button.setText("Reset Password");

                            toast("Reset failed (HTTP "
                                    + response.code() + "): "
                                    + readApiError(
                                    response,
                                    "Password reset failed."));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        button.setEnabled(true);
                        button.setText("Reset Password");
                        toast("Network error: " + safeErrorMessage(t));
                    }
                });
    }

    // =========================================================
    // ERROR HANDLING
    // =========================================================

    private String readApiError(
            Response<?> response,
            String fallback) {

        try {
            if (response.errorBody() != null) {
                String errorText = response.errorBody().string();

                if (!errorText.trim().isEmpty()) {
                    JsonElement parsed =
                            JsonParser.parseString(errorText);

                    if (parsed.isJsonObject()) {
                        JsonObject object = parsed.getAsJsonObject();

                        if (object.has("message")
                                && !object.get("message").isJsonNull()) {
                            return object.get("message").getAsString();
                        }

                        if (object.has("error")
                                && !object.get("error").isJsonNull()) {
                            return object.get("error").getAsString();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Use fallback.
        }

        return fallback;
    }

    private String readSuccessMessage(
            JsonObject result,
            String fallback) {

        if (result.has("message")
                && !result.get("message").isJsonNull()) {
            return result.get("message").getAsString();
        }

        return fallback;
    }

    private String safeErrorMessage(Throwable throwable) {
        String message = throwable.getMessage();
        return message == null || message.trim().isEmpty()
                ? "Unable to contact the server."
                : message;
    }

    // =========================================================
    // CHANGE PASSWORD
    // =========================================================

    private void showChangePasswordDialog() {
        if (!isAdded()) return;

        LinearLayout form = createForm();

        EditText currentPassword =
                createPasswordField("Current password");
        EditText newPassword =
                createPasswordField("New password (minimum 8 characters)");
        EditText confirmPassword =
                createPasswordField("Confirm new password");

        form.addView(currentPassword);
        addInputSpacing(form);
        form.addView(newPassword);
        addInputSpacing(form);
        form.addView(confirmPassword);

        CheckBox showPasswords = createShowPasswordsCheckBox();
        form.addView(showPasswords);

        showPasswords.setOnCheckedChangeListener((button, checked) -> {
            setPasswordVisibility(currentPassword, checked);
            setPasswordVisibility(newPassword, checked);
            setPasswordVisibility(confirmPassword, checked);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setMessage("Enter your current password and your new password.")
                .setView(form)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Update Password", null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String oldValue =
                                    currentPassword.getText().toString();
                            String newValue =
                                    newPassword.getText().toString();
                            String confirmValue =
                                    confirmPassword.getText().toString();

                            if (oldValue.trim().isEmpty()) {
                                currentPassword.setError(
                                        "Enter your current password");
                                return;
                            }

                            if (newValue.length() < 8) {
                                newPassword.setError(
                                        "Use at least 8 characters");
                                return;
                            }

                            if (newValue.length() > 72) {
                                newPassword.setError(
                                        "Password must not exceed 72 characters");
                                return;
                            }

                            if (oldValue.equals(newValue)) {
                                newPassword.setError(
                                        "Choose a different password");
                                return;
                            }

                            if (!newValue.equals(confirmValue)) {
                                confirmPassword.setError(
                                        "Passwords do not match");
                                return;
                            }

                            submitChangePassword(oldValue, newValue, dialog);
                        }));

        dialog.show();
    }

    private void submitChangePassword(
            String oldPassword,
            String newPassword,
            AlertDialog dialog) {

        if (!isAdded() || !dialog.isShowing()) return;

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(false);
        button.setText("Updating...");

        JsonObject request = new JsonObject();
        request.addProperty("oldPassword", oldPassword);
        request.addProperty("newPassword", newPassword);

        RetrofitClient.getApiService(requireContext())
                .changePassword(request)
                .enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonObject> call,
                            @NonNull Response<JsonObject> response) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        if (response.isSuccessful()) {
                            dialog.dismiss();
                            toast("Password changed successfully.");
                        } else {
                            button.setEnabled(true);
                            button.setText("Update Password");

                            toast("Change failed (HTTP "
                                    + response.code() + "): "
                                    + readApiError(
                                    response,
                                    "Password change failed."));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonObject> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        button.setEnabled(true);
                        button.setText("Update Password");
                        toast("Connection error: " + safeErrorMessage(t));
                    }
                });
    }

    // =========================================================
    // SHARED PASSWORD INPUTS
    // =========================================================

    private LinearLayout createForm() {
        LinearLayout form = new LinearLayout(requireContext());
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), dp(4));
        return form;
    }

    private EditText createPasswordField(String hint) {
        EditText field = new EditText(requireContext());
        field.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        field.setSingleLine(true);
        field.setHint(hint);
        field.setTextColor(textPrimary);
        field.setHintTextColor(textSecondary);
        field.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        field.setPadding(dp(12), dp(12), dp(12), dp(12));
        return field;
    }

    private CheckBox createShowPasswordsCheckBox() {
        CheckBox checkBox = new CheckBox(requireContext());
        checkBox.setText("Show passwords");
        checkBox.setTextColor(textPrimary);
        checkBox.setButtonTintList(
                ColorStateList.valueOf(accentBlue));
        return checkBox;
    }

    private void addInputSpacing(LinearLayout parent) {
        View space = new View(requireContext());
        parent.addView(space, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(8)));
    }

    private void setPasswordVisibility(EditText field, boolean visible) {
        int cursor = Math.max(0, field.getSelectionStart());

        field.setInputType(visible
                ? InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                : InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        field.setSelection(Math.min(cursor, field.length()));
    }

    // =========================================================
    // PASSWORD VERIFICATION FOR PRIVACY SECTIONS
    // =========================================================

    private void showPasswordDialog(String section) {
        if (!isAdded()) return;

        if ("History".equals(section) && historyLoading) {
            toast("History is loading. Please wait.");
            return;
        }

        EditText input = createPasswordField(
                "Enter your account password");

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
            String section,
            String password,
            AlertDialog dialog) {

        Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        button.setEnabled(false);

        RetrofitClient.getApiService(requireContext())
                .verifyPrivacyPassword(
                        new PrivacyPasswordRequest(password))
                .enqueue(new Callback<PrivacyPasswordResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<PrivacyPasswordResponse> call,
                            @NonNull Response<PrivacyPasswordResponse> response) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        button.setEnabled(true);

                        PrivacyPasswordResponse result = response.body();

                        if (response.isSuccessful()
                                && result != null
                                && result.isVerified()) {

                            dialog.dismiss();

                            if ("History".equals(section)) {
                                openHistory();
                            } else {
                                loadPersonalInformation();
                            }
                        } else {
                            toast("Verification failed (HTTP "
                                    + response.code() + "): "
                                    + readApiError(
                                    response,
                                    "Incorrect password or session expired."));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PrivacyPasswordResponse> call,
                            @NonNull Throwable t) {

                        if (!isAdded() || !dialog.isShowing()) return;

                        button.setEnabled(true);
                        toast("Unable to verify password. Check your connection.");
                    }
                });
    }

    // =========================================================
    // PERSONAL INFORMATION
    // =========================================================

    private void loadPersonalInformation() {
        if (!isAdded()) return;

        RetrofitClient.getApiService(requireContext())
                .getPrivacyProfile()
                .enqueue(new Callback<JsonElement>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful()
                                && response.body() != null) {

                            if (tvPersonalPrivacyStatus != null) {
                                tvPersonalPrivacyStatus.setText(
                                        "Information loaded");
                            }

                            showJsonDialog(
                                    "Personal Information", response.body());
                        } else {
                            toast("Unable to load personal information. HTTP "
                                    + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            toast("Connection error loading profile.");
                        }
                    }
                });
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
                        (dialog, which) -> showPasswordDialog(title))
                .show();
    }

    private String prettyJson(JsonElement data) {
        if (data == null || data.isJsonNull()) {
            return "No information available.";
        }

        if (!data.isJsonObject()) {
            return data.toString();
        }

        StringBuilder output = new StringBuilder();
        JsonObject object = data.getAsJsonObject();

        for (String key : object.keySet()) {
            String lower = key.toLowerCase(Locale.ROOT);

            if (lower.contains("password")
                    || lower.contains("token")
                    || lower.contains("secret")) {
                continue;
            }

            JsonElement value = object.get(key);

            output.append(friendlyLabel(key))
                    .append("\n")
                    .append(value == null || value.isJsonNull()
                            ? "Not available"
                            : value.toString())
                    .append("\n\n");
        }

        return output.length() == 0
                ? "No information available."
                : output.toString();
    }

    private String friendlyLabel(String key) {
        String spaced = key
                .replaceAll("([a-z])([A-Z])", "$1 $2")
                .replace('_', ' ')
                .replace('-', ' ');

        if (spaced.isEmpty()) return spaced;

        return spaced.substring(0, 1).toUpperCase(Locale.ROOT)
                + spaced.substring(1);
    }

    // =========================================================
    // STUDY HISTORY
    // =========================================================

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
                "Select records to delete, or clear all study history.");
        description.setTextColor(textSecondary);
        description.setTextSize(14);
        root.addView(description);

        historyCountView = new TextView(requireContext());
        historyCountView.setTextColor(accentBlue);
        historyCountView.setPadding(0, dp(12), 0, dp(8));
        root.addView(historyCountView);

        historyContainer = new LinearLayout(requireContext());
        historyContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(historyContainer);

        deleteSelectedButton = new Button(requireContext());
        deleteSelectedButton.setText("Delete Selected");
        root.addView(deleteSelectedButton);

        clearHistoryButton = new Button(requireContext());
        clearHistoryButton.setText("Clear All History");
        root.addView(clearHistoryButton);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Your History")
                .setView(scroll)
                .setPositiveButton("Close", null)
                .create();

        dialog.setOnDismissListener(ignored -> {
            historyContainer = null;
            historyCountView = null;
            deleteSelectedButton = null;
            clearHistoryButton = null;
        });

        deleteSelectedButton.setOnClickListener(v -> deleteSelected());
        clearHistoryButton.setOnClickListener(v -> confirmClearAll());

        dialog.show();
        loadHistory();
    }

    private void loadHistory() {
        if (!isAdded() || historyContainer == null) return;

        historyLoading = true;
        setHistoryStatus("Loading history...");
        historyContainer.removeAllViews();

        TextView loading = new TextView(requireContext());
        loading.setText("Loading your records...");
        loading.setTextColor(textSecondary);
        historyContainer.addView(loading);

        loadTasks();
    }

    private void loadTasks() {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyTasks()
                .enqueue(new Callback<JsonElement>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {

                        if (!isAdded()) return;

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            historyFailed("Unable to load tasks. HTTP "
                                    + response.code());
                            return;
                        }

                        addItems("TASK", "Task", response.body());
                        loadPlans();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {

                        if (isAdded()) historyFailed("Could not load tasks.");
                    }
                });
    }

    private void loadPlans() {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyStudyPlans()
                .enqueue(new Callback<JsonElement>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {

                        if (!isAdded()) return;

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            historyFailed("Unable to load study plans. HTTP "
                                    + response.code());
                            return;
                        }

                        addItems("STUDY_PLAN", "Study Plan", response.body());
                        loadSessions();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            historyFailed("Could not load study plans.");
                        }
                    }
                });
    }

    private void loadSessions() {
        RetrofitClient.getApiService(requireContext())
                .getPrivacyStudySessions()
                .enqueue(new Callback<JsonElement>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<JsonElement> call,
                            @NonNull Response<JsonElement> response) {

                        if (!isAdded()) return;

                        if (!response.isSuccessful()
                                || response.body() == null) {
                            historyFailed("Unable to load sessions. HTTP "
                                    + response.code());
                            return;
                        }

                        addItems("STUDY_SESSION",
                                "Study Session", response.body());

                        historyLoading = false;
                        setHistoryStatus("History loaded");
                        renderHistory();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<JsonElement> call,
                            @NonNull Throwable t) {

                        if (isAdded()) {
                            historyFailed("Could not load study sessions.");
                        }
                    }
                });
    }

    private void addItems(
            String type,
            String label,
            JsonElement data) {

        if (data == null || data.isJsonNull()) return;

        JsonArray array;

        if (data.isJsonArray()) {
            array = data.getAsJsonArray();
        } else if (data.isJsonObject()
                && data.getAsJsonObject().has("data")
                && data.getAsJsonObject().get("data").isJsonArray()) {
            array = data.getAsJsonObject().getAsJsonArray("data");
        } else {
            return;
        }

        for (JsonElement element : array) {
            if (!element.isJsonObject()) continue;

            JsonObject object = element.getAsJsonObject();

            if (!object.has("id") || object.get("id").isJsonNull()) {
                continue;
            }

            try {
                long id = object.get("id").getAsLong();

                String title = firstValue(
                        object, "title", "subject", "topic", "name");

                if (title.isEmpty()) title = label;

                String detail = firstValue(
                        object, "dueDate", "examDate",
                        "startTime", "createdAt", "status");

                historyItems.add(new HistoryItem(
                        type, id, label, title, detail));

            } catch (Exception ignored) {
                // Ignore malformed records.
            }
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

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dp(10);

            historyContainer.addView(card, params);

            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(item.typeLabel + " • " + item.title);
            if (!item.detail.isEmpty()) {
                checkBox.append("\n" + item.detail);
            }
            checkBox.setTextColor(textPrimary);
            checkBox.setButtonTintList(
                    ColorStateList.valueOf(accentBlue));
            checkBox.setChecked(selectedItems.contains(item.key()));

            checkBox.setOnCheckedChangeListener((button, checked) -> {
                if (checked) {
                    selectedItems.add(item.key());
                } else {
                    selectedItems.remove(item.key());
                }
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

    private void deleteSelected() {
        if (selectedItems.isEmpty()) {
            toast("Select at least one record.");
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Selected?")
                .setMessage("This permanently deletes "
                        + selectedItems.size() + " selected records.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete",
                        (dialog, which) -> performDeleteSelected())
                .show();
    }

    private void performDeleteSelected() {
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
                            historyItems.clear();
                            selectedItems.clear();
                            loadHistory();
                        } else {
                            toast("Delete failed (HTTP "
                                    + response.code() + ").");
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

    private void confirmClearAll() {
        if (historyItems.isEmpty()) {
            toast("There is no history to clear.");
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Clear All History?")
                .setMessage(
                        "All tasks, study plans and study sessions " +
                                "will be permanently deleted. Your account remains.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Clear All",
                        (dialog, which) -> performClearAll())
                .show();
    }

    private void performClearAll() {
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
                            historyItems.clear();
                            selectedItems.clear();
                            toast("All study history cleared.");
                            loadHistory();
                        } else {
                            toast("Clear history failed (HTTP "
                                    + response.code() + ").");
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

        if (historyContainer == null || !isAdded()) return;

        historyContainer.removeAllViews();

        TextView error = new TextView(requireContext());
        error.setText(message);
        error.setTextColor(textSecondary);
        historyContainer.addView(error);

        Button retry = new Button(requireContext());
        retry.setText("Retry");
        retry.setOnClickListener(v -> loadHistory());
        historyContainer.addView(retry);
    }

    private void setHistoryStatus(String message) {
        if (tvPrivacyHistoryStatus != null) {
            tvPrivacyHistoryStatus.setText(message);
        }
    }

    // =========================================================
    // THEME AND UTILITIES
    // =========================================================

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
            // Keep fallback colors.
        }
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
            Toast.makeText(requireContext(),
                    message, Toast.LENGTH_LONG).show();
        }
    }

    private static class HistoryItem {
        final String type;
        final long id;
        final String typeLabel;
        final String title;
        final String detail;

        HistoryItem(
                String type,
                long id,
                String typeLabel,
                String title,
                String detail) {
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
        cardChangePassword = null;
        cardForgotPassword = null;

        tvPersonalPrivacyStatus = null;
        tvPrivacyHistoryStatus = null;

        historyContainer = null;
        historyCountView = null;
        deleteSelectedButton = null;
        clearHistoryButton = null;

        historyItems.clear();
        selectedItems.clear();

        historyLoading = false;
        deletionInProgress = false;
    }
}
