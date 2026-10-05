package com.anticai.studentassistant.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.LoginResponse;
import com.anticai.studentassistant.network.RegisterRequest;
import com.anticai.studentassistant.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etEmail;
    private EditText etMobile;
    private EditText etCollege;
    private EditText etCourse;
    private EditText etYear;
    private EditText etBranch;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        initializeViews();

        TextView tvBack = findViewById(R.id.tvBack);
        TextView tvLogin = findViewById(R.id.tvLogin);
        TextView btnRegister = findViewById(R.id.btnRegister);

        // Back button
        tvBack.setOnClickListener(v -> finish());

        // Login link
        tvLogin.setOnClickListener(v -> {
            finish();
        });

        // Register button
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void initializeViews() {

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etCollege = findViewById(R.id.etCollege);
        etCourse = findViewById(R.id.etCourse);
        etYear = findViewById(R.id.etYear);
        etBranch = findViewById(R.id.etBranch);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbTerms = findViewById(R.id.cbTerms);
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String college = etCollege.getText().toString().trim();
        String course = etCourse.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String branch = etBranch.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Name validation
        if (name.isEmpty()) {
            etName.setError("Enter your name");
            etName.requestFocus();
            return;
        }

        // Email validation
        if (email.isEmpty()) {
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }

        // Mobile validation
        if (mobile.isEmpty()) {
            etMobile.setError("Enter your mobile number");
            etMobile.requestFocus();
            return;
        }

        // College validation
        if (college.isEmpty()) {
            etCollege.setError("Enter your college");
            etCollege.requestFocus();
            return;
        }

        // Course validation
        if (course.isEmpty()) {
            etCourse.setError("Enter your course");
            etCourse.requestFocus();
            return;
        }

        // Year validation
        if (year.isEmpty()) {
            etYear.setError("Enter your year");
            etYear.requestFocus();
            return;
        }

        // Branch validation
        if (branch.isEmpty()) {
            etBranch.setError("Enter your branch");
            etBranch.requestFocus();
            return;
        }

        // Password validation
        if (password.length() < 8) {
            etPassword.setError(
                    "Password must contain at least 8 characters"
            );
            etPassword.requestFocus();
            return;
        }

        // Confirm password validation
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(
                    "Passwords do not match"
            );
            etConfirmPassword.requestFocus();
            return;
        }

        // Terms validation
        if (!cbTerms.isChecked()) {
            Toast.makeText(
                    RegisterActivity.this,
                    "Please accept the Terms and Privacy Policy",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Show immediate confirmation that button worked
        Toast.makeText(
                RegisterActivity.this,
                "Sending registration...",
                Toast.LENGTH_SHORT
        ).show();

        // Create request
        RegisterRequest request = new RegisterRequest(
                name,
                email,
                password
        );

        // Create Retrofit API service
        ApiService apiService =
                RetrofitClient
                        .getInstance(RegisterActivity.this)
                        .create(ApiService.class);

        // Call backend
        Call<LoginResponse> call =
                apiService.register(request);

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(
                    Call<LoginResponse> call,
                    Response<LoginResponse> response
            ) {

                if (response.isSuccessful()) {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration successful",
                            Toast.LENGTH_LONG
                    ).show();

                    // Stay on this screen for now.
                    // We will add navigation after backend testing.

                } else {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration failed: HTTP "
                                    + response.code(),
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
                        RegisterActivity.this,
                        "Connection failed: "
                                + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}