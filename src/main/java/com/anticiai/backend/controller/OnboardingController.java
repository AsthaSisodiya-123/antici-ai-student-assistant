package com.anticiai.backend.controller;

import com.anticiai.backend.entity.OnboardingStatus;
import com.anticiai.backend.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(
            @RequestParam String deviceId) {

        if (!isValidDeviceId(deviceId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Valid deviceId is required"));
        }

        OnboardingStatus status =
                onboardingService.getStatus(deviceId);

        return ResponseEntity.ok(Map.of(
                "deviceId", status.getDeviceId(),
                "completed", status.isCompleted()
        ));
    }

    @PostMapping("/complete")
    public ResponseEntity<?> complete(
            @RequestParam String deviceId) {

        if (!isValidDeviceId(deviceId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Valid deviceId is required"));
        }

        OnboardingStatus status =
                onboardingService.complete(deviceId);

        return ResponseEntity.ok(Map.of(
                "message", "Onboarding completed",
                "deviceId", status.getDeviceId(),
                "completed", status.isCompleted()
        ));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(
            @RequestParam String deviceId) {

        if (!isValidDeviceId(deviceId)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Valid deviceId is required"));
        }

        OnboardingStatus status =
                onboardingService.reset(deviceId);

        return ResponseEntity.ok(Map.of(
                "message", "Onboarding reset",
                "deviceId", status.getDeviceId(),
                "completed", status.isCompleted()
        ));
    }

    private boolean isValidDeviceId(String deviceId) {
        return deviceId != null
                && !deviceId.isBlank()
                && deviceId.length() <= 150;
    }
}