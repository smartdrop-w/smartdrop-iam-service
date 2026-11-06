package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Record DTO para respuestas de usuario con soporte de token JWT.
 */
@Schema(description = "Datos de perfil y autenticación del usuario")
public record UserResponseDto(
        @Schema(description = "Identificador único del usuario", example = "2")
        Integer userId,

        @Schema(description = "Nombre completo del usuario", example = "Valeria Brewer")
        String fullName,

        @Schema(description = "Correo electrónico institucional", example = "valeria.brewer@artesanal.pe")
        String email,

        @Schema(description = "Fecha y hora de registro en la plataforma", example = "2026-11-13T23:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Token JWT de acceso emitido tras autenticación exitosa",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YWxlcmlhLmJyZXdlckBhcnRlc2FuYWwucGUiLCJ1c2VySWQiOjJ9.xyz")
        String token
) {
    public UserResponseDto(Integer userId, String fullName, String email, LocalDateTime createdAt) {
        this(userId, fullName, email, createdAt, null);
    }
}
