
package com.anticiai.backend.service;

import com.anticiai.backend.entity.ScheduleEntry;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.ScheduleRepository;
import com.anticiai.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository repository;
    private final UserRepository userRepository;

    public ScheduleService(
            ScheduleRepository repository,
            UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public List<ScheduleEntry> getAll(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<ScheduleEntry> getByType(
            String type, Long userId) {
        validateType(type);

        return repository
                .findByUserIdAndScheduleTypeIgnoreCaseOrderByCreatedAtDesc(
                        userId, type);
    }

    public ScheduleEntry getById(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new RuntimeException("Schedule not found"));
    }

    @Transactional
    public ScheduleEntry create(
            ScheduleEntry entry, Long userId) {
        validateEntry(entry);

        entry.setId(null);
        entry.setUser(getUser(userId));
        entry.setScheduleType(
                entry.getScheduleType().toUpperCase());

        return repository.save(entry);
    }

    @Transactional
    public ScheduleEntry update(
            Long id, ScheduleEntry updated, Long userId) {
        ScheduleEntry existing = getById(id, userId);
        validateEntry(updated);

        existing.setTitle(updated.getTitle());
        existing.setSubject(updated.getSubject());
        existing.setScheduleType(
                updated.getScheduleType().toUpperCase());
        existing.setDayOfWeek(updated.getDayOfWeek());
        existing.setScheduleDate(updated.getScheduleDate());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setLocation(updated.getLocation());
        existing.setNotes(updated.getNotes());

        // Keep the existing owner; never accept an owner from the request.
        return repository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        ScheduleEntry entry = getById(id, userId);
        repository.delete(entry);
    }

    private void validateEntry(ScheduleEntry entry) {
        if (entry.getTitle() == null
                || entry.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Schedule title is required");
        }

        validateType(entry.getScheduleType());

        if ("CLASS".equalsIgnoreCase(entry.getScheduleType())
                && (entry.getDayOfWeek() == null
                || entry.getDayOfWeek().isBlank())) {
            throw new IllegalArgumentException(
                    "Day of week is required for a class");
        }

        if ("EXAM".equalsIgnoreCase(entry.getScheduleType())
                && entry.getScheduleDate() == null) {
            throw new IllegalArgumentException(
                    "Exam date is required");
        }
    }

    private void validateType(String type) {
        if (type == null
                || (!type.equalsIgnoreCase("CLASS")
                && !type.equalsIgnoreCase("EXAM"))) {
            throw new IllegalArgumentException(
                    "Type must be CLASS or EXAM");
        }
    }
}
