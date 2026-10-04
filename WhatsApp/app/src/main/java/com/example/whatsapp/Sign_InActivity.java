package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

public class Sign_InActivity extends AppCompatActivity {
    LinearLayout sign_in;
    TextInputEditText tvSignInUserName,tvSignINMobileNo,tvSignInEmail,tvSignInPassword;
AppCompatButton btnSignIn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();

        sign_in=findViewById(R.id.Sign_in);
        btnSignIn=findViewById(R.id.btnSignIn);
        tvSignInUserName=findViewById(R.id.tvSignInUsername);
        tvSignINMobileNo=findViewById(R.id.tvSignInMobileNo);
        tvSignInEmail=findViewById(R.id.tvSignInEmail);
        tvSignInPassword=findViewById(R.id.tVSignInPassword);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSignInUserName.getText().toString().isEmpty()) {
                    tvSignInUserName.setError("Enter Your Name");
                } else if (tvSignInUserName.getText().toString().length() < 8) {
                    tvSignInUserName.setError("Name must contain at least 8 letters");
                } else if (tvSignINMobileNo.getText().toString().isEmpty()) {
                    tvSignINMobileNo.setError("Enter Your Mobile No");
                } else if (tvSignINMobileNo.getText().toString().length() != 10) {
                    tvSignINMobileNo.setError("Mobile No must contain 10 digits");
                } else if (tvSignInEmail.getText().toString().isEmpty()) {
                    tvSignInEmail.setError("Enter Your Email Address");
                } else if (!tvSignInEmail.getText().toString().contains("@gmail.com")) {
                    tvSignInEmail.setError("Invalid Email Address");
                }else if (tvSignInPassword.getText().toString().isEmpty()) {
                    tvSignInPassword.setError("Enter your Password");
                } else if (tvSignInPassword.getText().toString().length() < 8) {
                    tvSignInPassword.setError("Password must contain at least 8 digits");
                } else if (!tvSignInPassword.getText().toString().matches(".*[A-Z].*")) {
                    tvSignInPassword.setError("Password contain at least 1 letter in uppercase");
                } else if (!tvSignInPassword.getText().toString().matches(".*[a-z].*")) {
                    tvSignInPassword.setError("Password contain at least 1 letter in lowecase");
                } else if (!tvSignInPassword.getText().toString().matches(".*[0-9].*")) {
                    tvSignInPassword.setError("Password contain at least 1 digit");
                } else if (!tvSignInPassword.getText().toString().matches(".*[@#$%^&+=!].*")) {
                    tvSignInPassword.setError("Password contain at least 1 special symbol");
                } else {
                    Intent intent=new Intent(Sign_InActivity.this,OnBoard2Activity.class);
                    startActivity(intent);
                    finish();
                    editor.putString("username",tvSignInUserName.getText().toString()).commit();
                    editor.putString("mobile_no",tvSignINMobileNo.getText().toString()).commit();
                    editor.putString("email",tvSignInEmail.getText().toString()).commit();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}