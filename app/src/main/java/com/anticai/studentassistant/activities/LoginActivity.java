package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        TextView btnLogin = findViewById(R.id.btnLogin);
        TextView btnGoogle = findViewById(R.id.btnGoogle);
        TextView btnApple = findViewById(R.id.btnApple);
        TextView btnMobile = findViewById(R.id.btnMobile);
        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Login
        btnLogin.setOnClickListener(v -> openMain());

        // Google
        btnGoogle.setOnClickListener(v -> {
            // Google authentication will be connected later
        });

        // Apple
        btnApple.setOnClickListener(v -> {
            // Apple authentication will be connected later
        });

        // Mobile OTP
        btnMobile.setOnClickListener(v -> {
            // Mobile OTP screen will be connected later
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

    private void openMain() {

        Intent intent = new Intent(
                LoginActivity.this,
                MainActivity.class
        );

        startActivity(intent);
        finish();
    }
}