package com.example.skillbrigde.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.skillbrigde.EditProfileActivity;
import com.example.skillbrigde.R;

public class ProfileFragment extends Fragment {


        TextView name, email, phone, course, gender, dob, address, joined;
        Button editProfile;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_profile, container, false);

            name = view.findViewById(R.id.txtName);
            email = view.findViewById(R.id.txtEmail);
            phone = view.findViewById(R.id.txtPhone);
            course = view.findViewById(R.id.txtCourse);
            gender = view.findViewById(R.id.txtGender);
            dob = view.findViewById(R.id.txtDob);
            address = view.findViewById(R.id.txtAddress);
            joined = view.findViewById(R.id.txtJoined);
            editProfile = view.findViewById(R.id.btnEditProfile);

            // Example (replace with Firebase data)
            name.setText("Astha Sisodiya");
            email.setText("astha@gmail.com");
            phone.setText("9876543210");
            course.setText("Android Development");
            gender.setText("Female");
            dob.setText("12 March 2004");
            address.setText("Indore, India");
            joined.setText("Aug 2025");

            editProfile.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            });

            return view;
        }
    }
