package com.anticai.studentassistant.models;

import org.json.JSONException;
import org.json.JSONObject;

public class StudyPlan {

    public String id;
    public String title;
    public String subject;
    public String topic;
    public String date;
    public int durationMinutes;
    public boolean completed;

    public StudyPlan(
            String id,
            String title,
            String subject,
            String topic,
            String date,
            int durationMinutes,
            boolean completed
    ) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.topic = topic;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.completed = completed;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("title", title);
        object.put("subject", subject);
        object.put("topic", topic);
        object.put("date", date);
        object.put("durationMinutes", durationMinutes);
        object.put("completed", completed);
        return object;
    }

    public static StudyPlan fromJson(JSONObject object) {
        return new StudyPlan(
                object.optString("id"),
                object.optString("title"),
                object.optString("subject"),
                object.optString("topic"),
                object.optString("date"),
                object.optInt("durationMinutes", 25),
                object.optBoolean("completed", false)
        );
    }
}