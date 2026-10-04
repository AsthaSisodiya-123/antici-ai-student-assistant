package com.example.whatsapp.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsapp.LoginActivity;
import com.example.whatsapp.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfilePicFragment extends Fragment {

    FrameLayout frameLayout;
    ImageView ivProfileBackpress,ivProfilePicImage;
    TextView tvProfilePicName;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_profile_pic, container, false);
        ivProfileBackpress=view.findViewById(R.id.ivProfileBackpress);
        tvProfilePicName=view.findViewById(R.id.tvProfilePicName);
        frameLayout=view.findViewById(R.id.profileFrameLayout);

        ivProfilePicImage= view.findViewById(R.id.ivProfilePicImage);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String base64Image = preferences.getString("profileImage", null);
        String name = preferences.getString("profileName", null);

        if (base64Image != null) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            ivProfilePicImage.setImageBitmap(bitmap);
            tvProfilePicName.setText(name);

        }


        ivProfileBackpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat_Fragment chatFragment=new Chat_Fragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.homeFrameLayout, chatFragment).commit();
            }
        });

        return view;

    }


}