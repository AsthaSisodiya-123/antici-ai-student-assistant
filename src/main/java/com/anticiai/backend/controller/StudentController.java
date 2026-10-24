package com.anticiai.backend.controller;

import com.anticiai.backend.dto.DashboardResponse;
import com.anticiai.backend.dto.profile.ProfileUpdateRequest;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserRepository userRepository;

    public StudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET: Retrieve the authenticated student's profile.
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentStudent(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(buildProfileResponse(user));
    }

    // PUT: Update the authenticated student's profile.
    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentStudent(
            Authentication authentication,
            @RequestBody ProfileUpdateRequest request) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (request == null
                || request.getName() == null
                || request.getName().trim().isEmpty()) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", "Name is required.")
            );
        }

        if (request.getName().trim().length() > 150
                || exceedsLength(request.getCollege(), 150)
                || exceedsLength(request.getCourse(), 150)
                || exceedsLength(request.getYear(), 50)) {

            return ResponseEntity.badRequest().body(
                    Map.of("message", "One or more profile fields are too long.")
            );
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Student not found.")
            );
        }

        user.setName(request.getName().trim());

        if (request.getCollege() != null) {
            user.setCollege(cleanOptional(request.getCollege()));
        }

        if (request.getCourse() != null) {
            user.setCourse(cleanOptional(request.getCourse()));
        }

        if (request.getYear() != null) {
            user.setYear(cleanOptional(request.getYear()));
        }

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(buildProfileResponse(savedUser));
    }

    // Existing dashboard endpoint.
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication) {

        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        DashboardResponse response = new DashboardResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    private boolean exceedsLength(String value, int maxLength) {
        return value != null && value.trim().length() > maxLength;
    }

    private String cleanOptional(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Map<String, Object> buildProfileResponse(User user) {
        Map<String, Object> profile = new LinkedHashMap<>();

        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole());
        profile.put("college", user.getCollege());
        profile.put("course", user.getCourse());
        profile.put("year", user.getYear());

        return profile;
    }
}
