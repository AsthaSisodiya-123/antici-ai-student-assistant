package com.example.recipeapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {



    ImageView ivloginlogo;
    EditText etLoginUsername, etLoginPassword;
    CheckBox cbLoginShowHidePassword;
    Button btnLoginLogin;
    TextView tvLoginNewUsername;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivloginlogo=findViewById(R.id.ivLoginLogo);
        etLoginUsername=findViewById(R.id.etLoginUsername);
        etLoginPassword=findViewById(R.id.etLoginPassword);
        cbLoginShowHidePassword=findViewById(R.id.cbLoginShowHidePassword);
        btnLoginLogin=findViewById(R.id.btnLogin);
        tvLoginNewUsername=findViewById(R.id.tvloginNewuser);


        cbLoginShowHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    etLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    etLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnLoginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etLoginUsername.getText().toString().isEmpty())
                {
                    etLoginUsername.setError("Please Enter Your Username");
                }
                else if (etLoginUsername.getText().toString().length()<8)
                {
                     etLoginUsername.setError("Username length should not less than 8");
                }
                else if (etLoginPassword.getText().toString().isEmpty())
                {
                    etLoginPassword.setError("Please Enter Password");
                }
                else if (etLoginPassword.getText().toString().length()<8)
                {
                 etLoginPassword.setError("Password length should no less than 8");
                }
                else if (!etLoginPassword.getText().toString().matches(".*[a-z].*"))
                {
                    etLoginPassword.setError("Password contain at least 1 letter lowercase");
                }
                else if (!etLoginPassword.getText().toString().matches(".*[A-Z].*"))
                {
                    etLoginPassword.setError("Password contain at least 1 letter uppercase");
                }
                else if (!etLoginPassword.getText().toString().matches(".*[0-9].*"))
                {
                    etLoginPassword.setError("Password contain at least 1 digit");
                }
                else if (!etLoginPassword.getText().toString().matches(".*[@#$%^&+=!].*"))
                {
                    etLoginPassword.setError("Password contain at least 1 special symbol");
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}