package com.smart.drop.iam.interfaces.rest.dto;

public record ChangeNameRequestDto(
        String email,
        String newName
) {
}
