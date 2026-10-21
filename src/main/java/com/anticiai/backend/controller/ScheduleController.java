
package com.anticiai.backend.controller;

import com.anticiai.backend.dto.schedule.ScheduleResponse;
import com.anticiai.backend.entity.ScheduleEntry;
import com.anticiai.backend.repository.UserRepository;
import com.anticiai.backend.service.ScheduleService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService service;
    private final UserRepository userRepository;

    public ScheduleController(
            ScheduleService service,
            UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null ||
                authentication.getName() == null) {
            throw new AccessDeniedException("User is not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"))
                .getId();
    }

    @GetMapping
    public List<ScheduleResponse> getAll(
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return service.getAll(userId)
                .stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    @GetMapping("/type/{type}")
    public List<ScheduleResponse> getByType(
            @PathVariable String type,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return service.getByType(type, userId)
                .stream()
                .map(ScheduleResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ScheduleResponse getById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new ScheduleResponse(
                service.getById(id, userId));
    }

    @PostMapping
    public ScheduleResponse create(
            @RequestBody ScheduleEntry entry,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new ScheduleResponse(
                service.create(entry, userId));
    }

    @PutMapping("/{id}")
    public ScheduleResponse update(
            @PathVariable Long id,
            @RequestBody ScheduleEntry entry,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new ScheduleResponse(
                service.update(id, entry, userId));
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        service.delete(id, userId);

        return "Schedule deleted successfully";
    }
}
