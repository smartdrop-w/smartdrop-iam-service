package com.smart.drop.iam.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para validar e introspeccionar un token JWT emitido por SmartDrop")
public record ValidateTokenRequestDto(
        @Schema(description = "Token JWT firmado a validar (con o sin prefijo 'Bearer ')",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2YWxlcmlhLmJyZXdlckBhcnRlc2FuYWwucGUiLCJ1c2VySWQiOjIsImZ1bGxOYW1lIjoiVmFsZXJpYSBCcmV3ZXIiLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDA2MDQ4MDB9.xyzSignature")
        @NotBlank(message = "Token string is required")
        String token
) {
}
