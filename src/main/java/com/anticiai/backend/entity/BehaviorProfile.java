package com.anticiai.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "behavior_profiles")
public class BehaviorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "preferred_study_time")
    private String preferredStudyTime;

    @Column(name = "average_study_duration")
    private Integer averageStudyDuration;

    @Column(name = "preferred_study_day")
    private String preferredStudyDay;

    public BehaviorProfile() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPreferredStudyTime() {
        return preferredStudyTime;
    }

    public void setPreferredStudyTime(String preferredStudyTime) {
        this.preferredStudyTime = preferredStudyTime;
    }

    public Integer getAverageStudyDuration() {
        return averageStudyDuration;
    }

    public void setAverageStudyDuration(Integer averageStudyDuration) {
        this.averageStudyDuration = averageStudyDuration;
    }

    public String getPreferredStudyDay() {
        return preferredStudyDay;
    }

    public void setPreferredStudyDay(String preferredStudyDay) {
        this.preferredStudyDay = preferredStudyDay;
    }
}