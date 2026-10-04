package com.example.whatsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Adapter.ChatAdapter;
import com.example.whatsapp.ModelClass.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageView ivSend;

    private List<ChatModel> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    private String contactName, contactNumber;
    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etContactActivity);
        ivSend = findViewById(R.id.ivContactActivitySend);

        rvChat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatList);
        rvChat.setAdapter(chatAdapter);

        // Get data from fragment
        contactName = getIntent().getStringExtra("profileName");
        contactNumber = getIntent().getStringExtra("profileNumber");

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(contactName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // optional: adds back button
        }

        ivSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if(message.isEmpty()){
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add message to chat
            chatList.add(new ChatModel(message, true));
            chatAdapter.notifyItemInserted(chatList.size()-1);
            rvChat.scrollToPosition(chatList.size()-1);

            etMessage.setText("");

            // Send SMS
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            } else {
                sendSMS(message);
            }
        });
    }

    private void sendSMS(String message){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contactNumber, null, message, null, null);
            Toast.makeText(this, "Message sent to "+contactName, Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, "SMS failed: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == SMS_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted. Click send again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
