package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private View emailContainer;
    private View otpContainer;
    private View resetContainer;
    private View successContainer;

    private EditText etEmail;
    private EditText etOtp;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private TextView btnBack;
    private TextView btnBackToLogin;

    private Button btnSendOtp;
    private Button btnVerifyOtp;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        initializeViews();

        setupClickListeners();

        showEmailStep();
    }

    private void initializeViews() {

        emailContainer = findViewById(R.id.emailContainer);
        otpContainer = findViewById(R.id.otpContainer);
        resetContainer = findViewById(R.id.resetContainer);
        successContainer = findViewById(R.id.successContainer);

        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnBack = findViewById(R.id.btnBack);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    private void setupClickListeners() {

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Send OTP
        btnSendOtp.setOnClickListener(v -> sendOtp());

        // Verify OTP
        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        // Reset password
        btnResetPassword.setOnClickListener(v -> resetPassword());

        // Back to login
        btnBackToLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ForgotPasswordActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);

            finish();
        });
    }

    private void sendOtp() {

        String email = etEmail.getText()
                .toString()
                .trim();

        if (email.isEmpty()) {

            etEmail.setError("Enter your email");

            etEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter a valid email");

            etEmail.requestFocus();

            return;
        }

        /*
         * Frontend demo:
         * Real OTP generation and email sending
         * will be connected with Spring Boot later.
         */

        Toast.makeText(
                this,
                "Verification code sent",
                Toast.LENGTH_SHORT
        ).show();

        showOtpStep();
    }

    private void verifyOtp() {

        String otp = etOtp.getText()
                .toString()
                .trim();

        if (otp.isEmpty()) {

            etOtp.setError("Enter verification code");

            etOtp.requestFocus();

            return;
        }

        if (otp.length() != 6) {

            etOtp.setError(
                    "Verification code must contain 6 digits"
            );

            etOtp.requestFocus();

            return;
        }

        /*
         * Frontend demo:
         * Real OTP verification will be handled
         * by the backend later.
         */

        Toast.makeText(
                this,
                "Verification successful",
                Toast.LENGTH_SHORT
        ).show();

        showResetStep();
    }

    private void resetPassword() {

        String newPassword =
                etNewPassword.getText()
                        .toString();

        String confirmPassword =
                etConfirmPassword.getText()
                        .toString();

        if (newPassword.isEmpty()) {

            etNewPassword.setError(
                    "Enter new password"
            );

            etNewPassword.requestFocus();

            return;
        }

        if (newPassword.length() < 8) {

            etNewPassword.setError(
                    "Password must contain at least 8 characters"
            );

            etNewPassword.requestFocus();

            return;
        }

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.setError(
                    "Confirm your password"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        /*
         * Frontend demo:
         * Actual password update will be connected
         * to Spring Boot + MySQL later.
         */

        Toast.makeText(
                this,
                "Password reset successfully",
                Toast.LENGTH_SHORT
        ).show();

        showSuccessStep();
    }

    private void showEmailStep() {

        emailContainer.setVisibility(View.VISIBLE);
        otpContainer.setVisibility(View.GONE);
        resetContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.GONE);
    }

    private void showOtpStep() {

        emailContainer.setVisibility(View.GONE);
        otpContainer.setVisibility(View.VISIBLE);
        resetContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.GONE);

        etOtp.requestFocus();
    }

    private void showResetStep() {

        emailContainer.setVisibility(View.GONE);
        otpContainer.setVisibility(View.GONE);
        resetContainer.setVisibility(View.VISIBLE);
        successContainer.setVisibility(View.GONE);

        etNewPassword.requestFocus();
    }

    private void showSuccessStep() {

        emailContainer.setVisibility(View.GONE);
        otpContainer.setVisibility(View.GONE);
        resetContainer.setVisibility(View.GONE);
        successContainer.setVisibility(View.VISIBLE);
    }
}