package com.anticai.studentassistant.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.DashboardResponse;
import com.anticai.studentassistant.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TextView tvStudentGreeting;

    public DashboardFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_dashboard,
                container,
                false
        );

        tvStudentGreeting = view.findViewById(
                R.id.tvStudentGreeting
        );

        loadDashboard();

        return view;
    }

    private void loadDashboard() {

        ApiService apiService =
                RetrofitClient
                        .getInstance(requireContext())
                        .create(ApiService.class);

        Call<DashboardResponse> call =
                apiService.getDashboard();

        call.enqueue(new Callback<DashboardResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<DashboardResponse> call,
                    @NonNull Response<DashboardResponse> response) {

                if (!isAdded()) {
                    return;
                }

                if (response.isSuccessful()
                        && response.body() != null) {

                    DashboardResponse dashboard =
                            response.body();

                    String name = dashboard.getName();

                    if (name != null && !name.trim().isEmpty()) {

                        tvStudentGreeting.setText(
                                "Welcome back, " + name
                        );

                    } else {

                        tvStudentGreeting.setText(
                                "Welcome back"
                        );
                    }

                } else {

                    Toast.makeText(
                            requireContext(),
                            "Dashboard error: HTTP "
                                    + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<DashboardResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) {
                    return;
                }

                Toast.makeText(
                        requireContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}