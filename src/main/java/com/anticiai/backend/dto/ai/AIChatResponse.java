package com.anticiai.backend.dto.ai;


public class AIChatResponse {

    private String reply;

    public AIChatResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }
}
