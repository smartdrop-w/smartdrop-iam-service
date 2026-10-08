package com.smart.drop.profiles.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 100) String fullName,
        @Email @Size(max = 150) String email,
        @Size(max = 20) String language,
        Boolean notificationsEnabled
) {
}

