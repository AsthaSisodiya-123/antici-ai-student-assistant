
package com.anticai.studentassistant.network;

public class TaskRequest {

    private String title;
    private String description;
    private String dueDate;
    private String status;
    private String priority;
    private String taskType;

    public TaskRequest(
            String title,
            String description,
            String dueDate,
            String status,
            String priority,
            String taskType) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
        this.taskType = taskType;
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
}
