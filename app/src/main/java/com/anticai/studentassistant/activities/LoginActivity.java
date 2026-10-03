package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.LoginRequest;
import com.anticai.studentassistant.network.LoginResponse;
import com.anticai.studentassistant.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Initialize input fields
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Initialize buttons
        TextView btnLogin = findViewById(R.id.btnLogin);
        TextView btnGoogle = findViewById(R.id.btnGoogle);
        TextView btnApple = findViewById(R.id.btnApple);
        TextView btnMobile = findViewById(R.id.btnMobile);

        // Initialize links
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Login
        btnLogin.setOnClickListener(v -> loginUser());

        // Google login
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(
                    LoginActivity.this,
                    "Google login will be connected later",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Apple login
        btnApple.setOnClickListener(v -> {
            Toast.makeText(
                    LoginActivity.this,
                    "Apple login will be connected later",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Mobile OTP
        btnMobile.setOnClickListener(v -> {
            Toast.makeText(
                    LoginActivity.this,
                    "Mobile OTP will be connected later",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Register
        tvRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });

        // Forgot Password
        tvForgotPassword.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });
    }

    private void loginUser() {

        // Get values
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validate email
        if (email.isEmpty()) {

            etEmail.setError("Enter your email");
            etEmail.requestFocus();

            return;
        }

        // Validate password
        if (password.isEmpty()) {

            etPassword.setError("Enter your password");
            etPassword.requestFocus();

            return;
        }

        // Show loading message
        Toast.makeText(
                LoginActivity.this,
                "Signing in...",
                Toast.LENGTH_SHORT
        ).show();

        // Create request
        LoginRequest request = new LoginRequest(
                email,
                password
        );

        // Create API service
        ApiService apiService =
                RetrofitClient
                        .getInstance()
                        .create(ApiService.class);

        // Call backend login API
        Call<LoginResponse> call =
                apiService.login(request);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(
                    Call<LoginResponse> call,
                    Response<LoginResponse> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    // Login successful
                    Toast.makeText(
                            LoginActivity.this,
                            "Login successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Open main screen
                    openMain();

                } else {

                    // Login failed
                    Toast.makeText(
                            LoginActivity.this,
                            "Invalid email or password",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<LoginResponse> call,
                    Throwable t
            ) {

                Toast.makeText(
                        LoginActivity.this,
                        "Connection failed: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void openMain() {

        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        finish();
    }
}

