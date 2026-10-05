package com.anticiai.backend.controller;

import com.anticiai.backend.dto.ai.AIChatRequest;
import com.anticiai.backend.dto.ai.AIChatResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @PostMapping("/chat")
    public ResponseEntity<AIChatResponse> chat(
            @RequestBody AIChatRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();
        String message = request.getMessage();

        System.out.println("===== /api/ai/chat CALLED =====");
        System.out.println("Student: " + email);
        System.out.println("Message: " + message);

        String reply = generateReply(message);

        return ResponseEntity.ok(
                new AIChatResponse(reply)
        );
    }

    private String generateReply(String message) {

        String lowerMessage = message.toLowerCase();

        // Check DBMS first
        if (lowerMessage.contains("dbms")) {
            return "For DBMS preparation, start with ER models, "
                    + "normalization, SQL queries, transactions, "
                    + "and indexing. I can create a detailed study "
                    + "plan once the academic data module is connected.";
        }

        if (lowerMessage.contains("study")
                || lowerMessage.contains("today")) {
            return "Based on your question, I recommend starting "
                    + "with your highest-priority academic task today. "
                    + "Once your schedule and tasks are connected, "
                    + "I will give you a personalized study plan.";
        }

        if (lowerMessage.contains("hello")
                || lowerMessage.contains("hi")) {
            return "Hello! I'm Antici AI. "
                    + "I'm ready to help you with your studies, "
                    + "tasks, schedule, and academic planning.";
        }

        return "I understand your question. "
                + "I'm currently being connected to your academic "
                + "data so I can provide more personalized answers.";
    }
}