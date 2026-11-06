package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record DTO para respuestas de roles.
 */
@Schema(description = "Detalle de rol y permisos RBAC")
public record RoleResponseDto(
        @Schema(description = "Identificador único del rol", example = "1")
        Integer roleId,

        @Schema(description = "Nombre del rol", example = "ROLE_ADMIN")
        String name,

        @Schema(description = "Descripción de los privilegios del rol", example = "Administrador general del sistema SmartDrop")
        String description
) {
}
