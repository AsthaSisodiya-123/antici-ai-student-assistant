package com.example.skillbrigde.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.skillbrigde.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    TextView tvGreeting;
    ImageSlider imageSlider;
    GridLayout glPopularInternships, glFreeCourses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        imageSlider = view.findViewById(R.id.isMyCartImageSlider);
        glPopularInternships = view.findViewById(R.id.glpopularInternships);
        glFreeCourses = view.findViewById(R.id.glfreeCourses);

        setGreeting();
        setupSlider();
        loadPopularInternships();
        loadFreeCourses();

        return view;
    }

    // 👋 Greeting
    private void setGreeting() {
        tvGreeting.setText("Hi, Student 👋");
    }

    // 🖼 Image Slider
    private void setupSlider() {

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.skillbrigdelogo, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.img_splash_illustration, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    // ⭐ Popular Internships
    private void loadPopularInternships() {

        addCard("Web Developer", glPopularInternships);
        addCard("Android Developer", glPopularInternships);
        addCard("UI/UX Designer", glPopularInternships);
        addCard("Digital Marketing", glPopularInternships);
    }

    // 🎓 Free Courses
    private void loadFreeCourses() {

        addCard("Java Programming", glFreeCourses);
        addCard("Python Basics", glFreeCourses);
        addCard("Full Stack Development", glFreeCourses);
    }

    // 🔹 Card Creator
    private void addCard(String title, GridLayout gridLayout) {

        TextView tv = new TextView(getContext());
        tv.setText(title);
        tv.setTextSize(16);
        tv.setPadding(40, 40, 40, 40);
        tv.setBackgroundResource(R.drawable.bg_input);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(20, 20, 20, 20);
        tv.setLayoutParams(params);

        gridLayout.addView(tv);
    }
}
