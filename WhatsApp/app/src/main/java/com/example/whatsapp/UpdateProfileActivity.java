package com.example.whatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class UpdateProfileActivity extends AppCompatActivity {

    TextInputEditText updateProfileMobileNo,updateProfileEmail,updateProfileUsername,updateProfileAbout;
    Button btnSaveChange;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();

        updateProfileUsername=findViewById(R.id.updateProfileUsername);
        updateProfileMobileNo=findViewById(R.id.updateProfileMobileNo);
        updateProfileEmail=findViewById(R.id.updateProfileEmail);
        updateProfileAbout=findViewById(R.id.updateProfileAbout);
        btnSaveChange=findViewById(R.id.btnSaveChange);

        updateProfileUsername.setText(preferences.getString("username",""));
        updateProfileMobileNo.setText(preferences.getString("mobile_no",""));
        updateProfileEmail.setText(preferences.getString("email",""));
        updateProfileAbout.setText(preferences.getString("about",""));

        btnSaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateProfileUsername.getText().toString().isEmpty()) {
                    updateProfileUsername.setError("Enter Your Name");
                } else if (updateProfileUsername.getText().toString().length() < 8) {
                    updateProfileUsername.setError("Name must contain at least 8 letters");
                } else if (updateProfileMobileNo.getText().toString().isEmpty()) {
                    updateProfileMobileNo.setError("Enter Your Mobile No");
                } else if (updateProfileMobileNo.getText().toString().length() != 10) {
                    updateProfileMobileNo.setError("Mobile No must contain 10 digits");
                } else if (updateProfileEmail.getText().toString().isEmpty()) {
                    updateProfileEmail.setError("Enter Your Email Address");
                } else if (!updateProfileEmail.getText().toString().contains("@gmail.com")) {
                    updateProfileEmail.setError("Invalid Email Address");
                } else if (updateProfileUsername.getText().toString().isEmpty()) {
                    updateProfileUsername.setError("Enter Your Username");
                } else if (updateProfileUsername.getText().toString().length() < 8) {
                    updateProfileUsername.setError("Username must contain at least 8 letters");
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Changes Save", Toast.LENGTH_SHORT).show();

                    editor.putString("username",updateProfileUsername.getText().toString()).commit();
                    editor.putString("mobile_no",updateProfileMobileNo.getText().toString()).commit();
                    editor.putString("email",updateProfileEmail.getText().toString()).commit();
                    editor.putString("about",updateProfileAbout.getText().toString()).commit();

                    Intent intent=new Intent(UpdateProfileActivity.this, MyProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}