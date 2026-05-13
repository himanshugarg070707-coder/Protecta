package com.example.safeher.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {

    private static final String PREF_NAME = "protecta_preferences";

    public static final String KEY_SHAKE_DETECTION = "shake_detection";
    public static final String KEY_TAP_TRIGGER = "tap_trigger";
    public static final String KEY_VOICE_DETECTION = "voice_detection";
    public static final String KEY_MOTION_DETECTION = "motion_detection";
    public static final String KEY_AUTO_SOS = "auto_sos";
    public static final String KEY_MONITOR_SERVICE = "monitor_service";

    private AppSettings() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isShakeDetectionEnabled(Context context) {
        return prefs(context).getBoolean(KEY_SHAKE_DETECTION, true);
    }

    public static void setShakeDetectionEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_SHAKE_DETECTION, enabled).apply();
    }

    public static boolean isTapTriggerEnabled(Context context) {
        return prefs(context).getBoolean(KEY_TAP_TRIGGER, true);
    }

    public static void setTapTriggerEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_TAP_TRIGGER, enabled).apply();
    }

    public static boolean isVoiceDetectionEnabled(Context context) {
        return prefs(context).getBoolean(KEY_VOICE_DETECTION, true);
    }

    public static void setVoiceDetectionEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_VOICE_DETECTION, enabled).apply();
    }

    public static boolean isMotionDetectionEnabled(Context context) {
        return prefs(context).getBoolean(KEY_MOTION_DETECTION, true);
    }

    public static void setMotionDetectionEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_MOTION_DETECTION, enabled).apply();
    }

    public static boolean isAutoSosEnabled(Context context) {
        return prefs(context).getBoolean(KEY_AUTO_SOS, true);
    }

    public static void setAutoSosEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_AUTO_SOS, enabled).apply();
    }

    public static boolean isMonitoringServiceEnabled(Context context) {
        return prefs(context).getBoolean(KEY_MONITOR_SERVICE, false);
    }

    public static void setMonitoringServiceEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_MONITOR_SERVICE, enabled).apply();
    }
}
