package com.example.dailymart;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymart.Common.NetworkChangeListner;
import com.example.dailymart.Database.DBHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends BaseActivity {

    LinearLayout lllogin;
    TextView tvLogin;
    TextInputEditText tietUsername,tietPassword;
    Button btnLogin,btnGoogleLogin;
    TextView tvLoginNewUsername;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    DBHelper dbHelper; // ✅ Add DBHelper

    NetworkChangeListner networkChangeListner=new NetworkChangeListner();
    TextView tvTerms;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#449984")));

        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        if (preferences.getBoolean("isLogin",false))
        {
            Intent intent=new Intent(this,HomePageActivity.class);
            startActivity(intent);

        }
        tvTerms = findViewById(R.id.tvTerms);


        tvTerms.setOnClickListener(v -> {
            // Any sample URL for Terms & Conditions
            String url = "https://www.example.com/terms";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        dbHelper = new DBHelper(this); // ✅ Initialize DBHelper


        lllogin = findViewById(R.id.lllogin);
        tvLogin = findViewById(R.id.tvLogin);
        tietUsername = findViewById(R.id.tietUsername);
        tietPassword = findViewById(R.id.tietPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvLoginNewUsername = findViewById(R.id.tvLoginNewUsername);
        btnGoogleLogin=findViewById(R.id.btnGoogleLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strusername = tietUsername.getText().toString();
                String strpassword = tietPassword.getText().toString();
                if (tietUsername.getText().toString().isEmpty()) {
                    tietUsername.setError("Please Enter Username");
                } else if (tietUsername.getText().toString().length() < 8) {
                    tietUsername.setError("Username must contain at least 8 letters");
                } else if (tietPassword.getText().toString().isEmpty()) {
                    tietPassword.setError("Please Enter Password");
                } else if (tietPassword.getText().toString().length() < 8) {
                    tietPassword.setError("Password must contain at least 8 digits");
                } else if (!tietPassword.getText().toString().matches(".*[A-Z].*")) {
                    tietPassword.setError("Password contain at least 1 letter in uppercase");
                } else if (!tietPassword.getText().toString().matches(".*[a-z].*")) {
                    tietPassword.setError("Password contain at least 1 letter in lowecase");
                } else if (!tietPassword.getText().toString().matches(".*[0-9].*")) {
                    tietPassword.setError("Password contain at least 1 digit");
                } else if (!tietPassword.getText().toString().matches(".*[@#$%^&+=!].*")) {
                    tietPassword.setError("Password contain at least 1 special symbol");
                } else {
                    validateUser(strusername,strpassword);
                }
            }
        });


        tvLoginNewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);

            }
        });

        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);

        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });

    }

    private void signIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent,999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==999)
        {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    // Save email id to SharedPreferences
                    editor.putString("emailid", account.getEmail());
                    editor.commit();
                }

                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                startActivity(intent);
                editor.putBoolean("isLogin",true).commit();
                finish();

            } catch (ApiException e) {
                Toast.makeText(this,""+e.toString(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void validateUser(String strusername, String strpassword){
        if (dbHelper.loginUser(strusername,strpassword))
        {
            Toast.makeText(this,"Login Successfully Done",Toast.LENGTH_SHORT).show();
            Intent i=new Intent(LoginActivity.this,HomePageActivity.class);
            editor.putBoolean("isLogin",true).commit();
            editor.putString("username",tietUsername.getText().toString()).commit();
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this,"User Does Not Exists",Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListner,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListner);
        super.onStop();
    }
}

