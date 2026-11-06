package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para habilitar autenticación de doble factor (2FA)")
public record Enable2FARequestDto(
        @Schema(description = "Email del usuario para activación de 2FA", example = "valeria.brewer@artesanal.pe")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email
) {}
