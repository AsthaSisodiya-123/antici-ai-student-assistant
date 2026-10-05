package com.anticai.studentassistant.fragments.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anticai.studentassistant.R;
import com.anticai.studentassistant.network.AIChatRequest;
import com.anticai.studentassistant.network.AIChatResponse;
import com.anticai.studentassistant.network.ApiService;
import com.anticai.studentassistant.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIChatFragment extends Fragment {

    private EditText etMessage;
    private TextView btnSend;
    private LinearLayout chatContainer;

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
        chatContainer = view.findViewById(R.id.chatContainer);

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {

        String message = etMessage
                .getText()
                .toString()
                .trim();

        if (message.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Please enter a message",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        addUserMessage(message);

        etMessage.setText("");

        btnSend.setEnabled(false);

        ApiService apiService =
                RetrofitClient
                        .getInstance(requireContext())
                        .create(ApiService.class);

        AIChatRequest request =
                new AIChatRequest(message);

        Call<AIChatResponse> call =
                apiService.chat(request);

        call.enqueue(new Callback<AIChatResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<AIChatResponse> call,
                    @NonNull Response<AIChatResponse> response) {

                if (!isAdded()) {
                    return;
                }

                btnSend.setEnabled(true);

                if (response.isSuccessful()
                        && response.body() != null) {

                    String reply =
                            response.body().getReply();

                    addAIMessage(reply);

                } else {

                    addAIMessage(
                            "Sorry, I couldn't process your request. "
                                    + "Server error: "
                                    + response.code()
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<AIChatResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) {
                    return;
                }

                btnSend.setEnabled(true);

                addAIMessage(
                        "I couldn't connect to the Antici AI server. "
                                + "Please make sure the backend is running."
                );

                Toast.makeText(
                        requireContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void addUserMessage(String message) {

        TextView textView =
                new TextView(requireContext());

        textView.setText(
                "You\n\n" + message
        );

        textView.setTextColor(
                getResources().getColor(
                        android.R.color.white
                )
        );

        textView.setTextSize(13);

        textView.setPadding(
                17,
                15,
                17,
                15
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                12,
                0,
                0
        );

        textView.setLayoutParams(params);

        chatContainer.addView(textView);
    }

    private void addAIMessage(String message) {

        TextView textView =
                new TextView(requireContext());

        textView.setText(
                "✦  ANTICIAI\n\n" + message
        );

        textView.setTextColor(
                getResources().getColor(
                        android.R.color.white
                )
        );

        textView.setTextSize(13);

        textView.setPadding(
                17,
                15,
                17,
                15
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                12,
                0,
                0
        );

        textView.setLayoutParams(params);

        chatContainer.addView(textView);
    }
}