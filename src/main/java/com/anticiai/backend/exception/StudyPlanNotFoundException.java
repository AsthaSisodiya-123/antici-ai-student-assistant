package com.anticiai.backend.exception;

public class StudyPlanNotFoundException extends RuntimeException {

    public StudyPlanNotFoundException(Long id) {
        super("Study plan not found with ID: " + id);
    }
}
