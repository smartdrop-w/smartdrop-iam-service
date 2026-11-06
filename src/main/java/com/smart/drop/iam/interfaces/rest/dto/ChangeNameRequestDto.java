package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualización de nombre de usuario")
public record ChangeNameRequestDto(
        @Schema(description = "Email del usuario", example = "valeria.brewer@artesanal.pe")
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email,

        @Schema(description = "Nuevo nombre completo", example = "Valeria Brewer Morales")
        @NotBlank(message = "New name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String newName
) {}
