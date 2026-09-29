package com.anticai.studentassistant.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.fragments.schedule.ScheduleFragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome;
    private LinearLayout navSchedule;
    private LinearLayout navTasks;
    private LinearLayout navAI;
    private LinearLayout navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeNavigation();
        setupNavigation();
    }

    private void initializeNavigation() {

        navHome = findViewById(R.id.navHome);
        navSchedule = findViewById(R.id.navSchedule);
        navTasks = findViewById(R.id.navTasks);
        navAI = findViewById(R.id.navAI);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupNavigation() {

        // HOME
        navHome.setOnClickListener(v -> {

            getSupportFragmentManager()
                    .popBackStack();

        });

        // SCHEDULE
        navSchedule.setOnClickListener(v -> {

            openFragment(new ScheduleFragment());

        });

        // TASKS
        navTasks.setOnClickListener(v -> {

            // Tasks screen will be created next

        });

        // AI
        navAI.setOnClickListener(v -> {

            // AI screen will be created later

        });

        // PROFILE
        navProfile.setOnClickListener(v -> {

            // Profile screen will be created later

        });
    }

    private void openFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}