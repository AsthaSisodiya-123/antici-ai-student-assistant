
package com.anticai.studentassistant.network;

public class FinishStudySessionRequest {

    private final int durationMinutes;

    public FinishStudySessionRequest(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
