package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado de la validación e introspección del token JWT")
public record ValidateTokenResponseDto(
        @Schema(description = "Indica si el token es válido y no ha expirado", example = "true")
        boolean valid,

        @Schema(description = "Nombre de usuario / email asociado al token", example = "valeria.brewer@artesanal.pe")
        String username,

        @Schema(description = "Lista de roles RBAC asignados al usuario en los claims", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
        List<String> roles,

        @Schema(description = "Identificador único numérico del usuario autenticado", example = "2")
        Integer userId,

        @Schema(description = "Mensaje informativo sobre el resultado de validación", example = "Token is valid")
        String message
) {
}
