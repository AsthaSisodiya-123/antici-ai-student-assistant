
package com.anticiai.backend.controller;

import com.anticiai.backend.dto.task.TaskResponse;
import com.anticiai.backend.entity.Task;
import com.anticiai.backend.repository.UserRepository;
import com.anticiai.backend.service.TaskService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    public TaskController(
            TaskService taskService,
            UserRepository userRepository) {
        this.taskService = taskService;
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
    public List<TaskResponse> getAllTasks(
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return taskService.getAllTasks(userId)
                .stream()
                .map(TaskResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new TaskResponse(
                taskService.getTaskById(id, userId));
    }

    @PostMapping
    public TaskResponse createTask(
            @RequestBody Task task,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new TaskResponse(
                taskService.createTask(task, userId));
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @RequestBody Task task,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new TaskResponse(
                taskService.updateTask(id, task, userId));
    }

    @PatchMapping("/{id}/complete")
    public TaskResponse markCompleted(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        return new TaskResponse(
                taskService.markCompleted(id, userId));
    }

    @DeleteMapping("/{id}")
    public String deleteTask(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);

        taskService.deleteTask(id, userId);

        return "Task deleted successfully";
    }
}
