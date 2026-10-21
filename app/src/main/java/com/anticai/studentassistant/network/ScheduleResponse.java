
package com.anticai.studentassistant.network;

public class ScheduleResponse {

    private Long id;
    private String title;
    private String subject;
    private String scheduleType;
    private String dayOfWeek;
    private String scheduleDate;
    private String startTime;
    private String endTime;
    private String location;
    private String notes;
    private String createdAt;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getScheduleType() { return scheduleType; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getScheduleDate() { return scheduleDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getNotes() { return notes; }
    public String getCreatedAt() { return createdAt; }
}
