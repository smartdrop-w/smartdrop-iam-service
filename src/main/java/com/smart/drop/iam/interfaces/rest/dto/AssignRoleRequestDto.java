package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Record DTO para solicitudes de asignación y revocación de roles.
 */
@Schema(description = "Solicitud de asignación o revocación de rol RBAC")
public record AssignRoleRequestDto(
        @Schema(description = "ID del usuario objetivo", example = "2")
        @NotNull(message = "userId is required")
        Integer userId,

        @Schema(description = "Nombre del rol a asignar o revocar", example = "ROLE_ADMIN")
        @NotBlank(message = "roleName is required")
        String roleName
) {
}
