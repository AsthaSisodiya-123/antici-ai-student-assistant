
package com.anticai.studentassistant.network;

public class ScheduleRequest {

    private String title;
    private String subject;
    private String scheduleType;
    private String dayOfWeek;
    private String scheduleDate;
    private String startTime;
    private String endTime;
    private String location;
    private String notes;

    public ScheduleRequest(
            String title,
            String subject,
            String scheduleType,
            String dayOfWeek,
            String scheduleDate,
            String startTime,
            String endTime,
            String location,
            String notes) {

        this.title = title;
        this.subject = subject;
        this.scheduleType = scheduleType;
        this.dayOfWeek = dayOfWeek;
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.notes = notes;
    }

    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getScheduleType() { return scheduleType; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getScheduleDate() { return scheduleDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getNotes() { return notes; }
}
