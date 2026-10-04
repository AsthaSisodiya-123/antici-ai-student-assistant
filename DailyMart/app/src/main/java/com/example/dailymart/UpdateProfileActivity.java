package com.example.dailymart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.dailymart.Database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class UpdateProfileActivity extends BaseActivity {

    TextInputEditText updateProfileName,updateProfileMobileNo,updateProfileEmail,updateProfileUsername;
    Button btnSaveChange;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    TextView tvLoginDeleteAccount;
    DBHelper dbHelper;
    String oldusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();

        dbHelper =new DBHelper(this);
        updateProfileName=findViewById(R.id.updateProfileName);
        updateProfileMobileNo=findViewById(R.id.updateProfileMobileNo);
        updateProfileEmail=findViewById(R.id.updateProfileEmail);
        updateProfileUsername=findViewById(R.id.updateProfileUsername);
        btnSaveChange=findViewById(R.id.btnSaveChange);

        updateProfileName.setText(preferences.getString("name",""));
        updateProfileMobileNo.setText(preferences.getString("mobile_no",""));
        updateProfileEmail.setText(preferences.getString("emailid",""));
        updateProfileUsername.setText(preferences.getString("username",""));
        oldusername=preferences.getString("username","");
        tvLoginDeleteAccount=findViewById(R.id.tvLoginDeleteAccount);
        btnSaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strName=updateProfileName.getText().toString();
                String strMobileNo=updateProfileMobileNo.getText().toString();
                String strEmail=updateProfileEmail.getText().toString();
                String strUsername=updateProfileUsername.getText().toString();
                if(dbHelper.updateUser(oldusername,strName,strMobileNo,strEmail,strUsername)){
                    Toast.makeText(UpdateProfileActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();

                    editor.putString("name",strName).commit();
                    editor.putString("mobileno",strMobileNo).commit();
                    editor.putString("emailid",strEmail).commit();
                    editor.putString("username",strUsername).commit();

                    Intent intent=new Intent(UpdateProfileActivity.this, MyProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(UpdateProfileActivity.this, "Profile Update Failed..", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tvLoginDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dbHelper.deleteUser(oldusername)){
                    Toast.makeText(UpdateProfileActivity.this, "Account deleted successfully..", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(UpdateProfileActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(UpdateProfileActivity.this, "Unable to delete account", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}