package com.smart.drop.profiles.domain.model.aggregates;

import com.smart.drop.profiles.domain.model.entities.Preferences;

import java.time.LocalDateTime;

/**
 * Aggregate root for user profile.
 */
public record Profile(
        Integer profileId,
        Integer userId,
        String fullName,
        String email,
        String language,
        Boolean notificationsEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Preferences preferences
) {
    public Profile {
        if (fullName != null && fullName.length() > 100) {
            throw new IllegalArgumentException("fullName must be <= 100 characters");
        }
        if (email != null && email.length() > 150) {
            throw new IllegalArgumentException("email must be <= 150 characters");
        }
        if (language != null && language.length() > 20) {
            throw new IllegalArgumentException("language must be <= 20 characters");
        }
    }

    public static Profile create(Integer userId, String fullName, String email, String language, Boolean notificationsEnabled) {
        LocalDateTime now = LocalDateTime.now();
        return new Profile(null, userId, fullName, email, language, notificationsEnabled, now, now, null);
    }

    public Profile withUpdatedBasicData(String fullName, String email, String language, Boolean notificationsEnabled) {
        return new Profile(
                profileId,
                userId,
                fullName,
                email,
                language,
                notificationsEnabled,
                createdAt,
                LocalDateTime.now(),
                preferences
        );
    }

    public Profile withPreferences(Preferences newPreferences) {
        return new Profile(
                profileId,
                userId,
                fullName,
                email,
                language,
                notificationsEnabled,
                createdAt,
                LocalDateTime.now(),
                newPreferences
        );
    }
}

