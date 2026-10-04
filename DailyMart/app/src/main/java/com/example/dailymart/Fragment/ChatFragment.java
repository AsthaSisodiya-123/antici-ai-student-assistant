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

import com.example.dailymart.R;

public class ChatFragment extends Fragment {

LinearLayout tvChatFNetworkStatus;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        Toast.makeText(getActivity(),"Message",Toast.LENGTH_SHORT).show();

        tvChatFNetworkStatus = view.findViewById(R.id.tvChatFNetworkStatus);

        // check network on load
        checkNetworkStatus();
        return view;
    }
    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            tvChatFNetworkStatus.setVisibility(View.GONE); // hide banner
        } else {
            tvChatFNetworkStatus.setVisibility(View.VISIBLE); // show banner
        }
    }
}