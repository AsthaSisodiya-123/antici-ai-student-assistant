package com.anticai.studentassistant.fragments.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;

public class AIChatFragment extends Fragment {

    private EditText etMessage;
    private TextView btnSend;

    public AIChatFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_a_i_chat,
                container,
                false
        );

        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {

        String message = etMessage.getText().toString().trim();

        if (message.isEmpty()) {
            return;
        }

        etMessage.setText("");

        // AI API integration will be added later.
    }
}