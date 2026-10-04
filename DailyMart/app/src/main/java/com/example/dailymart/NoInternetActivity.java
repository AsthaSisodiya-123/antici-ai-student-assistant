package com.example.dailymart;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.dailymart.Common.NetworkDetails;

public class NoInternetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        // Animate layout
        View view = findViewById(R.id.noInternetRoot);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        view.startAnimation(slideIn);

        AppCompatButton btnTryAgain = findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(v -> {
            if (NetworkDetails.isConnectedToInternet(NoInternetActivity.this)) {
                Toast.makeText(this, "Internet Connected", Toast.LENGTH_SHORT).show();
                finish(); // Close activity
            } else {
                Toast.makeText(this, "Still no internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
