package com.anticiai.backend.validation;

import com.anticiai.backend.dto.study.StudyPlanRequest;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class StudyPlanValidator {

    private static final Set<String> ALLOWED_DIFFICULTIES =
            Set.of("Easy", "Medium", "Hard");

    public List<String> validate(StudyPlanRequest request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            errors.add("Study plan request cannot be empty.");
            return errors;
        }

        if (request.getSubject() == null
                || request.getSubject().isBlank()) {
            errors.add("Subject is required.");
        }

        if (request.getTopic() == null
                || request.getTopic().isBlank()) {
            errors.add("Topic is required.");
        }

        if (request.getDifficulty() == null
                || request.getDifficulty().isBlank()) {
            errors.add("Difficulty is required.");
        } else {
            boolean validDifficulty = ALLOWED_DIFFICULTIES.stream()
                    .anyMatch(value -> value.equalsIgnoreCase(
                            request.getDifficulty().trim()));

            if (!validDifficulty) {
                errors.add("Difficulty must be Easy, Medium, or Hard.");
            }
        }

        if (request.getTargetMinutes() == null
                || request.getTargetMinutes() <= 0) {
            errors.add("Target minutes must be greater than zero.");
        }

        if (request.getExamDate() == null) {
            errors.add("Exam date is required.");
        } else if (request.getExamDate().isBefore(LocalDate.now())) {
            errors.add("Exam date cannot be in the past.");
        }

        return errors;
    }
}
