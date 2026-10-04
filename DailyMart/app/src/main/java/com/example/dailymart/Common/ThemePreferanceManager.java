package com.example.dailymart.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemePreferanceManager {

private static final String PREF_NAME="ThemePrefs"; //temp database
    private static final String KEY_THEME="theme_mode";
    public static void saveThemeMode(Context context , int themeMode )
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putInt(KEY_THEME,themeMode);
        editor.apply();
    }

    public static int getThemeMode(Context context)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_THEME,1);
    }
}
