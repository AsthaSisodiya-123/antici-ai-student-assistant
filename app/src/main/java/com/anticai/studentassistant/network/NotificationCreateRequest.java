
package com.anticai.studentassistant.network;

public class NotificationCreateRequest {

    private String title;
    private String message;
    private String category;

    public NotificationCreateRequest(
            String title,
            String message,
            String category
    ) {
        this.title = title;
        this.message = message;
        this.category = category;
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
}
