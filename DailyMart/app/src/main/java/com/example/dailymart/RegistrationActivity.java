package com.example.dailymart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymart.Database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegistrationActivity extends BaseActivity {

    TextInputEditText tietRegName, tietRegMobileNo,tietRegEmail,tietRegUsername,tietRegPassword;
    Button btnConfirm;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    DBHelper dbHelper; // ✅ Declare DBHelper


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#449984")));

        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        dbHelper = new DBHelper(this); // ✅ Initialize DBHelper

        tietRegName=findViewById(R.id.tietRegName);
        tietRegMobileNo=findViewById(R.id.tietRegMobileNo);

        tietRegEmail=findViewById(R.id.tietRegEmail);
        tietRegUsername=findViewById(R.id.tietRegUsername);
        tietRegPassword=findViewById(R.id.tietRegPassword);
        btnConfirm=findViewById(R.id.btnConfirm);




        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strname = tietRegName.getText().toString();
                String strmobile = tietRegMobileNo.getText().toString();
                String stremail = tietRegEmail.getText().toString();
                String strusername = tietRegUsername.getText().toString();
                String strpassword = tietRegPassword.getText().toString();
                if (tietRegName.getText().toString().isEmpty()) {
                    tietRegName.setError("Enter Your Name");
                } else if (tietRegName.getText().toString().length() < 8) {
                    tietRegName.setError("Name must contain at least 8 letters");
                } else if (tietRegMobileNo.getText().toString().isEmpty()) {
                    tietRegMobileNo.setError("Enter Your Mobile No");
                } else if (tietRegMobileNo.getText().toString().length() != 10) {
                    tietRegMobileNo.setError("Mobile No must contain 10 digits");
                } else if (tietRegEmail.getText().toString().isEmpty()) {
                    tietRegEmail.setError("Enter Your Email Address");
                } else if (!tietRegEmail.getText().toString().contains("@gmail.com")) {
                    tietRegEmail.setError("Invalid Email Address");
                } else if (tietRegUsername.getText().toString().isEmpty()) {
                    tietRegUsername.setError("Enter Your Username");
                } else if (tietRegUsername.getText().toString().length() < 8) {
                    tietRegUsername.setError("Username must contain at least 8 letters");
                } else if (tietRegPassword.getText().toString().isEmpty()) {
                    tietRegPassword.setError("Enter your Password");
                } else if (tietRegPassword.getText().toString().length() < 8) {
                    tietRegPassword.setError("Password must contain at least 8 digits");
                } else if (!tietRegPassword.getText().toString().matches(".*[A-Z].*")) {
                    tietRegPassword.setError("Password contain at least 1 letter in uppercase");
                } else if (!tietRegPassword.getText().toString().matches(".*[a-z].*")) {
                    tietRegPassword.setError("Password contain at least 1 letter in lowecase");
                } else if (!tietRegPassword.getText().toString().matches(".*[0-9].*")) {
                    tietRegPassword.setError("Password contain at least 1 digit");
                } else if (!tietRegPassword.getText().toString().matches(".*[@#$%^&+=!].*")) {
                    tietRegPassword.setError("Password contain at least 1 special symbol");
                } else {
                    addUser(strname,strmobile,stremail,strusername,strpassword);
                }
            }
        });
    }

    private void addUser(String strname, String strmobile,String stremail, String strusername, String strpassword) {
        if(dbHelper.registerUser(strname,strmobile,stremail,strusername,strpassword))
        {
            Toast.makeText(RegistrationActivity.this,"Registration Successfully Done",Toast.LENGTH_SHORT).show();
            editor.putString("name",tietRegName.getText().toString()).commit();
            editor.putString("mobileno",tietRegMobileNo.getText().toString()).commit();
            editor.putString("emailid",tietRegEmail.getText().toString()).commit();
            editor.putString("username",tietRegUsername.getText().toString()).commit();
            Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(RegistrationActivity.this,"User Already Exists",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}