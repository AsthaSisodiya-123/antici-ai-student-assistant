
package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.models.OnboardingSession;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private static final String TAG = "ONBOARDING";
    private TextView tvPageNumber;
    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvOrbLabel;
    private TextView btnNext;
    private TextView tvSkip;

    private View dot1;
    private View dot2;
    private View dot3;
    private View orb;

    private int currentPage = 0;
    private boolean completing = false;

    private final String[] titles = {
            "Your day, before it happens.",
            "Know what needs attention.",
            "Study smarter with AnticiAI."
    };

    private final String[] descriptions = {
            "AnticiAI learns your academic patterns and identifies what may need your attention next.",
            "Keep track of study plans, sessions, and subjects so you can focus on what matters most.",
            "Build better study habits, manage your time, and take your next step with confidence."
    };

    private final String[] orbLabels = {
            "PREDICT",
            "ANALYZE",
            "FOCUS"
    };

    private final int BLUE = Color.rgb(77, 141, 255);
    private final int INACTIVE = Color.rgb(37, 43, 61);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvOrbLabel = findViewById(R.id.tvOrbLabel);
        tvPageNumber = findViewById(R.id.tvPageNumber);

        btnNext = findViewById(R.id.btnNext);
        tvSkip = findViewById(R.id.tvSkip);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);

        orb = findViewById(R.id.orb);

        if (tvTitle == null || tvDescription == null
                || tvOrbLabel == null || btnNext == null
                || tvSkip == null) {
            Log.e(TAG, "Required onboarding views are missing. Check XML IDs.");
            Toast.makeText(
                    this,
                    "Onboarding layout error. Please check the layout IDs.",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        btnNext.setOnClickListener(v -> onNextClicked());
        tvSkip.setOnClickListener(v -> completeOnboarding());

        showPage(0);
    }

    private void onNextClicked() {
        if (completing) {
            return;
        }

        if (currentPage < titles.length - 1) {
            showPage(currentPage + 1);
        } else {
            completeOnboarding();
        }
    }
    private void showPage(int page) {
        if (page < 0 || page >= titles.length) {
            return;
        }

        currentPage = page;

        // Update page number: 1, 2, 3
        if (tvPageNumber != null) {
            tvPageNumber.setText(String.valueOf(page + 1));
        }

        tvTitle.animate().cancel();
        tvDescription.animate().cancel();

        tvTitle.setText(titles[page]);
        tvDescription.setText(descriptions[page]);
        tvOrbLabel.setText(orbLabels[page]);

        // Continue with the rest of your existing animation
        // and progress indicator code here.


        currentPage = page;

        // Cancel previous animations.
        tvTitle.animate().cancel();
        tvDescription.animate().cancel();

        // Update content immediately so the latest page is always displayed.
        tvTitle.setText(titles[page]);
        tvDescription.setText(descriptions[page]);
        tvOrbLabel.setText(orbLabels[page]);

        // Animate the title.
        tvTitle.setAlpha(0f);
        tvTitle.animate()
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Animate the description.
        tvDescription.setAlpha(0f);
        tvDescription.animate()
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // Update button text.
        btnNext.setText(
                page == titles.length - 1
                        ? "GET STARTED  →"
                        : "CONTINUE  →"
        );

        updateProgress(page);

        // Animate the visual area.
        if (orb != null) {
            orb.animate().cancel();
            orb.setScaleX(0.96f);
            orb.setScaleY(0.96f);

            orb.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(350)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }
    }

    private void updateProgress(int page) {
        updateDot(dot1, 0, page);
        updateDot(dot2, 1, page);
        updateDot(dot3, 2, page);
    }

    private void updateDot(View dot, int index, int page) {
        if (dot == null) {
            return;
        }

        int targetWidth = dp(index == page ? 30 : 15);
        int targetColor = index <= page ? BLUE : INACTIVE;

        ViewGroup.LayoutParams params = dot.getLayoutParams();

        if (params == null) {
            return;
        }

        int startWidth = params.width;

        android.animation.ValueAnimator animator =
                android.animation.ValueAnimator.ofInt(
                        startWidth,
                        targetWidth
                );

        animator.setDuration(250);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams updated = dot.getLayoutParams();

            if (updated != null) {
                updated.width = (int) animation.getAnimatedValue();
                dot.setLayoutParams(updated);
            }
        });

        animator.start();
        dot.setBackgroundColor(targetColor);
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private void completeOnboarding() {
        if (completing) {
            return;
        }

        completing = true;

        btnNext.setEnabled(false);
        tvSkip.setEnabled(false);
        btnNext.setAlpha(0.6f);

        String deviceId = OnboardingSession.getDeviceId(this);

        Log.d(TAG, "Saving onboarding for deviceId=" + deviceId);

        ApiService api = RetrofitClient.getApiService(this);

        api.completeOnboarding(deviceId).enqueue(
                new Callback<JsonObject>() {

                    @Override
                    public void onResponse(
                            Call<JsonObject> call,
                            Response<JsonObject> response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (response.isSuccessful()) {
                            Log.d(
                                    TAG,
                                    "Onboarding saved. HTTP " + response.code()
                            );

                            OnboardingSession.setCompleted(
                                    OnboardingActivity.this,
                                    true
                            );

                            Intent intent = new Intent(
                                    OnboardingActivity.this,
                                    LoginActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {
                            String errorBody = "";

                            try {
                                if (response.errorBody() != null) {
                                    errorBody =
                                            response.errorBody().string();
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Could not read error body", e);
                            }

                            Log.e(
                                    TAG,
                                    "Onboarding save failed. HTTP "
                                            + response.code()
                                            + ", body=" + errorBody
                            );

                            restoreButtons();

                            Toast.makeText(
                                    OnboardingActivity.this,
                                    "Save failed: HTTP " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<JsonObject> call,
                            Throwable throwable) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        Log.e(TAG, "Network/request failure", throwable);

                        restoreButtons();

                        Toast.makeText(
                                OnboardingActivity.this,
                                "Connection failed: "
                                        + throwable.getClass().getSimpleName(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void restoreButtons() {
        completing = false;

        btnNext.setEnabled(true);
        tvSkip.setEnabled(true);
        btnNext.setAlpha(1f);
    }

    @Override
    protected void onDestroy() {
        if (orb != null) {
            orb.animate().cancel();
        }

        if (tvTitle != null) {
            tvTitle.animate().cancel();
        }

        if (tvDescription != null) {
            tvDescription.animate().cancel();
        }

        super.onDestroy();
    }
}
