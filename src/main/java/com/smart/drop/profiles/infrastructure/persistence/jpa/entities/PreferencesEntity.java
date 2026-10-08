package com.smart.drop.profiles.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "Preferences")
public class PreferencesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PreferenceId")
    private Integer preferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProfileId")
    private ProfileEntity profile;

    @Column(name = "AlertChannel", length = 50)
    private String alertChannel;

    @Column(name = "NotificationEnabled")
    private Boolean notificationEnabled;

    @Column(name = "ThemeMode", length = 20)
    private String themeMode;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    public Integer getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Integer preferenceId) {
        this.preferenceId = preferenceId;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public String getAlertChannel() {
        return alertChannel;
    }

    public void setAlertChannel(String alertChannel) {
        this.alertChannel = alertChannel;
    }

    public Boolean getNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(Boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(String themeMode) {
        this.themeMode = themeMode;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

