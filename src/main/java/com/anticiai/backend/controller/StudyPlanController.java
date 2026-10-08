package com.anticiai.backend.controller;

import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.UserRepository;
import com.anticiai.backend.service.StudyPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/study-plan")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;
    private final UserRepository userRepository;

    public StudyPlanController(
            StudyPlanService studyPlanService,
            UserRepository userRepository
    ) {
        this.studyPlanService = studyPlanService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<StudyPlanResponse> getStudyPlan(
            Authentication authentication
    ) {

        System.out.println(
                "===== /api/study-plan CALLED ====="
        );

        String email = authentication.getName();

        System.out.println(
                "Generating study plan for: " + email
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student not found"
                        )
                );

        StudyPlanResponse response =
                studyPlanService.generatePlan(user);

        System.out.println(
                "Study plan generated successfully"
        );

        return ResponseEntity.ok(response);
    }
}

