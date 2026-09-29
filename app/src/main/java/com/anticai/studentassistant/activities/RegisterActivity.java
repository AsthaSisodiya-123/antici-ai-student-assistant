package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;

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

        tvBack.setOnClickListener(v -> finish());

        tvLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

            finish();
        });

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


        if (name.isEmpty()) {
            etName.setError("Enter your name");
            etName.requestFocus();
            return;
        }


        if (email.isEmpty()) {
            etEmail.setError("Enter your email");
            etEmail.requestFocus();
            return;
        }


        if (mobile.isEmpty()) {
            etMobile.setError("Enter your mobile number");
            etMobile.requestFocus();
            return;
        }


        if (college.isEmpty()) {
            etCollege.setError("Enter your college");
            etCollege.requestFocus();
            return;
        }


        if (course.isEmpty()) {
            etCourse.setError("Enter your course");
            etCourse.requestFocus();
            return;
        }


        if (year.isEmpty()) {
            etYear.setError("Enter your year");
            etYear.requestFocus();
            return;
        }


        if (branch.isEmpty()) {
            etBranch.setError("Enter your branch");
            etBranch.requestFocus();
            return;
        }


        if (password.length() < 8) {
            etPassword.setError(
                    "Password must contain at least 8 characters"
            );

            etPassword.requestFocus();
            return;
        }


        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();
            return;
        }


        if (!cbTerms.isChecked()) {

            Toast.makeText(
                    this,
                    "Please accept the Terms and Privacy Policy",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Temporary frontend behaviour.
        // Backend API will be connected here.

        Toast.makeText(
                this,
                "Account details are valid",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(
                RegisterActivity.this,
                MainActivity.class
        );

        startActivity(intent);

        finish();
    }
}