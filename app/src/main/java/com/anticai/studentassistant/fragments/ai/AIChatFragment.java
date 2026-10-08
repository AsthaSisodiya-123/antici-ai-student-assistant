package com.anticai.studentassistant.fragments.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    private ScrollView chatScrollView;
    private TextView typingIndicator;

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
        chatScrollView = view.findViewById(R.id.chatScrollView);

        // SEND BUTTON
        btnSend.setOnClickListener(
                v -> sendMessage()
        );

        // SUGGESTION CARDS
        TextView suggestionStudy =
                view.findViewById(R.id.suggestionStudy);

        TextView suggestionPlan =
                view.findViewById(R.id.suggestionPlan);

        TextView suggestionAttention =
                view.findViewById(R.id.suggestionAttention);

        suggestionStudy.setOnClickListener(
                v -> sendSuggestion(
                        "What should I study today?"
                )
        );

        suggestionPlan.setOnClickListener(
                v -> sendSuggestion(
                        "Create a study plan for my DBMS exam."
                )
        );

        suggestionAttention.setOnClickListener(
                v -> sendSuggestion(
                        "What needs my attention next?"
                )
        );

        return view;
    }

    private void sendSuggestion(String message) {

        etMessage.setText(message);

        sendMessage();
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

        // ADD USER MESSAGE
        addUserMessage(message);

        // CLEAR INPUT
        etMessage.setText("");

        // DISABLE SEND BUTTON
        btnSend.setEnabled(false);

        // SHOW THINKING INDICATOR
        showTypingIndicator();

        scrollToBottom();

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

                // REMOVE THINKING INDICATOR
                removeTypingIndicator();

                // ENABLE SEND BUTTON
                btnSend.setEnabled(true);

                if (response.isSuccessful()
                        && response.body() != null) {

                    String reply =
                            response.body().getReply();

                    if (reply != null
                            && !reply.isBlank()) {

                        addAIMessage(reply);

                    } else {

                        addAIMessage(
                                "Antici AI returned an empty response."
                        );
                    }

                } else {

                    addAIMessage(
                            "Antici AI is temporarily unavailable. "
                                    + "Please try again in a moment."
                    );
                }

                scrollToBottom();
            }

            @Override
            public void onFailure(
                    @NonNull Call<AIChatResponse> call,
                    @NonNull Throwable t) {

                if (!isAdded()) {
                    return;
                }

                // REMOVE THINKING INDICATOR
                removeTypingIndicator();

                // ENABLE SEND BUTTON
                btnSend.setEnabled(true);

                addAIMessage(
                        "I couldn't connect to Antici AI. "
                                + "Please check your connection "
                                + "and try again."
                );

                scrollToBottom();

                Toast.makeText(
                        requireContext(),
                        "Network error",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showTypingIndicator() {

        if (typingIndicator != null) {
            return;
        }

        typingIndicator =
                new TextView(requireContext());

        typingIndicator.setText(
                "✦  ANTICIAI\n\nThinking..."
        );

        typingIndicator.setTextColor(
                getResources().getColor(
                        android.R.color.white
                )
        );

        typingIndicator.setTextSize(13);

        typingIndicator.setBackgroundResource(
                R.drawable.bg_ai_message
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                12,
                35,
                0
        );

        typingIndicator.setLayoutParams(params);

        chatContainer.addView(
                typingIndicator
        );

        scrollToBottom();
    }

    private void removeTypingIndicator() {

        if (typingIndicator != null) {

            chatContainer.removeView(
                    typingIndicator
            );

            typingIndicator = null;
        }
    }

    private void addUserMessage(String message) {

        TextView textView =
                new TextView(requireContext());

        textView.setText(
                "YOU\n\n" + message
        );

        textView.setTextColor(
                getResources().getColor(
                        android.R.color.white
                )
        );

        textView.setTextSize(13);

        textView.setBackgroundResource(
                R.drawable.bg_user_message
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                35,
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

        textView.setBackgroundResource(
                R.drawable.bg_ai_message
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(
                0,
                12,
                35,
                0
        );

        textView.setLayoutParams(params);

        chatContainer.addView(textView);
    }

    private void scrollToBottom() {

        if (chatScrollView == null) {
            return;
        }

        chatScrollView.post(() ->
                chatScrollView.fullScroll(
                        View.FOCUS_DOWN
                )
        );
    }
}

