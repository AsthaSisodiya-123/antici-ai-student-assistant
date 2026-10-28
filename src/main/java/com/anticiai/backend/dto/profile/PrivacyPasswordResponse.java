package com.anticiai.backend.dto.profile;

public class PrivacyPasswordResponse {

    private final boolean verified;
    private final String message;

    public PrivacyPasswordResponse(boolean verified, String message) {
        this.verified = verified;
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getMessage() {
        return message;
    }
}
