package com.anticiai.backend.dto.study;

import java.util.List;

public class StudyPlanResponse {

    private String title;
    private String description;
    private List<StudyPlanItem> sessions;

    public StudyPlanResponse(
            String title,
            String description,
            List<StudyPlanItem> sessions
    ) {
        this.title = title;
        this.description = description;
        this.sessions = sessions;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<StudyPlanItem> getSessions() {
        return sessions;
    }

    public static class StudyPlanItem {

        private String time;
        private Integer durationMinutes;
        private String subject;
        private String topic;
        private String priority;

        public StudyPlanItem(
                String time,
                Integer durationMinutes,
                String subject,
                String topic,
                String priority
        ) {
            this.time = time;
            this.durationMinutes = durationMinutes;
            this.subject = subject;
            this.topic = topic;
            this.priority = priority;
        }

        public String getTime() {
            return time;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }

        public String getSubject() {
            return subject;
        }

        public String getTopic() {
            return topic;
        }

        public String getPriority() {
            return priority;
        }
    }
}

