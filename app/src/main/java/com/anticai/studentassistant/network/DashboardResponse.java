package com.anticai.studentassistant.network;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class DashboardResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("studyProgress")
    private int studyProgress;

    @SerializedName("activeTasks")
    private int activeTasks;

    @SerializedName("aiStatus")
    private String aiStatus;

    @SerializedName("aiDescription")
    private String aiDescription;

    @SerializedName("predictionConfidence")
    private int predictionConfidence;

    @SerializedName("predictionTitle")
    private String predictionTitle;

    @SerializedName("predictionDetails")
    private String predictionDetails;

    @SerializedName("upcomingSessions")
    private List<UpcomingSession> upcomingSessions = new ArrayList<>();

    public String getName() {
        return name;
    }

    public int getStudyProgress() {
        return studyProgress;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public String getAiStatus() {
        return aiStatus;
    }

    public String getAiDescription() {
        return aiDescription;
    }

    public int getPredictionConfidence() {
        return predictionConfidence;
    }

    public String getPredictionTitle() {
        return predictionTitle;
    }

    public String getPredictionDetails() {
        return predictionDetails;
    }

    public List<UpcomingSession> getUpcomingSessions() {
        return upcomingSessions;
    }

    public static class UpcomingSession {

        @SerializedName("subject")
        private String subject;

        @SerializedName("time")
        private String time;

        public String getSubject() {
            return subject;
        }

        public String getTime() {
            return time;
        }
    }
}
