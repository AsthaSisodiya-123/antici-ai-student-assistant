package com.anticai.studentassistant.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.fragments.ai.AIChatFragment;
import com.anticai.studentassistant.fragments.ai.StudyPlanFragment;
import com.anticai.studentassistant.fragments.home.DashboardFragment;
import com.anticai.studentassistant.fragments.profile.ProfileFragment;
import com.anticai.studentassistant.fragments.schedule.ScheduleFragment;
import com.anticai.studentassistant.fragments.tasks.TasksFragment;

public class MainActivity extends AppCompatActivity {

    private LinearLayout navHome;
    private LinearLayout navSchedule;
    private LinearLayout navTasks;
    private LinearLayout navAI;
    private LinearLayout navStudyPlan;
    private LinearLayout navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNavigation();
        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment(), false);
        }
    }

    private void initializeNavigation() {
        navHome = findViewById(R.id.navHome);
        navSchedule = findViewById(R.id.navSchedule);
        navTasks = findViewById(R.id.navTasks);
        navAI = findViewById(R.id.navAI);
        navStudyPlan = findViewById(R.id.navStudyPlan);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v ->
                loadFragment(new DashboardFragment(), false));

        navSchedule.setOnClickListener(v ->
                loadFragment(new ScheduleFragment(), false));

        navTasks.setOnClickListener(v ->
                loadFragment(new TasksFragment(), false));

        navAI.setOnClickListener(v ->
                loadFragment(new AIChatFragment(), false));

        navStudyPlan.setOnClickListener(v ->
                loadFragment(new StudyPlanFragment(), false));

        navProfile.setOnClickListener(v ->
                loadFragment(new ProfileFragment(), false));
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.mainContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void openStudyPlan() {
        loadFragment(new StudyPlanFragment(), true);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}