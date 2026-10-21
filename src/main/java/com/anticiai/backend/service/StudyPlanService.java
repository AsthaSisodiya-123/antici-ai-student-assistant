
package com.anticiai.backend.service;

import com.anticiai.backend.dto.study.StudyPlanRequest;
import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.entity.StudyPlan;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.exception.StudyPlanNotFoundException;
import com.anticiai.backend.mapper.StudyPlanMapper;
import com.anticiai.backend.repository.StudyPlanRepository;
import com.anticiai.backend.repository.UserRepository;
import com.anticiai.backend.validation.StudyPlanValidator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudyPlanService {

    private final StudyPlanRepository repository;
    private final StudyPlanMapper mapper;
    private final StudyPlanValidator validator;
    private final UserRepository userRepository;

    public StudyPlanService(
            StudyPlanRepository repository,
            StudyPlanMapper mapper,
            StudyPlanValidator validator,
            UserRepository userRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
        this.userRepository = userRepository;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    private StudyPlan getOwnedPlan(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new StudyPlanNotFoundException(id));
    }

    private void validateRequest(StudyPlanRequest request) {
        List<String> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", errors));
        }
    }

    @Transactional(readOnly = true)
    public List<StudyPlanResponse> getAllPlans(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudyPlanResponse> getActivePlans(Long userId) {
        return repository
                .findByUserIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
                        userId, "ACTIVE")
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudyPlanResponse getPlanById(Long id, Long userId) {
        return mapper.toResponse(getOwnedPlan(id, userId));
    }

    public StudyPlanResponse createPlan(
            StudyPlanRequest request, Long userId) {

        validateRequest(request);

        StudyPlan plan = mapper.toEntity(request);
        plan.setId(null);
        plan.setUser(getUser(userId));

        if (plan.getCompletedMinutes() == null) {
            plan.setCompletedMinutes(0);
        }

        if (plan.getStatus() == null || plan.getStatus().isBlank()) {
            plan.setStatus("ACTIVE");
        }

        if (plan.getCreatedAt() == null) {
            plan.setCreatedAt(java.time.LocalDateTime.now());
        }

        return mapper.toResponse(repository.save(plan));
    }

    public StudyPlanResponse updatePlan(
            Long id, StudyPlanRequest request, Long userId) {

        validateRequest(request);

        StudyPlan plan = getOwnedPlan(id, userId);
        mapper.updateEntity(plan, request);

        // Preserve the existing owner.
        return mapper.toResponse(repository.save(plan));
    }

    public void deletePlan(Long id, Long userId) {
        repository.delete(getOwnedPlan(id, userId));
    }
}
