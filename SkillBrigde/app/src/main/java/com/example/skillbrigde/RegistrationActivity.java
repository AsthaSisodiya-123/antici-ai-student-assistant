package com.example.skillbrigde;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

        EditText etName, etMobile, etEmail, etUsername, etPassword;
        RadioButton rbStudent, rbAdmin;
        Button btnSignup;
        TextView tvLogin;

        FirebaseAuth mAuth;
        DatabaseReference userRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registration);

            // Initialize views
            etName = findViewById(R.id.etName);
            etMobile = findViewById(R.id.etMobile);
            etEmail = findViewById(R.id.etEmail);
            etUsername = findViewById(R.id.etUsername);
            etPassword = findViewById(R.id.etPassword);

            rbStudent = findViewById(R.id.rbStudent);
            rbAdmin = findViewById(R.id.rbAdmin);

            btnSignup = findViewById(R.id.btnSignup);
            tvLogin = findViewById(R.id.tvLogin);

            // Firebase
            mAuth = FirebaseAuth.getInstance();
            userRef = FirebaseDatabase.getInstance().getReference("users");

            btnSignup.setOnClickListener(v -> registerUser());

            tvLogin.setOnClickListener(v ->
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));
        }

        private void registerUser() {

            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            String role = rbAdmin.isChecked() ? "admin" : "student";

            if (name.isEmpty() || mobile.isEmpty() || email.isEmpty()
                    || username.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        String uid = mAuth.getCurrentUser().getUid();

                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("name", name);
                        userMap.put("mobile", mobile);
                        userMap.put("email", email);
                        userMap.put("username", username);
                        userMap.put("role", role);

                        userRef.child(uid).setValue(userMap)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
