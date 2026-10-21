package com.anticiai.backend.dto.task;

import com.anticiai.backend.entity.Task;

import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String dueDate;
    private String status;
    private String priority;
    private String taskType;
    private LocalDateTime createdAt;

    public TaskResponse(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate() == null
                ? null : task.getDueDate().toString();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.taskType = task.getTaskType();
        this.createdAt = task.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getTaskType() { return taskType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
