package com.smart.drop.profiles.interfaces.rest.dto;

import java.time.LocalDateTime;

public record PreferencesResponse(
        Integer preferenceId,
        Integer profileId,
        String alertChannel,
        Boolean notificationEnabled,
        String themeMode,
        LocalDateTime updatedAt
) {
}

