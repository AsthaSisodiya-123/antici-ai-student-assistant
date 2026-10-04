package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    LinearLayout llmain;
    ImageView ivmainlogo;
    TextView tvmain,tvmainslogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        llmain=findViewById(R.id.main);
        ivmainlogo=findViewById(R.id.ivmainlogo);
        tvmain=findViewById(R.id.tvmain);
        tvmainslogan=findViewById(R.id.tvmainslogan);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);


    }
}