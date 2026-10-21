
package com.anticai.studentassistant.network;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String dueDate;
    private String status;
    private String priority;
    private String taskType;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
