package com.anticiai.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "privacy_settings")
public class PrivacySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "location_enabled")
    private boolean locationEnabled = true;

    @Column(name = "behavior_tracking_enabled")
    private boolean behaviorTrackingEnabled = true;

    @Column(name = "notifications_enabled")
    private boolean notificationsEnabled = true;

    public PrivacySettings() {
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

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    public boolean isBehaviorTrackingEnabled() {
        return behaviorTrackingEnabled;
    }

    public void setBehaviorTrackingEnabled(boolean behaviorTrackingEnabled) {
        this.behaviorTrackingEnabled = behaviorTrackingEnabled;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}