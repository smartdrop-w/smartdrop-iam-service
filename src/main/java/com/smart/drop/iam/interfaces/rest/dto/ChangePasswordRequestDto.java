package com.smart.drop.iam.interfaces.rest.dto;

public record ChangePasswordRequestDto(
        String email,
        String currentPassword,
        String newPassword
) {
}
