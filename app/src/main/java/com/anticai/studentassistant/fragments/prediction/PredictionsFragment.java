package com.anticai.studentassistant.fragments.prediction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class PredictionsFragment extends Fragment {

    public PredictionsFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_predictions,
                container,
                false
        );

        LinearLayout predictionDbms =
                view.findViewById(R.id.predictionDbms);

        predictionDbms.setOnClickListener(v -> {

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.mainContainer,
                            new PredictionDetailFragment()
                    )
                    .addToBackStack(null)
                    .commit();

        });

        return view;
    }
}