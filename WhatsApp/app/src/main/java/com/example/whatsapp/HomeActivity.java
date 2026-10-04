package com.example.whatsapp;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.whatsapp.Fragments.Call_Fragment;
import com.example.whatsapp.Fragments.Chat_Fragment;
import com.example.whatsapp.Fragments.Settings_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    public boolean doubleTap = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01BC32")));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        boolean isFirstTime= preferences.getBoolean("isFirstTime",true);

        if(isFirstTime)
        {
            welcome();
        }


        bottomNavigationView=findViewById(R.id.homeBottomNavIconView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.homeBottomNavChat);

    }

    private void welcome() {

        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("WhatsApp");
        ad.setMessage("Welcome to WhatsApp");
        ad.setPositiveButton("Thank you", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
        editor.putBoolean("isFirstTime",false).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuScaner) {
            Toast.makeText(HomeActivity.this, "Scanner", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, ScannerActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuCamera) {
            Toast.makeText(HomeActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuMyprofile) {
            Toast.makeText(HomeActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder ad = new AlertDialog.Builder(HomeActivity.this);
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
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
        if (doubleTap)
        {
            finishAffinity();
        }
        else
        {
            Toast.makeText(this,"Press again to exit",Toast.LENGTH_SHORT).show();

            doubleTap=true;
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap=false;
                }
            },2000);
        }

    }

    Chat_Fragment chatFragment=new Chat_Fragment();
    Call_Fragment callFragment=new Call_Fragment();
    Settings_Fragment settingFragment=new Settings_Fragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        if (menuitem.getItemId()==R.id.homeBottomNavChat)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, chatFragment).commit();

        }
        else if (menuitem.getItemId()==R.id.homeBottomNavCall)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout,callFragment).commit();

        }
        else if (menuitem.getItemId()==R.id.homeBottomNavSettings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, settingFragment).commit();

        }
        return false;
    }

}