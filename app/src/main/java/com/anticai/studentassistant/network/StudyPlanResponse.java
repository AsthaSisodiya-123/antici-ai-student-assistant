
package com.anticai.studentassistant.network;

import com.google.gson.annotations.SerializedName;

public class StudyPlanResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("subject")
    private String subject;

    @SerializedName("topic")
    private String topic;

    @SerializedName("difficulty")
    private String difficulty;

    @SerializedName("examDate")
    private String examDate;

    @SerializedName("targetMinutes")
    private Integer targetMinutes;

    @SerializedName("completedMinutes")
    private Integer completedMinutes;

    @SerializedName("status")
    private String status;

    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getExamDate() {
        return examDate;
    }

    public Integer getTargetMinutes() {
        return targetMinutes;
    }

    public Integer getCompletedMinutes() {
        return completedMinutes;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
