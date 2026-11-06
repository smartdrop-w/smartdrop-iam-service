package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Record DTO para solicitudes de registro de usuario.
 */
@Schema(description = "Datos para el registro inicial de una nueva cuenta de usuario")
public record RegisterUserRequestDto(
        @Schema(description = "Nombre completo del usuario", example = "Valeria Brewer")
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @Schema(description = "Correo electrónico único para la cuenta", example = "valeria.brewer@artesanal.pe")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email,

        @Schema(description = "Contraseña segura de acceso", example = "BeerPro2026!")
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50, message = "Password must have at least 6 characters")
        String password
) {
}
