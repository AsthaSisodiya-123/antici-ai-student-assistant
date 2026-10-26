
package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.models.OnboardingSession;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2200L;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private View loadingLine;
    private boolean navigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loadingLine = findViewById(R.id.loadingLine);

        if (loadingLine != null) {
            loadingLine.setPivotX(0f);
            loadingLine.setScaleX(0f);

            loadingLine.post(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    loadingLine.animate()
                            .scaleX(1f)
                            .setDuration(SPLASH_DURATION)
                            .setInterpolator(
                                    new AccelerateDecelerateInterpolator())
                            .start();
                }
            });
        }

        handler.postDelayed(
                this::checkOnboardingStatus,
                SPLASH_DURATION
        );
    }

    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(
                "AnticiAISettings",
                MODE_PRIVATE
        );

        String theme = prefs.getString("themeMode", "dark");

        int mode;

        switch (theme) {
            case "light":
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;

            case "dark":
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;

            case "system":
            default:
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    private void checkOnboardingStatus() {
        if (isFinishing() || isDestroyed() || navigating) {
            return;
        }

        String deviceId = OnboardingSession.getDeviceId(this);
        ApiService api = RetrofitClient.getApiService(this);

        api.getOnboardingStatus(deviceId).enqueue(
                new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response) {

                        if (isFinishing()
                                || isDestroyed()
                                || navigating) {
                            return;
                        }

                        boolean completed =
                                OnboardingSession.isCompleted(
                                        SplashActivity.this);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().has("completed")
                                && !response.body()
                                .get("completed").isJsonNull()) {

                            completed = response.body()
                                    .get("completed")
                                    .getAsBoolean();

                            OnboardingSession.setCompleted(
                                    SplashActivity.this,
                                    completed
                            );
                        }

                        navigate(completed);
                    }

                    @Override
                    public void onFailure(
                            Call<JsonObject> call,
                            Throwable throwable) {

                        if (!isFinishing() && !isDestroyed()) {
                            navigate(
                                    OnboardingSession.isCompleted(
                                            SplashActivity.this)
                            );
                        }
                    }
                }
        );
    }

    private void navigate(boolean completed) {
        if (navigating || isFinishing() || isDestroyed()) {
            return;
        }

        navigating = true;

        Class<?> destination = completed
                ? LoginActivity.class
                : OnboardingActivity.class;

        Intent intent = new Intent(this, destination);
        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (loadingLine != null) {
            loadingLine.animate().cancel();
        }

        super.onDestroy();
    }
}
