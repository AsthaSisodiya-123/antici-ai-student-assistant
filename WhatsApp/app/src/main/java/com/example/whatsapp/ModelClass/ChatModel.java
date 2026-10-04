
package com.example.whatsapp.ModelClass;

public class ChatModel {
    private String message;
    private boolean isSent; // true if sent, false if received

    public ChatModel(String message, boolean isSent) {
        this.message = message;
        this.isSent = isSent;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }
}
