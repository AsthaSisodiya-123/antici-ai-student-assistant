package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;


public class LoginActivity extends AppCompatActivity {

    ConstraintLayout login;

    TextView tvLoginUsername, tvLoginPassward, tvNewUserClickHere;
    AppCompatButton btnLogin;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvNewUserClickHere=findViewById(R.id.tvNewUserClickHere);



        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if (preferences.getBoolean("isLogin",false))
        {
            Intent intent=new Intent(this,HomeActivity.class);
            startActivity(intent);

        }
        login = findViewById(R.id.login);
        tvLoginUsername = findViewById(R.id.tietUsername);
        tvLoginPassward = findViewById(R.id.tietPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvLoginUsername.getText().toString().isEmpty()) {
                    tvLoginUsername.setError("Please Enter Username");
                } else if (tvLoginUsername.getText().toString().length() < 8) {
                    tvLoginUsername.setError("Username must contain at least 8 letters");
                } else if (tvLoginPassward.getText().toString().isEmpty()) {
                    tvLoginPassward.setError("Please Enter Password");
                } else if (tvLoginPassward.getText().toString().length() < 8) {
                    tvLoginPassward.setError("Password must contain at least 8 digits");
                } else if (!tvLoginPassward.getText().toString().matches(".*[A-Z].*")) {
                    tvLoginPassward.setError("Password contain at least 1 letter in uppercase");
                } else if (!tvLoginPassward.getText().toString().matches(".*[a-z].*")) {
                    tvLoginPassward.setError("Password contain at least 1 letter in lowecase");
                } else if (!tvLoginPassward.getText().toString().matches(".*[0-9].*")) {
                    tvLoginPassward.setError("Password contain at least 1 digit");
                } else if (!tvLoginPassward.getText().toString().matches(".*[@#$%^&+=!].*")) {
                    tvLoginPassward.setError("Password contain at least 1 special symbol");
                } else {
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    editor.putBoolean("isLogin",true).commit();
                }

            }

        });

        tvNewUserClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,Sign_InActivity.class);
                startActivity(intent);
                finish();
                
            }
        });

    }
}