package com.anticai.studentassistant.models;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class OnboardingSession {

    private static final String PREFS_NAME = "AnticiAIPrefs";
    private static final String KEY_DEVICE_ID = "onboarding_device_id";
    private static final String KEY_COMPLETED = "onboarding_complete";

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static String getDeviceId(Context context) {
        SharedPreferences prefs = getPrefs(context);
        String deviceId = prefs.getString(KEY_DEVICE_ID, null);

        if (deviceId == null || deviceId.trim().isEmpty()) {
            deviceId = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply();
        }

        return deviceId;
    }

    public static void setCompleted(Context context, boolean completed) {
        getPrefs(context).edit()
                .putBoolean(KEY_COMPLETED, completed)
                .apply();
    }

    public static boolean isCompleted(Context context) {
        return getPrefs(context).getBoolean(KEY_COMPLETED, false);
    }
}
