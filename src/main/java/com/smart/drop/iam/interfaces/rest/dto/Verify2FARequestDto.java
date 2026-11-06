package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Solicitud para verificación de código de doble factor (2FA)")
public record Verify2FARequestDto(
        @Schema(description = "Email del usuario", example = "valeria.brewer@artesanal.pe")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email,

        @Schema(description = "Código numérico de 6 dígitos enviado por correo", example = "481920")
        @NotBlank(message = "Code is required")
        @Pattern(regexp = "^[0-9]{6}$", message = "2FA code must be exactly 6 digits")
        String code
) {}
