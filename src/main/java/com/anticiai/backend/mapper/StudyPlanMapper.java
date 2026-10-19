package com.anticiai.backend.mapper;

import com.anticiai.backend.dto.study.StudyPlanRequest;
import com.anticiai.backend.dto.study.StudyPlanResponse;
import com.anticiai.backend.entity.StudyPlan;

import org.springframework.stereotype.Component;

@Component
public class StudyPlanMapper {

    // Convert request DTO to database entity
    public StudyPlan toEntity(StudyPlanRequest request) {
        StudyPlan plan = new StudyPlan();

        plan.setSubject(request.getSubject());
        plan.setTopic(request.getTopic());
        plan.setDifficulty(request.getDifficulty());
        plan.setExamDate(request.getExamDate());
        plan.setTargetMinutes(request.getTargetMinutes());

        if (plan.getCompletedMinutes() == null) {
            plan.setCompletedMinutes(0);
        }

        if (plan.getStatus() == null || plan.getStatus().isBlank()) {
            plan.setStatus("ACTIVE");
        }

        return plan;
    }

    // Update an existing entity
    public StudyPlan updateEntity(
            StudyPlan existingPlan,
            StudyPlanRequest request) {

        existingPlan.setSubject(request.getSubject());
        existingPlan.setTopic(request.getTopic());
        existingPlan.setDifficulty(request.getDifficulty());
        existingPlan.setExamDate(request.getExamDate());
        existingPlan.setTargetMinutes(request.getTargetMinutes());

        return existingPlan;
    }

    // Convert database entity to response DTO
    public StudyPlanResponse toResponse(StudyPlan plan) {
        return StudyPlanResponse.fromEntity(plan);
    }
}
