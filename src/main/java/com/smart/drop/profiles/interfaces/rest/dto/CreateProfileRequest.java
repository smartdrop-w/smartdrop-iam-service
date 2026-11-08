package com.smart.drop.profiles.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para creación de un perfil de usuario asociado")
public record CreateProfileRequest(
        @Schema(description = "Identificador único del usuario de IAM", example = "2")
        @NotNull(message = "userId is required")
        Integer userId,

        @Schema(description = "Nombre completo del perfil", example = "Valeria Brewer")
        @Size(max = 100)
        String fullName,

        @Schema(description = "Correo electrónico de contacto", example = "valeria.brewer@artesanal.pe")
        @Email
        @Size(max = 150)
        String email,

        @Schema(description = "Código de idioma preferido", example = "es-PE")
        @Size(max = 20)
        String language,

        @Schema(description = "Habilitación global de notificaciones", example = "true")
        Boolean notificationsEnabled
) {
}
