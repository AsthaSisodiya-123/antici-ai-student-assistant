package com.example.dailymart.AdaptorClass;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymart.R;

public class ChatActivity extends AppCompatActivity {

    TextView tvChat;
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String CHAT_KEY = "chat_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChat = findViewById(R.id.tvChat);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load old chat history
        String oldChat = sharedPreferences.getString(CHAT_KEY, "Chat Started...");
        tvChat.setText(oldChat);

        // Get new message from intent
        String newMsg = getIntent().getStringExtra("message");

        if (newMsg != null) {
            // Append new message to old chat
            String updatedChat = oldChat + "\nYou: " + newMsg;

            // Update TextView
            tvChat.setText(updatedChat);

            // Save updated chat to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CHAT_KEY, updatedChat);
            editor.apply();
        }
    }
}
