package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);



        new Handler().postDelayed(() -> {

            Intent intent = new Intent(
                    SplashActivity.this,
                    OnboardingActivity.class
            );

            startActivity(intent);
            finish();

        }, SPLASH_DURATION);
    }
}