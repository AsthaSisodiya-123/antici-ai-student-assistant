
package com.anticiai.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getPrivacyInformation() {
        return ResponseEntity.ok(Map.of(
                "title", "AnticiAI Privacy Information",
                "profileData",
                "Your profile information is used to personalize your student experience.",
                "storage",
                "Profile data and settings are stored by the application backend.",
                "security",
                "Keep your login credentials private and sign out on shared devices.",
                "support",
                "Use Help & Support to submit privacy or account-related requests."
        ));
    }
}
