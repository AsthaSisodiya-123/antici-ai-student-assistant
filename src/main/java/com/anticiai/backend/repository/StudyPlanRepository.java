
package com.anticiai.backend.repository;

import com.anticiai.backend.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository
        extends JpaRepository<StudyPlan, Long> {

    List<StudyPlan> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<StudyPlan> findByUserIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
            Long userId, String status);

    List<StudyPlan> findByUserIdAndSubjectIgnoreCaseOrderByCreatedAtDesc(
            Long userId, String subject);

    List<StudyPlan>
    findByUserIdAndStatusIgnoreCaseAndSubjectIgnoreCaseOrderByCreatedAtDesc(
            Long userId, String status, String subject);

    Optional<StudyPlan> findByIdAndUserId(Long id, Long userId);
}
