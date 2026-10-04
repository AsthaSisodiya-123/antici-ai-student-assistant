package com.example.whatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

public class MyProfileActivity extends AppCompatActivity {
    TextView tvMyProfileMobileNo,tvMyProfileEmail,tvMyProfileUsername,tvMyProfileAbout,tvMyProfileUpdate;
    ShapeableImageView ivMyProfileImage,ivMyProfileEditProfile;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Uri imagePath;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor=preferences.edit();

        ivMyProfileImage=findViewById(R.id.ivMyPrifileImage);
        ivMyProfileEditProfile=findViewById(R.id.ivMyProfileEditProfile);
        tvMyProfileMobileNo=findViewById(R.id.tvMyProfileMobileNo);
        tvMyProfileEmail=findViewById(R.id.tvMyProfileEmail);
        tvMyProfileUsername=findViewById(R.id.tvMyProfileUsername);
        tvMyProfileUpdate=findViewById(R.id.tvMyProfileUpdate);
        tvMyProfileAbout=findViewById(R.id.tvMyProfileAbout);

        tvMyProfileMobileNo.setText(preferences.getString("mobile_no","Mobile No not available"));
        tvMyProfileEmail.setText(preferences.getString("email","Email not available"));
        tvMyProfileUsername.setText(preferences.getString("username","Username not available"));
        tvMyProfileAbout.setText(preferences.getString("about","Available"));

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