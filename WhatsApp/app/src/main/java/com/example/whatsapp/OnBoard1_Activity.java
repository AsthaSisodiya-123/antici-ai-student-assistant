package com.example.whatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnBoard1_Activity extends AppCompatActivity {
    AppCompatButton btnOnBoard1AgreeAndContinue;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board1);

        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        if (preferences.getBoolean("isAgree",false))
        {
            Intent intent=new Intent(this,HomeActivity.class);
            startActivity(intent);

        }
        btnOnBoard1AgreeAndContinue=findViewById(R.id.btnOnBoard1AgreeAndContinue);

        btnOnBoard1AgreeAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OnBoard1_Activity.this,LoginActivity.class);
                startActivity(intent);
                finish();

                editor.putBoolean("isAgree",true).commit();
            }
        });


        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        Spinner languageSpinner = findViewById(R.id.languageSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.language_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);



    }
}