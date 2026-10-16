
package com.anticai.studentassistant.network;

public class StartStudySessionRequest {

    private final String subject;

    public StartStudySessionRequest(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
}
