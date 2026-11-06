package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Record DTO para solicitudes de inicio de sesión de usuario.
 */
@Schema(description = "Credenciales requeridas para autenticación de usuario")
public record LoginUserRequestDto(
        @Schema(description = "Correo electrónico del usuario registrado", example = "valeria.brewer@artesanal.pe")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email,

        @Schema(description = "Contraseña en texto claro para validación", example = "BeerPro2026!")
        @NotBlank(message = "Password is required")
        String password
) {
}
