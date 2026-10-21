
package com.anticiai.backend.controller;

import com.anticiai.backend.dto.study.StudyPlanRequest;
import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.repository.UserRepository;
import com.anticiai.backend.service.StudyPlanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-plan")
public class StudyPlanController {

    private final StudyPlanService service;
    private final UserRepository userRepository;

    public StudyPlanController(
            StudyPlanService service,
            UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null ||
                authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"))
                .getId();
    }

    @GetMapping
    public ResponseEntity<List<StudyPlanResponse>> getAll(
            Authentication authentication) {
        return ResponseEntity.ok(
                service.getAllPlans(currentUserId(authentication)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<StudyPlanResponse>> getActive(
            Authentication authentication) {
        return ResponseEntity.ok(
                service.getActivePlans(currentUserId(authentication)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyPlanResponse> getById(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(
                service.getPlanById(id, currentUserId(authentication)));
    }

    @PostMapping
    public ResponseEntity<StudyPlanResponse> create(
            @RequestBody StudyPlanRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createPlan(
                        request, currentUserId(authentication)));
    }

    @PostMapping("/generate")
    public ResponseEntity<StudyPlanResponse> generate(
            @RequestBody StudyPlanRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createPlan(
                        request, currentUserId(authentication)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyPlanResponse> update(
            @PathVariable Long id,
            @RequestBody StudyPlanRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                service.updatePlan(
                        id, request, currentUserId(authentication)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        service.deletePlan(id, currentUserId(authentication));
        return ResponseEntity.noContent().build();
    }
}
