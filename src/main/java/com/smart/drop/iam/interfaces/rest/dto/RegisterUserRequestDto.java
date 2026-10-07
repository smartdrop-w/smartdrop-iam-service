package com.smart.drop.iam.interfaces.rest.dto;

/**
 * Record DTO para solicitudes de registro de usuario.
 */
public record RegisterUserRequestDto(
        String fullName,
        String email,
        String password
) {
}

