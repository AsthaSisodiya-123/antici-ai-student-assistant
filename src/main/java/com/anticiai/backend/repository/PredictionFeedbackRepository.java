package com.anticiai.backend.repository;

import com.anticiai.backend.entity.PredictionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionFeedbackRepository extends JpaRepository<PredictionFeedback, Long> {
}