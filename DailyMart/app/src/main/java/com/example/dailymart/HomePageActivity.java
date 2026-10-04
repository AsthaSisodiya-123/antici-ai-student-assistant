package com.example.dailymart;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.dailymart.Common.NetworkChangeListner;
import com.example.dailymart.Common.ThemePreferanceManager;
import com.example.dailymart.Fragment.CategoriesFragment;
import com.example.dailymart.Fragment.ChatFragment;
import com.example.dailymart.Fragment.HomeFragment;
import com.example.dailymart.Fragment.MyCartFragment;
import com.example.dailymart.Fragment.WishListFragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePageActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public boolean doubleTap = false;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    BottomNavigationView bottomNavigationView;
    NetworkChangeListner networkChangeListner = new NetworkChangeListner();
GoogleSignInClient googleSignInClient;
GoogleSignInOptions googleSignInOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#449984")));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();


        boolean isFirstTime = preferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            welcome();
        }

        bottomNavigationView = findViewById(R.id.homeBottomNavIconView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homebottomnavhome);


    }


    private void welcome() {

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Thakur Kirana Shoppy");
        ad.setMessage("Welcome to Thakur Kirana Shoppy");
        ad.setPositiveButton("Thank you", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
        editor.putBoolean("isFirstTime", false).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        MenuItem menuItem=menu.findItem(R.id.menuTheme);
        Switch themeSwitch = menuItem.getActionView().findViewById(R.id.action_theme_switch);

        int currentTheme = AppCompatDelegate.getDefaultNightMode();
        themeSwitch.setChecked(currentTheme==AppCompatDelegate.MODE_NIGHT_YES);
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            int newThememode =isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            ThemePreferanceManager.saveThemeMode(this,newThememode);
            AppCompatDelegate.setDefaultNightMode(newThememode);
            recreate();
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuMyFav) {
            Toast.makeText(HomePageActivity.this, "My Favorite", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, MyFavoriteActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuScaner) {
            Toast.makeText(HomePageActivity.this, "Scanner", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, ScannerActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuQRCode) {
            Toast.makeText(HomePageActivity.this, "QR Code", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, QR_CodeActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuMyLocation) {
            Toast.makeText(HomePageActivity.this, "My Location", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, MapsActivity
                    .class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuMyprofile) {
            Toast.makeText(HomePageActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, MyProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuSetting) {
            Toast.makeText(HomePageActivity.this, "Setting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuContactus) {
            Toast.makeText(HomePageActivity.this, "Contact us", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuAboutus) {
            Toast.makeText(HomePageActivity.this, "About us", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomePageActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else {

            googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build();
            AlertDialog.Builder ad = new AlertDialog.Builder(HomePageActivity.this);
            ad.setTitle("Logout");
            ad.setMessage("Are you sure you want to Logout?");
            ad.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ad.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                    googleSignInClient.signOut();
                    editor.putBoolean("isLogin", false).commit();
                    startActivity(intent);
                    finish();

                }
            }).create().show();


        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (doubleTap) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                }
            }, 2000);
        }
    }

    HomeFragment homeFragment = new HomeFragment();
    CategoriesFragment categoriesFragment = new CategoriesFragment();
    ChatFragment chatFragment = new ChatFragment();
    WishListFragment wishListFragment = new WishListFragment();
    MyCartFragment myCartFragment = new MyCartFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        if (menuitem.getItemId() == R.id.homebottomnavhome) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, homeFragment).commit();

        } else if (menuitem.getItemId() == R.id.homebottomnavcategories) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, categoriesFragment).commit();

        } else if (menuitem.getItemId() == R.id.homebottomnavchat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, chatFragment).commit();

        } else if (menuitem.getItemId() == R.id.homebottomnavwishlist) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, wishListFragment).commit();

        } else if (menuitem.getItemId() == R.id.homebottomnavmycart) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, myCartFragment).commit();

        }
        return false;
    }

}
