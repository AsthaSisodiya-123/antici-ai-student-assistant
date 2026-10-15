
package com.anticiai.backend.controller;

import com.anticiai.backend.entity.StudySession;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.StudySessionRepository;
import com.anticiai.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-sessions")
public class StudySessionController {

    private final StudySessionRepository sessionRepository;
    private final UserRepository userRepository;

    public StudySessionController(
            StudySessionRepository sessionRepository,
            UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startSession(
            @RequestBody StartSessionRequest request,
            Authentication authentication) {

        if (request.getSubject() == null
                || request.getSubject().isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Subject is required"));
        }

        User user = findUser(authentication);

        StudySession session = new StudySession();
        session.setUser(user);
        session.setSubject(request.getSubject().trim());
        session.setStartTime(Instant.now().toString());
        session.setEndTime(null);
        session.setDurationMinutes(0);

        StudySession saved = sessionRepository.save(session);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("subject", saved.getSubject());
        result.put("startTime", saved.getStartTime());
        result.put("endTime", saved.getEndTime());
        result.put("durationMinutes", saved.getDurationMinutes());
        result.put("message", "Study session started");

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<?> finishSession(
            @PathVariable Long sessionId,
            @RequestBody(required = false) FinishSessionRequest request,
            Authentication authentication) {

        User user = findUser(authentication);

        StudySession session = sessionRepository.findById(sessionId)
                .orElse(null);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Study session not found"));
        }

        if (session.getUser() == null
                || !session.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("message", "Access denied"));
        }

        if (session.getEndTime() != null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Session is already finished"));
        }

        try {
            Instant start = Instant.parse(session.getStartTime());
            Instant end = Instant.now();

            long elapsedMinutes = Duration.between(start, end).toMinutes();
            int duration = (int) Math.min(
                    Math.max(0, elapsedMinutes), Integer.MAX_VALUE);

            if (request != null && request.getDurationMinutes() != null) {
                int requested = request.getDurationMinutes();

                if (requested < 0) {
                    return ResponseEntity.badRequest().body(
                            Map.of("message", "Duration cannot be negative"));
                }

                // Never save more minutes than elapsed wall-clock time.
                duration = (int) Math.min(
                        requested, Math.min(elapsedMinutes, Integer.MAX_VALUE));
            }

            session.setEndTime(end.toString());
            session.setDurationMinutes(duration);

            StudySession saved = sessionRepository.save(session);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("id", saved.getId());
            result.put("subject", saved.getSubject());
            result.put("startTime", saved.getStartTime());
            result.put("endTime", saved.getEndTime());
            result.put("durationMinutes", saved.getDurationMinutes());
            result.put("message", "Study session completed");

            return ResponseEntity.ok(result);

        } catch (DateTimeParseException ex) {
            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Stored session start time is invalid"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getSessions(Authentication authentication) {
        User user = findUser(authentication);

        List<StudySession> sessions = sessionRepository.findByUser(user);
        return ResponseEntity.ok(sessions);
    }

    private User findUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Student not found"));
    }

    public static class StartSessionRequest {
        private String subject;

        public StartSessionRequest() {
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }

    public static class FinishSessionRequest {
        private Integer durationMinutes;

        public FinishSessionRequest() {
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
        }
    }
}
