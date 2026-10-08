package com.smart.drop.iam.interfaces.rest.dto;
public record ResetPasswordRequestDto(String token, String newPassword) {}