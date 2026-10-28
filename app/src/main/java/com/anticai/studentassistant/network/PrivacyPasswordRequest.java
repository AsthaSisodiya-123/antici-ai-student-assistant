
package com.anticai.studentassistant.network;

public class PrivacyPasswordRequest {

    private final String password;

    public PrivacyPasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
