
package com.anticiai.backend.service;

import com.anticiai.backend.entity.Task;
import com.anticiai.backend.entity.User;
import com.anticiai.backend.repository.TaskRepository;
import com.anticiai.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public List<Task> getAllTasks(Long userId) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Task getTaskById(Long id, Long userId) {
        return taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new RuntimeException("Task not found"));
    }

    @Transactional
    public Task createTask(Task task, Long userId) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        User user = getUser(userId);

        task.setId(null);
        task.setUser(user);

        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("PENDING");
        }

        if (task.getPriority() == null || task.getPriority().isBlank()) {
            task.setPriority("MEDIUM");
        }

        if (task.getTaskType() == null || task.getTaskType().isBlank()) {
            task.setTaskType("PERSONAL");
        }

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask, Long userId) {
        Task existingTask = getTaskById(id, userId);

        if (updatedTask.getTitle() == null
                || updatedTask.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title is required");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setDueDate(updatedTask.getDueDate());

        if (updatedTask.getStatus() != null
                && !updatedTask.getStatus().isBlank()) {
            existingTask.setStatus(updatedTask.getStatus());
        }

        if (updatedTask.getPriority() != null
                && !updatedTask.getPriority().isBlank()) {
            existingTask.setPriority(updatedTask.getPriority());
        }

        if (updatedTask.getTaskType() != null
                && !updatedTask.getTaskType().isBlank()) {
            existingTask.setTaskType(updatedTask.getTaskType());
        }

        return taskRepository.save(existingTask);
    }

    @Transactional
    public Task markCompleted(Long id, Long userId) {
        Task task = getTaskById(id, userId);
        task.setStatus("COMPLETED");
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id, Long userId) {
        Task task = getTaskById(id, userId);
        taskRepository.delete(task);
    }
}
