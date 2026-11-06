package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para restablecimiento de contraseña mediante token")
public record ResetPasswordRequestDto(
        @Schema(description = "Token de recuperación emitido previamente", example = "d94f26ca-7c08-410a-9d0a-9b19dfb97150")
        @NotBlank(message = "Token is required")
        String token,

        @Schema(description = "Nueva contraseña para la cuenta", example = "NewSecurePass2026!")
        @NotBlank(message = "New password is required")
        @Size(min = 6, max = 50, message = "Password must have at least 6 characters")
        String newPassword
) {}
