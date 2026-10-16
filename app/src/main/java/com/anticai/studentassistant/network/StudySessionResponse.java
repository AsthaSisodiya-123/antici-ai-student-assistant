
package com.anticai.studentassistant.network;

public class StudySessionResponse {

    private Long id;
    private String subject;
    private String startTime;
    private String endTime;
    private Integer durationMinutes;
    private String message;

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getMessage() {
        return message;
    }
}
