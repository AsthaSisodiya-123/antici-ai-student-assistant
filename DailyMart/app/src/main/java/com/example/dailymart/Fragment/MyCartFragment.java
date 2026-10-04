package com.example.dailymart.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.dailymart.R;

import java.util.ArrayList;

public class MyCartFragment extends Fragment {
    ImageSlider imageSlider;
    VideoView videoView;
    LinearLayout tvMyCartNetworkStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_cart, container, false);

        tvMyCartNetworkStatus =view.findViewById(R.id.tvMyCartNetworkStatus);
        checkNetworkStatus();

        imageSlider=view.findViewById(R.id.isMyCartImageSlider);
        videoView=view.findViewById(R.id.vvMyCartVideo);

        ArrayList<SlideModel>slideModelArrayList=new ArrayList<>();
        slideModelArrayList.add(new SlideModel(R.drawable.imageslider1,"Slide 1", ScaleTypes.CENTER_CROP));
        slideModelArrayList.add(new SlideModel(R.drawable.imageslider2,"Slide 2", ScaleTypes.CENTER_CROP));
        slideModelArrayList.add(new SlideModel(R.drawable.imageslider3,"Slide 3", ScaleTypes.CENTER_CROP));
        slideModelArrayList.add(new SlideModel(R.drawable.imageslider4,"Slide 4", ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModelArrayList);
        imageSlider.setSlideAnimation(AnimationTypes.BACKGROUND_TO_FOREGROUND);
        String videoPath="android.resource://"+ getActivity().getPackageName()+"/raw/video";
        videoView.setVideoPath(videoPath);
        videoView.start();
        Toast.makeText(getActivity(),"My Cart",Toast.LENGTH_SHORT).show();
        return view;
    }
    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            tvMyCartNetworkStatus .setVisibility(View.GONE); // hide banner
        } else {
            tvMyCartNetworkStatus .setVisibility(View.VISIBLE); // show banner
        }
    }
}