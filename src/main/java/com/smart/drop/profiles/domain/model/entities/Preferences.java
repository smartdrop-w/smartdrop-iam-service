package com.smart.drop.profiles.domain.model.entities;

import java.time.LocalDateTime;

/**
 * Immutable domain entity for profile preferences.
 */
public record Preferences(
        Integer preferenceId,
        Integer profileId,
        String alertChannel,
        Boolean notificationEnabled,
        String themeMode,
        LocalDateTime updatedAt
) {
    public Preferences {
        if (themeMode != null && themeMode.length() > 20) {
            throw new IllegalArgumentException("themeMode must be <= 20 characters");
        }
        if (alertChannel != null && alertChannel.length() > 50) {
            throw new IllegalArgumentException("alertChannel must be <= 50 characters");
        }
    }

    public static Preferences create(Integer profileId, String alertChannel, Boolean notificationEnabled, String themeMode) {
        return new Preferences(null, profileId, alertChannel, notificationEnabled, themeMode, LocalDateTime.now());
    }

    public Preferences withUpdatedValues(String alertChannel, Boolean notificationEnabled, String themeMode) {
        return new Preferences(
                preferenceId,
                profileId,
                alertChannel,
                notificationEnabled,
                themeMode,
                LocalDateTime.now()
        );
    }
}

