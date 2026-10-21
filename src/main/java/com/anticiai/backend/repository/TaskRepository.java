
package com.anticiai.backend.repository;

import com.anticiai.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // Get only the tasks belonging to a student
    List<Task> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find a task only when it belongs to that student
    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
