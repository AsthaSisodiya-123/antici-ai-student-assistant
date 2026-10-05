package com.anticai.studentassistant.network;

public class AIChatRequest {

    private String message;

    public AIChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}