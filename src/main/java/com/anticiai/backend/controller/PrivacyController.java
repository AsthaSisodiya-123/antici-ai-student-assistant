
package com.anticiai.backend.controller;

import com.anticiai.backend.dto.profile.PrivacyPasswordRequest;
import com.anticiai.backend.dto.profile.PrivacyPasswordResponse;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PrivacyController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/verify-password")
    public ResponseEntity<PrivacyPasswordResponse> verifyPassword(
            @RequestBody PrivacyPasswordRequest request,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || request == null
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PrivacyPasswordResponse(
                            false,
                            "Password verification failed."
                    ));
        }

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElse(null);

        if (user == null
                || user.getPassword() == null
                || !passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PrivacyPasswordResponse(
                            false,
                            "Incorrect password."
                    ));
        }

        return ResponseEntity.ok(
                new PrivacyPasswordResponse(
                        true,
                        "Password verified."
                )
        );
    }
}
