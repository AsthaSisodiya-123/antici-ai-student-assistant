
package com.anticiai.backend.controller;

import com.anticiai.backend.dto.auth.LoginRequest;
import com.anticiai.backend.dto.auth.LoginResponse;
import com.anticiai.backend.dto.auth.RegisterRequest;
import com.anticiai.backend.service.AuthService;
import com.anticiai.backend.service.PasswordResetService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(authService.register(request));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // SEND PASSWORD RESET OTP
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");

        return ResponseEntity.ok(
                passwordResetService.sendOtp(email)
        );
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String otp = request.get("otp");

        return ResponseEntity.ok(
                passwordResetService.verifyOtp(email, otp)
        );
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestBody Map<String, String> request) {

        String email = request.get("email");
        String resetToken = request.get("resetToken");
        String newPassword = request.get("newPassword");

        return ResponseEntity.ok(
                passwordResetService.resetPassword(
                        email,
                        resetToken,
                        newPassword
                )
        );
    }
}
