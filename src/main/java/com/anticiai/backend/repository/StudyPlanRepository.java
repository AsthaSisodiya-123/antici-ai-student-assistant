package com.anticiai.backend.repository;

import com.anticiai.backend.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {

    List<StudyPlan> findByStatusOrderByCreatedAtDesc(String status);

    List<StudyPlan> findBySubjectIgnoreCaseOrderByCreatedAtDesc(String subject);

    List<StudyPlan> findByStatusAndSubjectIgnoreCaseOrderByCreatedAtDesc(
            String status,
            String subject
    );
}
