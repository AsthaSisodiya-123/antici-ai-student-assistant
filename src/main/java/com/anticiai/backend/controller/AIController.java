package com.anticiai.backend.controller;

import com.anticiai.backend.dto.ai.AIChatRequest;
import com.anticiai.backend.dto.ai.AIChatResponse;
import com.anticiai.backend.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

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

        AIChatResponse response =
                aiService.chat(message);

        return ResponseEntity.ok(response);
    }
}