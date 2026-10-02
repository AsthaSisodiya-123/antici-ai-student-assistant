package com.anticai.studentassistant.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.fragments.ai.AIChatFragment;
import com.anticai.studentassistant.fragments.home.DashboardFragment;
import com.anticai.studentassistant.fragments.profile.ProfileFragment;
import com.anticai.studentassistant.fragments.schedule.ScheduleFragment;
import com.anticai.studentassistant.fragments.tasks.TasksFragment;

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

        // Open Home/Dashboard when MainActivity starts
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment(), false);
        }
    }

    /**
     * Initialize bottom navigation views
     */
    private void initializeNavigation() {

        navHome = findViewById(R.id.navHome);
        navSchedule = findViewById(R.id.navSchedule);
        navTasks = findViewById(R.id.navTasks);
        navAI = findViewById(R.id.navAI);
        navProfile = findViewById(R.id.navProfile);
    }

    /**
     * Setup bottom navigation click listeners
     */
    private void setupNavigation() {

        // HOME
        navHome.setOnClickListener(v -> {

            loadFragment(
                    new DashboardFragment(),
                    false
            );

        });

        // SCHEDULE
        navSchedule.setOnClickListener(v -> {

            loadFragment(
                    new ScheduleFragment(),
                    false
            );

        });

        // TASKS
        navTasks.setOnClickListener(v -> {

            loadFragment(
                    new TasksFragment(),
                    false
            );

        });

        // AI
        navAI.setOnClickListener(v -> {

            loadFragment(
                    new AIChatFragment(),
                    false
            );

        });

        // PROFILE
        navProfile.setOnClickListener(v -> {

            loadFragment(
                    new ProfileFragment(),
                    false
            );

        });
    }

    /**
     * Replace the current fragment
     *
     * @param fragment Fragment to display
     * @param addToBackStack Whether to add the transaction to back stack
     */
    private void loadFragment(Fragment fragment, boolean addToBackStack) {

        androidx.fragment.app.FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(
                                R.id.mainContainer,
                                fragment
                        );

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    /**
     * Handle Android back button
     */
    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            getSupportFragmentManager().popBackStack();

        } else {

            super.onBackPressed();

        }
    }
}

