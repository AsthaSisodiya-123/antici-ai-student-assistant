package com.example.dailymart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;

public class MyProfileActivity extends BaseActivity {

    TextView tvMyProfileName,tvMyProfileMobileNo,tvMyProfileEmail,tvMyProfileUsername,tvToken,tvMyProfileUpdate;
    ShapeableImageView ivMyProfileImage,ivMyProfileEditProfile;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Uri imagePath;
    Bitmap bitmap;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#449984")));

        tvToken=findViewById(R.id.tvMyProfileToken);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();

        ivMyProfileImage=findViewById(R.id.ivMyProfileImage);
        ivMyProfileEditProfile=findViewById(R.id.ivMyProfileEditProfile);
        tvMyProfileName=findViewById(R.id.tvMyProfileName);
        tvMyProfileMobileNo=findViewById(R.id.tvMyProfileMobileNo);
        tvMyProfileEmail=findViewById(R.id.tvMyProfileEmail);
        tvMyProfileUsername=findViewById(R.id.tvMyProfileUsername);
        tvMyProfileUpdate=findViewById(R.id.tvMyProfileUpdate);

        tvMyProfileName.setText(preferences.getString("name","Name not available"));
        tvMyProfileMobileNo.setText(preferences.getString("mobileno","Mobile No not available"));
        tvMyProfileEmail.setText(preferences.getString("emailid","Email not available"));
        tvMyProfileUsername.setText(preferences.getString("username","Username not available"));
        tvToken.setText(preferences.getString("token","Token not available"));

        ivMyProfileEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });


        tvMyProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyProfileActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showImageChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Photo"),999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==999 && resultCode==RESULT_OK && data!=null)
        {
           imagePath= Uri.parse(data.getData().toString());
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                ivMyProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this,""+e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}