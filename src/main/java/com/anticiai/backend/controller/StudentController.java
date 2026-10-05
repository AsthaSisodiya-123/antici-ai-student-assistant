package com.anticiai.backend.controller;

import com.anticiai.backend.dto.DashboardResponse;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserRepository userRepository;

    public StudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentStudent(
            Authentication authentication
    ) {
        System.out.println("===== /api/student/me CALLED =====");
        System.out.println("Authenticated user: " + authentication.getName());

        return ResponseEntity.ok(
                Map.of(
                        "message", "JWT authentication successful",
                        "email", authentication.getName()
                )
        );
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication
    ) {
        System.out.println("===== /api/student/dashboard CALLED =====");
        System.out.println("Authenticated user: " + authentication.getName());

        String email = authentication.getName();

        System.out.println("Searching user: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Student not found")
                );

        System.out.println("User found: " + user.getName());

        DashboardResponse response =
                new DashboardResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                );

        System.out.println("Sending dashboard response");

        return ResponseEntity.ok(response);
    }
}