package com.smart.drop.iam.domain.exceptions;

public class TwoFactorRequiredException extends RuntimeException {
    private final String simulatedCode;

    public TwoFactorRequiredException(String email, String simulatedCode) {
        super("Two-Factor Authentication required for email: " + email);
        this.simulatedCode = simulatedCode;
    }

    public String getSimulatedCode() {
        return simulatedCode;
    }
}