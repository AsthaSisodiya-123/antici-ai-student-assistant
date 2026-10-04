package com.example.dailymart.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailymart.R;

public abstract class BaseFragment extends Fragment {

    protected TextView tvNetworkStatus;
    private BroadcastReceiver networkReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate child fragment layout
        View root = inflater.inflate(getLayoutId(), container, false);

        // Find the network status banner (must exist in each fragment layout)
        tvNetworkStatus = root.findViewById(R.id.tvNetworkStatus);

        // Check once at startup
        checkNetworkStatus(requireContext());

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register network change receiver
        registerNetworkReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister to avoid leaks
        if (networkReceiver != null) {
            requireContext().unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
    }

    protected abstract int getLayoutId();

    private void registerNetworkReceiver() {
        if (networkReceiver == null) {
            networkReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    checkNetworkStatus(context);
                }
            };
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            requireContext().registerReceiver(networkReceiver, filter);
        }
    }

    protected void checkNetworkStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (tvNetworkStatus != null) {
            if (isConnected) {
                tvNetworkStatus.setVisibility(View.GONE);
            } else {
                tvNetworkStatus.setVisibility(View.VISIBLE);
                tvNetworkStatus.setText("You are offline");
            }
        }
    }
}
