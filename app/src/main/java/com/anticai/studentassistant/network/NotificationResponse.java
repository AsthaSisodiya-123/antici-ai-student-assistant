
package com.anticai.studentassistant.network;

public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String category;
    private boolean read;
    private String createdAt;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCategory() {
        return category;
    }

    public boolean isRead() {
        return read;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
