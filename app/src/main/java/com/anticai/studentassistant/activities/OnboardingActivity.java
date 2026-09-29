package com.anticai.studentassistant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anticai.studentassistant.R;

public class OnboardingActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView btnNext;

    private int page = 0;

    private final String[] titles = {
            "Your day, before it happens.",
            "Stay ahead of the deadline.",
            "Less reacting. More knowing."
    };

    private final String[] descriptions = {
            "AnticiAI learns your academic patterns and identifies what may need your attention next.",
            "AnticiAI combines your schedule, progress and behavior to identify upcoming study needs.",
            "Receive personalized suggestions when they are actually useful."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnNext = findViewById(R.id.btnNext);

        TextView skip = findViewById(R.id.tvSkip);

        skip.setOnClickListener(v -> openLogin());

        btnNext.setOnClickListener(v -> {

            if (page < 2) {

                page++;

                updateScreen();

            } else {

                openLogin();

            }

        });
    }


    private void updateScreen() {

        tvTitle.setText(titles[page]);

        tvDescription.setText(descriptions[page]);

        if (page == 0) {

            btnNext.setText("CONTINUE  →");

        } else if (page == 1) {

            btnNext.setText("CONTINUE  →");

        } else {

            btnNext.setText("GET STARTED  →");

        }
    }


    private void openLogin() {

        Intent intent = new Intent(
                OnboardingActivity.this,
                LoginActivity.class
        );

        startActivity(intent);

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );

        finish();
    }
}