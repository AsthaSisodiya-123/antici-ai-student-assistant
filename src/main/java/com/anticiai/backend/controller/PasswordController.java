
package com.anticiai.backend.controller;

import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/change")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in again"));
        }

        if (request == null
                || request.getOldPassword() == null
                || request.getNewPassword() == null
                || request.getOldPassword().isBlank()
                || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please fill in all password fields"));
        }

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        if (newPassword.length() < 8 || newPassword.length() > 72) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "New password must be between 8 and 72 characters"
                    ));
        }

        if (oldPassword.equals(newPassword)) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "New password must be different from the current password"
                    ));
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User account not found"));
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );
    }

    public static class ChangePasswordRequest {

        private String oldPassword;
        private String newPassword;

        public ChangePasswordRequest() {
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
