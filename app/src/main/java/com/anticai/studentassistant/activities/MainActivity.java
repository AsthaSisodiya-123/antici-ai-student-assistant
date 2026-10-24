
package com.anticai.studentassistant.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
    private LinearLayout navStudy;

    private TextView labelHome;
    private TextView labelSchedule;
    private TextView labelTasks;
    private TextView labelStudy;

    private TextView btnAI;
    private ImageButton btnNotifications;
    private TextView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupNavigation();
        setupActionButtons();

        getSupportFragmentManager()
                .addOnBackStackChangedListener(
                        this::updateAIButtonVisibility);

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment(), false);
            selectNavigation(navHome);
        } else {
            updateAIButtonVisibility();
        }
    }

    private void applySavedTheme() {
        android.content.SharedPreferences prefs =
                getSharedPreferences("AnticiAISettings", MODE_PRIVATE);

        String theme = prefs.getString("themeMode", "dark");

        int mode;

        switch (theme) {
            case "light":
                mode = AppCompatDelegate.MODE_NIGHT_NO;
                break;

            case "system":
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;

            default:
                mode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
        }

        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    private void initializeViews() {
        navHome = findViewById(R.id.navHome);
        navSchedule = findViewById(R.id.navSchedule);
        navTasks = findViewById(R.id.navTasks);
        navStudy = findViewById(R.id.navStudy);

        labelHome = findViewById(R.id.labelHome);
        labelSchedule = findViewById(R.id.labelSchedule);
        labelTasks = findViewById(R.id.labelTasks);
        labelStudy = findViewById(R.id.labelStudy);

        btnAI = findViewById(R.id.btnAI);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnProfile = findViewById(R.id.btnProfile);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            clearBackStack();
            loadFragment(new DashboardFragment(), false);
            selectNavigation(navHome);
        });

        navSchedule.setOnClickListener(v -> {
            clearBackStack();
            loadFragment(new ScheduleFragment(), false);
            selectNavigation(navSchedule);
        });

        navTasks.setOnClickListener(v -> {
            clearBackStack();
            loadFragment(new TasksFragment(), false);
            selectNavigation(navTasks);
        });

        navStudy.setOnClickListener(v -> {
            clearBackStack();
            loadFragment(new StudyPlanFragment(), false);
            selectNavigation(navStudy);
        });
    }

    private void setupActionButtons() {
        btnAI.setOnClickListener(v ->
                loadFragment(new AIChatFragment(), true));

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(
                        MainActivity.this,
                        "Notifications coming soon",
                        Toast.LENGTH_SHORT
                ).show());

        btnProfile.setOnClickListener(v ->
                loadFragment(new ProfileFragment(), true));
    }

    private void loadFragment(
            Fragment fragment,
            boolean addToBackStack) {

        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.mainContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        getSupportFragmentManager().executePendingTransactions();

        updateAIButtonVisibility();
    }

    private void updateAIButtonVisibility() {
        if (btnAI == null) {
            return;
        }

        Fragment currentFragment =
                getSupportFragmentManager()
                        .findFragmentById(R.id.mainContainer);

        if (currentFragment instanceof DashboardFragment) {
            btnAI.setVisibility(View.VISIBLE);
        } else {
            btnAI.setVisibility(View.GONE);
        }
    }

    private void clearBackStack() {
        getSupportFragmentManager().popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().executePendingTransactions();
    }

    private void selectNavigation(LinearLayout selectedItem) {
        resetNavigationLabels();

        int selectedColor = getColor(R.color.nav_selected);

        if (selectedItem == navHome) {
            labelHome.setTextColor(selectedColor);
        } else if (selectedItem == navSchedule) {
            labelSchedule.setTextColor(selectedColor);
        } else if (selectedItem == navTasks) {
            labelTasks.setTextColor(selectedColor);
        } else if (selectedItem == navStudy) {
            labelStudy.setTextColor(selectedColor);
        }
    }

    private void resetNavigationLabels() {
        int inactiveColor = getColor(R.color.nav_unselected);

        labelHome.setTextColor(inactiveColor);
        labelSchedule.setTextColor(inactiveColor);
        labelTasks.setTextColor(inactiveColor);
        labelStudy.setTextColor(inactiveColor);
    }

    public void openStudyPlan() {
        clearBackStack();
        loadFragment(new StudyPlanFragment(), false);
        selectNavigation(navStudy);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager =
                getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
