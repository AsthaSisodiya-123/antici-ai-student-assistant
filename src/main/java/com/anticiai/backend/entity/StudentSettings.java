
package com.anticiai.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "student_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
public class StudentSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private boolean notificationsEnabled = true;

    @Column(nullable = false)
    private boolean remindersEnabled = true;

    @Column(nullable = false, length = 30)
    private String themeMode = "system";

    public StudentSettings() {
    }

    public StudentSettings(User user) {
        this.user = user;
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

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
    }

    public boolean isRemindersEnabled() {
        return remindersEnabled;
    }

    public void setRemindersEnabled(boolean enabled) {
        this.remindersEnabled = enabled;
    }

    public String getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(String themeMode) {
        this.themeMode = themeMode;
    }
}
