package com.anticiai.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponse {

    private Long id;
    private String name;
    private String email;
    private String role;

    private int studyProgress;
    private int activeTasks;

    private String aiStatus;
    private String aiDescription;

    private int predictionConfidence;
    private String predictionTitle;
    private String predictionDetails;

    private List<UpcomingSession> upcomingSessions = new ArrayList<>();

    // Required by Jackson and other serializers
    public DashboardResponse() {
    }

    // Constructor used by StudentController
    public DashboardResponse(
            Long id,
            String name,
            String email,
            String role) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;

        // Initial defaults until connected to real data
        this.studyProgress = 0;
        this.activeTasks = 0;

        this.aiStatus = "Ready to help you study";
        this.aiDescription =
                "Complete a study session to receive personalized insights.";

        this.predictionConfidence = 0;
        this.predictionTitle =
                "Your study recommendation will appear here.";
        this.predictionDetails =
                "Build your learning history to receive recommendations.";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStudyProgress() {
        return studyProgress;
    }

    public void setStudyProgress(int studyProgress) {
        this.studyProgress = studyProgress;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
        this.activeTasks = activeTasks;
    }

    public String getAiStatus() {
        return aiStatus;
    }

    public void setAiStatus(String aiStatus) {
        this.aiStatus = aiStatus;
    }

    public String getAiDescription() {
        return aiDescription;
    }

    public void setAiDescription(String aiDescription) {
        this.aiDescription = aiDescription;
    }

    public int getPredictionConfidence() {
        return predictionConfidence;
    }

    public void setPredictionConfidence(int predictionConfidence) {
        this.predictionConfidence = predictionConfidence;
    }

    public String getPredictionTitle() {
        return predictionTitle;
    }

    public void setPredictionTitle(String predictionTitle) {
        this.predictionTitle = predictionTitle;
    }

    public String getPredictionDetails() {
        return predictionDetails;
    }

    public void setPredictionDetails(String predictionDetails) {
        this.predictionDetails = predictionDetails;
    }

    public List<UpcomingSession> getUpcomingSessions() {
        return upcomingSessions;
    }

    public void setUpcomingSessions(
            List<UpcomingSession> upcomingSessions) {
        this.upcomingSessions = upcomingSessions;
    }

    public static class UpcomingSession {

        private String subject;
        private String time;

        public UpcomingSession() {
        }

        public UpcomingSession(String subject, String time) {
            this.subject = subject;
            this.time = time;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
