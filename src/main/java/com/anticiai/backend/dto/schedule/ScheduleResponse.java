package com.anticiai.backend.dto.schedule;

import com.anticiai.backend.entity.ScheduleEntry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleResponse {

    private Long id;
    private String title;
    private String subject;
    private String scheduleType;
    private String dayOfWeek;
    private LocalDate scheduleDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String notes;
    private LocalDateTime createdAt;

    public ScheduleResponse(ScheduleEntry entry) {
        this.id = entry.getId();
        this.title = entry.getTitle();
        this.subject = entry.getSubject();
        this.scheduleType = entry.getScheduleType();
        this.dayOfWeek = entry.getDayOfWeek();
        this.scheduleDate = entry.getScheduleDate();
        this.startTime = entry.getStartTime();
        this.endTime = entry.getEndTime();
        this.location = entry.getLocation();
        this.notes = entry.getNotes();
        this.createdAt = entry.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getScheduleType() { return scheduleType; }
    public String getDayOfWeek() { return dayOfWeek; }
    public LocalDate getScheduleDate() { return scheduleDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getLocation() { return location; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
