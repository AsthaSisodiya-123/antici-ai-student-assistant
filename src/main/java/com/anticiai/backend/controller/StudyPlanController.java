package com.anticiai.backend.controller;

import com.anticiai.backend.dto.study.StudyPlanRequest;
import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.service.StudyPlanService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/study-plan")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    public StudyPlanController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }

    // Get all study plans
    @GetMapping
    public ResponseEntity<List<StudyPlanResponse>> getAllPlans() {
        return ResponseEntity.ok(studyPlanService.getAllPlans());
    }

    // Get active study plans
    @GetMapping("/active")
    public ResponseEntity<List<StudyPlanResponse>> getActivePlans() {
        return ResponseEntity.ok(studyPlanService.getActivePlans());
    }

    // Get a study plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<StudyPlanResponse> getPlanById(
            @PathVariable Long id) {
        return ResponseEntity.ok(studyPlanService.getPlanById(id));
    }

    // Create a study plan
    @PostMapping
    public ResponseEntity<StudyPlanResponse> createPlan(
            @RequestBody StudyPlanRequest request) {
        StudyPlanResponse response = studyPlanService.createPlan(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Generate a study plan from the supplied request data
    @PostMapping("/generate")
    public ResponseEntity<StudyPlanResponse> generateStudyPlan(
            @RequestBody StudyPlanRequest request) {

        StudyPlanResponse response = studyPlanService.createPlan(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update a study plan
    @PutMapping("/{id}")
    public ResponseEntity<StudyPlanResponse> updatePlan(
            @PathVariable Long id,
            @RequestBody StudyPlanRequest request) {

        return ResponseEntity.ok(
                studyPlanService.updatePlan(id, request)
        );
    }

    // Delete a study plan
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        studyPlanService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
