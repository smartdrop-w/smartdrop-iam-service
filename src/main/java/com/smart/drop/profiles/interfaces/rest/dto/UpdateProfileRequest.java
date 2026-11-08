package com.smart.drop.profiles.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para actualización de datos generales del perfil")
public record UpdateProfileRequest(
        @Schema(description = "Nombre completo del usuario", example = "Valeria Brewer Morales")
        @Size(max = 100)
        String fullName,

        @Schema(description = "Nuevo correo electrónico de contacto", example = "valeria.brewer@cerveceriaperu.com")
        @Email
        @Size(max = 150)
        String email,

        @Schema(description = "Idioma de la interfaz", example = "en-US")
        @Size(max = 20)
        String language,

        @Schema(description = "Habilitar notificaciones", example = "false")
        Boolean notificationsEnabled
) {
}
