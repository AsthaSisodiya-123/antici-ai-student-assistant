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

        View logo = findViewById(R.id.logoContainer);
        View brand = findViewById(R.id.tvBrand);
        View tagline = findViewById(R.id.tvTagline);
        View loading = findViewById(R.id.loadingContainer);

        // Logo animation
        logo.setAlpha(0f);
        logo.setScaleX(0.7f);
        logo.setScaleY(0.7f);

        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .start();

        // Brand animation
        brand.setAlpha(0f);

        brand.animate()
                .alpha(1f)
                .setStartDelay(450)
                .setDuration(600)
                .start();

        // Tagline animation
        tagline.setAlpha(0f);

        tagline.animate()
                .alpha(1f)
                .setStartDelay(700)
                .setDuration(600)
                .start();

        // Loading animation
        loading.setAlpha(0f);

        loading.animate()
                .alpha(1f)
                .setStartDelay(1000)
                .setDuration(500)
                .start();


        // Open onboarding
        new Handler().postDelayed(() -> {

            Intent intent = new Intent(
                    SplashActivity.this,
                    OnboardingActivity.class
            );

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );

            finish();

        }, SPLASH_DURATION);
    }
}