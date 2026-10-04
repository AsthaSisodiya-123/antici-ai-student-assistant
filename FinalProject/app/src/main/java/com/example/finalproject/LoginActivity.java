package com.example.finalproject;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.Database.DBHelper;


public class LoginActivity extends AppCompatActivity {

    EditText login_username, login_passward;
    Button btn_login;
    TextView login_newuser;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // If already logged in
        if (preferences.getBoolean("isLogin", false)) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        login_username = findViewById(R.id.etUsername);
        login_passward = findViewById(R.id.etPassword);
        btn_login = findViewById(R.id.btnLogin);
        login_newuser = findViewById(R.id.login_newUser);

        dbHelper = new DBHelper(this);

        // Login Button Click
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strusername = login_username.getText().toString().trim();
                String strpassword = login_passward.getText().toString().trim();

                if (strusername.isEmpty()) {
                    login_username.setError("Please Enter Username");

                } else if (strusername.length() < 8) {
                    login_username.setError("Username must contain at least 8 letters");

                } else if (strpassword.isEmpty()) {
                    login_passward.setError("Please Enter Password");

                } else if (strpassword.length() < 8) {
                    login_passward.setError("Password must contain at least 8 characters");

                } else if (!strpassword.matches(".*[A-Z].*")) {
                    login_passward.setError("Password must contain at least 1 uppercase letter");

                } else if (!strpassword.matches(".*[a-z].*")) {
                    login_passward.setError("Password must contain at least 1 lowercase letter");

                } else if (!strpassword.matches(".*[0-9].*")) {
                    login_passward.setError("Password must contain at least 1 digit");

                } else if (!strpassword.matches(".*[@#$%^&+=!].*")) {
                    login_passward.setError("Password must contain at least 1 special symbol");

                } else {
                    validateUser(strusername, strpassword);
                }
            }
        });

        // New User Click (Role Selection Dialog)
        login_newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoleDialog();
            }
        });
    }

    // Validate Login
    private void validateUser(String strusername, String strpassword) {

        if (dbHelper.loginUser(strusername, strpassword)) {

            Toast.makeText(this, "Login Successfully Done", Toast.LENGTH_SHORT).show();

            editor.putBoolean("isLogin", true);
            editor.putString("username", strusername);
            editor.apply();

            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();

        } else {
            Toast.makeText(this, "User Does Not Exist", Toast.LENGTH_SHORT).show();
        }
    }

    // Role Selection Dialog
    private void showRoleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Select Your Role");

        String[] roles = {"Student", "Admin"};

        builder.setItems(roles, (dialog, which) -> {

            if (which == 0) {
                // Student Selected
                Intent intent = new Intent(LoginActivity.this, StudentRegistrationActivity.class);
                startActivity(intent);

            } else {
                // Admin Selected
                Intent intent = new Intent(LoginActivity.this, AdminRegistrationActivity.class);
                startActivity(intent);
            }
        });

        builder.setCancelable(true);
        builder.show();
    }
}
