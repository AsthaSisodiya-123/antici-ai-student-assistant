
package com.anticiai.backend.controller;

import com.anticiai.backend.entity.StudyPlan;
import com.anticiai.backend.entity.StudySession;
import com.anticiai.backend.entity.Task;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.StudyPlanRepository;
import com.anticiai.backend.repository.StudySessionRepository;
import com.anticiai.backend.repository.TaskRepository;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/privacy/history")
public class PrivacyHistoryController {

    private final TaskRepository taskRepository;
    private final StudyPlanRepository planRepository;
    private final StudySessionRepository sessionRepository;
    private final UserRepository userRepository;

    public PrivacyHistoryController(
            TaskRepository taskRepository,
            StudyPlanRepository planRepository,
            StudySessionRepository sessionRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.planRepository = planRepository;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"));
    }

    @PostMapping("/delete-selected")
    @Transactional
    public ResponseEntity<?> deleteSelected(
            @RequestBody DeleteSelectedRequest request,
            Authentication authentication) {

        User user = currentUser(authentication);

        if (request == null || request.records == null
                || request.records.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Select at least one record"));
        }

        // Reject duplicate entries and validate ownership first.
        Set<String> uniqueRecords = new HashSet<>();

        for (HistoryRecord record : request.records) {
            if (record == null || record.id == null || record.id <= 0
                    || record.type == null) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Invalid history record"));
            }

            String type = record.type.trim().toUpperCase(Locale.ROOT);
            String key = type + ":" + record.id;

            if (!uniqueRecords.add(key)) {
                return ResponseEntity.badRequest().body(
                        Map.of("message", "Duplicate history record"));
            }

            boolean owned;

            switch (type) {
                case "TASK":
                    owned = taskRepository
                            .findByIdAndUserId(record.id, user.getId())
                            .isPresent();
                    break;
                case "STUDY_PLAN":
                    owned = planRepository
                            .findByIdAndUserId(record.id, user.getId())
                            .isPresent();
                    break;
                case "STUDY_SESSION":
                    owned = sessionRepository
                            .findByIdAndUserId(record.id, user.getId())
                            .isPresent();
                    break;
                default:
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Unknown record type"));
            }

            if (!owned) {
                return ResponseEntity.status(403).body(
                        Map.of("message",
                                "A record was not found or does not belong to you"));
            }
        }

        for (HistoryRecord record : request.records) {
            String type = record.type.trim().toUpperCase(Locale.ROOT);

            switch (type) {
                case "TASK":
                    taskRepository.deleteById(record.id);
                    break;
                case "STUDY_PLAN":
                    planRepository.deleteById(record.id);
                    break;
                case "STUDY_SESSION":
                    sessionRepository.deleteById(record.id);
                    break;
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "Selected history deleted successfully",
                "deletedCount", request.records.size()
        ));
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<?> clearAllHistory(
            Authentication authentication) {

        User user = currentUser(authentication);

        List<Task> tasks =
                taskRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<StudyPlan> plans =
                planRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<StudySession> sessions = sessionRepository.findByUser(user);

        int count = tasks.size() + plans.size() + sessions.size();

        taskRepository.deleteAll(tasks);
        planRepository.deleteAll(plans);
        sessionRepository.deleteAll(sessions);

        return ResponseEntity.ok(Map.of(
                "message", "History cleared successfully",
                "deletedCount", count
        ));
    }

    public static class DeleteSelectedRequest {
        public List<HistoryRecord> records;

        public DeleteSelectedRequest() {}
    }

    public static class HistoryRecord {
        public String type;
        public Long id;

        public HistoryRecord() {}
    }
}
