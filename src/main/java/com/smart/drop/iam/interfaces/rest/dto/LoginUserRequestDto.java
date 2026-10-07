package com.smart.drop.iam.interfaces.rest.dto;

/**
 * Record DTO para solicitudes de inicio de sesión de usuario.
 */
public record LoginUserRequestDto(
        String email,
        String password
) {
}
