
package com.anticiai.backend.controller;

import com.anticiai.backend.entity.StudentSettings;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.StudentSettingsRepository;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final UserRepository userRepository;
    private final StudentSettingsRepository settingsRepository;

    public SettingsController(
            UserRepository userRepository,
            StudentSettingsRepository settingsRepository
    ) {
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
    }

    @GetMapping
    public ResponseEntity<?> getSettings(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required"));
        }

        StudentSettings settings = settingsRepository
                .findByUser_Id(user.getId())
                .orElseGet(() -> settingsRepository.save(
                        new StudentSettings(user)
                ));

        return ResponseEntity.ok(toMap(settings));
    }

    @PutMapping
    public ResponseEntity<?> updateSettings(
            Authentication authentication,
            @RequestBody SettingsRequest request
    ) {
        User user = getAuthenticatedUser(authentication);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Authentication required"));
        }

        if (request.getThemeMode() == null
                || !request.getThemeMode().matches(
                "light|dark|system"
        )) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Theme must be light, dark, or system")
            );
        }

        StudentSettings settings = settingsRepository
                .findByUser_Id(user.getId())
                .orElseGet(() -> new StudentSettings(user));

        settings.setNotificationsEnabled(
                request.isNotificationsEnabled()
        );
        settings.setRemindersEnabled(
                request.isRemindersEnabled()
        );
        settings.setThemeMode(request.getThemeMode());

        settingsRepository.save(settings);

        return ResponseEntity.ok(toMap(settings));
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

    private Map<String, Object> toMap(StudentSettings settings) {
        return Map.of(
                "notificationsEnabled",
                settings.isNotificationsEnabled(),
                "remindersEnabled",
                settings.isRemindersEnabled(),
                "themeMode",
                settings.getThemeMode()
        );
    }

    public static class SettingsRequest {

        private boolean notificationsEnabled = true;
        private boolean remindersEnabled = true;
        private String themeMode = "system";

        public boolean isNotificationsEnabled() {
            return notificationsEnabled;
        }

        public void setNotificationsEnabled(boolean enabled) {
            this.notificationsEnabled = enabled;
        }

        public boolean isRemindersEnabled() {
            return remindersEnabled;
        }

        public void setRemindersEnabled(boolean enabled) {
            this.remindersEnabled = enabled;
        }

        public String getThemeMode() {
            return themeMode;
        }

        public void setThemeMode(String themeMode) {
            this.themeMode = themeMode;
        }
    }
}
