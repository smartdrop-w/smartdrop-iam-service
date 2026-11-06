package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para cambio de contraseña desde perfil autenticado")
public record ChangePasswordRequestDto(
        @Schema(description = "Email del usuario", example = "valeria.brewer@artesanal.pe")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email,

        @Schema(description = "Contraseña actual para confirmación de seguridad", example = "BeerPro2026!")
        @NotBlank(message = "Current password is required")
        String currentPassword,

        @Schema(description = "Nueva contraseña deseada", example = "ModernDrop#2026")
        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 50, message = "Password must have at least 6 characters")
        String newPassword
) {}
