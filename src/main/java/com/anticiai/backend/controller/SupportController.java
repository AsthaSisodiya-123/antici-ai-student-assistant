
package com.anticiai.backend.controller;

import com.anticiai.backend.entity.SupportRequest;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.SupportRequestRepository;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    private final UserRepository userRepository;
    private final SupportRequestRepository supportRepository;

    public SupportController(
            UserRepository userRepository,
            SupportRequestRepository supportRepository
    ) {
        this.userRepository = userRepository;
        this.supportRepository = supportRepository;
    }

    @PostMapping
    public ResponseEntity<?> submitRequest(
            Authentication authentication,
            @RequestBody SupportRequestBody request
    ) {
        User user = getAuthenticatedUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401).body(
                    Map.of("message", "Authentication required")
            );
        }

        if (request.getSubject() == null
                || request.getSubject().trim().isEmpty()
                || request.getSubject().trim().length() > 150
                || request.getMessage() == null
                || request.getMessage().trim().isEmpty()
                || request.getMessage().trim().length() > 3000) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Enter a subject and a message")
            );
        }

        SupportRequest support = new SupportRequest();
        support.setUser(user);
        support.setSubject(request.getSubject().trim());
        support.setMessage(request.getMessage().trim());

        String category = request.getCategory();

        if (category != null
                && category.matches("GENERAL|BUG|ACCOUNT|PRIVACY")) {
            support.setCategory(category);
        }

        supportRepository.save(support);

        return ResponseEntity.ok(Map.of(
                "id", support.getId(),
                "status", support.getStatus(),
                "message", "Support request submitted successfully"
        ));
    }

    @GetMapping
    public ResponseEntity<?> getMyRequests(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401).body(
                    Map.of("message", "Authentication required")
            );
        }

        List<Map<String, Object>> requests = supportRepository
                .findByUser_IdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(item -> Map.<String, Object>of(
                        "id", item.getId(),
                        "subject", item.getSubject(),
                        "message", item.getMessage(),
                        "category", item.getCategory(),
                        "status", item.getStatus(),
                        "createdAt", item.getCreatedAt().toString()
                ))
                .toList();

        return ResponseEntity.ok(requests);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        return userRepository.findByEmail(authentication.getName())
                .orElse(null);
    }

    public static class SupportRequestBody {
        private String subject;
        private String message;
        private String category;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
