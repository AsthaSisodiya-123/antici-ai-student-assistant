package com.anticiai.backend.service;

import com.anticiai.backend.dto.study.StudyPlanRequest;
import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.entity.StudyPlan;
import com.anticiai.backend.exception.StudyPlanNotFoundException;
import com.anticiai.backend.mapper.StudyPlanMapper;
import com.anticiai.backend.repository.StudyPlanRepository;
import com.anticiai.backend.validation.StudyPlanValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanMapper studyPlanMapper;
    private final StudyPlanValidator studyPlanValidator;

    public StudyPlanService(
            StudyPlanRepository studyPlanRepository,
            StudyPlanMapper studyPlanMapper,
            StudyPlanValidator studyPlanValidator) {
        this.studyPlanRepository = studyPlanRepository;
        this.studyPlanMapper = studyPlanMapper;
        this.studyPlanValidator = studyPlanValidator;
    }

    // Get all study plans
    @Transactional(readOnly = true)
    public List<StudyPlanResponse> getAllPlans() {
        return studyPlanRepository.findAll()
                .stream()
                .map(studyPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get active study plans
    @Transactional(readOnly = true)
    public List<StudyPlanResponse> getActivePlans() {
        return studyPlanRepository
                .findByStatusOrderByCreatedAtDesc("ACTIVE")
                .stream()
                .map(studyPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Get a study plan by ID
    @Transactional(readOnly = true)
    public StudyPlanResponse getPlanById(Long id) {
        StudyPlan plan = studyPlanRepository.findById(id)
                .orElseThrow(() -> new StudyPlanNotFoundException(id));

        return studyPlanMapper.toResponse(plan);
    }

    // Create a study plan
    public StudyPlanResponse createPlan(StudyPlanRequest request) {
        List<String> errors = studyPlanValidator.validate(request);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }

        StudyPlan plan = studyPlanMapper.toEntity(request);
        StudyPlan savedPlan = studyPlanRepository.save(plan);

        return studyPlanMapper.toResponse(savedPlan);
    }

    // Update a study plan
    public StudyPlanResponse updatePlan(
            Long id,
            StudyPlanRequest request) {

        List<String> errors = studyPlanValidator.validate(request);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }

        StudyPlan existingPlan = studyPlanRepository.findById(id)
                .orElseThrow(() -> new StudyPlanNotFoundException(id));

        studyPlanMapper.updateEntity(existingPlan, request);

        StudyPlan updatedPlan = studyPlanRepository.save(existingPlan);

        return studyPlanMapper.toResponse(updatedPlan);
    }

    // Delete a study plan
    public void deletePlan(Long id) {
        StudyPlan plan = studyPlanRepository.findById(id)
                .orElseThrow(() -> new StudyPlanNotFoundException(id));

        studyPlanRepository.delete(plan);
    }
}
