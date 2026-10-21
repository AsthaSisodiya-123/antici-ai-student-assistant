
package com.anticai.studentassistant.network;

import com.google.gson.annotations.SerializedName;

public class StudyPlanRequest {

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

    public StudyPlanRequest(
            String subject,
            String topic,
            String difficulty,
            String examDate,
            Integer targetMinutes) {

        this.subject = subject;
        this.topic = topic;
        this.difficulty = difficulty;
        this.examDate = examDate;
        this.targetMinutes = targetMinutes;
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
}
