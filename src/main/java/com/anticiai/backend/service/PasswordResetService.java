package com.anticiai.backend.service;

import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetService {

    private static final long OTP_VALIDITY_SECONDS = 300; // 5 minutes
    private static final long TOKEN_VALIDITY_SECONDS = 600; // 10 minutes
    private static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${spring.mail.username}")
    private String senderEmail;

    private final Map<String, ResetData> resetRequests =
            new ConcurrentHashMap<>();

    public PasswordResetService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public synchronized Map<String, Object> sendOtp(String email) {
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException("No account found for this email"));

        String otp = String.format(
                "%06d", secureRandom.nextInt(1_000_000));

        ResetData data = new ResetData();
        data.otp = otp;
        data.otpExpiresAt =
                Instant.now().getEpochSecond() + OTP_VALIDITY_SECONDS;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(user.getEmail());
        message.setSubject("AnticiAI Password Reset OTP");
        message.setText(
                "Hello " + user.getName() + ",\n\n"
                        + "Your AnticiAI password reset OTP is: " + otp + "\n\n"
                        + "This OTP expires in 5 minutes. "
                        + "Do not share it with anyone.\n\n"
                        + "If you did not request this, you can ignore this email."
        );

        // Send first so a failed email delivery does not create a valid OTP.
        mailSender.send(message);
        resetRequests.put(normalizedEmail, data);

        return Map.of(
                "success", true,
                "message", "OTP sent to your registered email"
        );
    }

    public synchronized Map<String, Object> verifyOtp(
            String email, String otp) {

        String normalizedEmail = normalizeEmail(email);
        ResetData data = resetRequests.get(normalizedEmail);

        if (data == null || data.otp == null) {
            throw new RuntimeException(
                    "No active OTP request. Please request a new OTP");
        }

        if (Instant.now().getEpochSecond() > data.otpExpiresAt) {
            resetRequests.remove(normalizedEmail);
            throw new RuntimeException("OTP expired. Please request a new OTP");
        }

        if (data.attempts >= MAX_ATTEMPTS) {
            resetRequests.remove(normalizedEmail);
            throw new RuntimeException(
                    "Too many incorrect attempts. Please request a new OTP");
        }

        if (otp == null || !data.otp.equals(otp.trim())) {
            data.attempts++;
            throw new RuntimeException("Invalid OTP");
        }

        data.otp = null;
        data.resetToken = UUID.randomUUID().toString();
        data.resetTokenExpiresAt =
                Instant.now().getEpochSecond() + TOKEN_VALIDITY_SECONDS;

        return Map.of(
                "success", true,
                "message", "OTP verified successfully",
                "resetToken", data.resetToken
        );
    }

    public synchronized Map<String, Object> resetPassword(
            String email, String resetToken, String newPassword) {

        String normalizedEmail = normalizeEmail(email);
        ResetData data = resetRequests.get(normalizedEmail);

        if (data == null
                || data.resetToken == null
                || resetToken == null
                || !data.resetToken.equals(resetToken.trim())) {
            throw new RuntimeException(
                    "Invalid reset token. Verify your OTP again");
        }

        if (Instant.now().getEpochSecond() > data.resetTokenExpiresAt) {
            resetRequests.remove(normalizedEmail);
            throw new RuntimeException(
                    "Reset token expired. Please start again");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException(
                    "Password must contain at least 8 characters");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException("User account not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetRequests.remove(normalizedEmail);

        return Map.of(
                "success", true,
                "message", "Password reset successfully"
        );
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RuntimeException("Email is required");
        }
        return email.trim().toLowerCase();
    }

    private static class ResetData {
        private String otp;
        private long otpExpiresAt;
        private int attempts;
        private String resetToken;
        private long resetTokenExpiresAt;
    }
}
