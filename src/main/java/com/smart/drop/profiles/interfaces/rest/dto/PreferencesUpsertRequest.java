package com.smart.drop.profiles.interfaces.rest.dto;

import jakarta.validation.constraints.Size;

public record PreferencesUpsertRequest(
        @Size(max = 50) String alertChannel,
        Boolean notificationEnabled,
        @Size(max = 20) String themeMode
) {
}

