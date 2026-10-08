package com.anticai.studentassistant.network;

import java.util.List;

public class StudyPlanResponse {

    private String title;
    private String description;
    private List<StudyPlanItem> sessions;

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

