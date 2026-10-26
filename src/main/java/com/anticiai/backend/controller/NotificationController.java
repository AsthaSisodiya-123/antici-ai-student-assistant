
package com.anticiai.backend.controller;

import com.anticiai.backend.dto.NotificationCreateRequest;
import com.anticiai.backend.dto.NotificationResponse;
import com.anticiai.backend.entity.Notification;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.NotificationRepository;
import com.anticiai.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in to access notifications."
            );
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found."
                ));
    }

    // GET: Get all notifications for the logged-in user
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        List<NotificationResponse> notifications =
                notificationRepository
                        .findByUserOrderByCreatedAtDesc(user)
                        .stream()
                        .map(NotificationResponse::new)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(notifications);
    }

    // GET: Get unread notification count
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        long count =
                notificationRepository.countByUserAndReadFalse(user);

        return ResponseEntity.ok(count);
    }

    // POST: Create a notification for the logged-in user
    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestBody NotificationCreateRequest request,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        if (request == null
                || request.getTitle() == null
                || request.getTitle().isBlank()
                || request.getTitle().trim().length() > 150
                || request.getMessage() == null
                || request.getMessage().isBlank()
                || request.getMessage().trim().length() > 1000) {

            return ResponseEntity.badRequest()
                    .body("A valid title and message are required.");
        }

        String category = request.getCategory() == null
                || request.getCategory().isBlank()
                ? "GENERAL"
                : request.getCategory().trim().toUpperCase();

        Set<String> allowedCategories = Set.of(
                "GENERAL",
                "AI",
                "TASK",
                "SCHEDULE",
                "STUDY"
        );

        if (!allowedCategories.contains(category)) {
            return ResponseEntity.badRequest()
                    .body("Invalid category. Use GENERAL, AI, TASK, SCHEDULE or STUDY.");
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(request.getTitle().trim());
        notification.setMessage(request.getMessage().trim());
        notification.setCategory(category);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new NotificationResponse(saved));
    }

    // PATCH: Mark a notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        Notification notification = notificationRepository
                .findByIdAndUser(id, user)
                .orElse(null);

        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notification not found.");
        }

        notification.setRead(true);

        Notification saved = notificationRepository.save(notification);

        return ResponseEntity.ok(new NotificationResponse(saved));
    }

    // DELETE: Delete a notification belonging to the logged-in user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {

        User user = getCurrentUser(authentication);

        Notification notification = notificationRepository
                .findByIdAndUser(id, user)
                .orElse(null);

        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notification not found.");
        }

        notificationRepository.delete(notification);

        return ResponseEntity.noContent().build();
    }
}
