
package com.anticiai.backend.repository;

import com.anticiai.backend.entity.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository
        extends JpaRepository<ScheduleEntry, Long> {

    // Get all schedules belonging to a student
    List<ScheduleEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Get a student's schedules by type: CLASS or EXAM
    List<ScheduleEntry> findByUserIdAndScheduleTypeIgnoreCaseOrderByCreatedAtDesc(
            Long userId,
            String scheduleType
    );

    // Find a schedule only if it belongs to that student
    Optional<ScheduleEntry> findByIdAndUserId(Long id, Long userId);
}
