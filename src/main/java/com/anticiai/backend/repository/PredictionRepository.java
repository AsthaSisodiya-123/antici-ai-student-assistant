package com.anticiai.backend.repository;

import com.anticiai.backend.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
}
