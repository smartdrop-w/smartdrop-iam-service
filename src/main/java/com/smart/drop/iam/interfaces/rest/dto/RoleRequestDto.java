package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Record DTO para solicitudes de creación/actualización de roles.
 */
@Schema(description = "Solicitud para registrar o modificar un rol RBAC")
public record RoleRequestDto(
        @Schema(description = "Nombre único del rol (convención ROLE_*)", example = "ROLE_OPERATOR")
        @NotBlank(message = "Role name is required")
        @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
        String name,

        @Schema(description = "Descripción funcional de los privilegios del rol", example = "Operador de planta con acceso a telemetría y tanques")
        @Size(max = 255, message = "Description must be <= 255 characters")
        String description
) {
}
