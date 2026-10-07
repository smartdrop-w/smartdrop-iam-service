package com.smart.drop.profiles.interfaces.rest.dto;

import java.time.LocalDateTime;

public record ProfileResponse(
        Integer profileId,
        Integer userId,
        String fullName,
        String email,
        String language,
        Boolean notificationsEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        PreferencesResponse preferences
) {
}

