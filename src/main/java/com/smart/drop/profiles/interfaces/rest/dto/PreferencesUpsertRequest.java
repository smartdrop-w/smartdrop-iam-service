package com.smart.drop.profiles.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para registrar o actualizar preferencias del perfil")
public record PreferencesUpsertRequest(
        @Schema(description = "Canal de alerta preferente (EMAIL, SMS, WHATSAPP)", example = "EMAIL")
        @Size(max = 50)
        String alertChannel,

        @Schema(description = "Habilitar o silenciar notificaciones push y reportes", example = "true")
        Boolean notificationEnabled,

        @Schema(description = "Preferencia de modo oscuro o claro (LIGHT, DARK, AUTO)", example = "DARK")
        @Size(max = 20)
        String themeMode
) {
}
