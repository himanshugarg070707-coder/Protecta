package com.example.safeher.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 700;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    private long shakeTimestamp;
    private int shakeCount;

    public interface OnShakeListener {
        void onShake();
    }

    private final OnShakeListener listener;

    public ShakeDetector(OnShakeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float gX = x / SensorManagerConstants.GRAVITY_EARTH;
        float gY = y / SensorManagerConstants.GRAVITY_EARTH;
        float gZ = z / SensorManagerConstants.GRAVITY_EARTH;

        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();

            if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }

            if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                shakeCount = 0;
            }

            shakeTimestamp = now;
            shakeCount++;

            if (shakeCount >= 2 && listener != null) {
                listener.onShake();
                shakeCount = 0;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op
    }

    // Separate class to keep static reference explicit and beginner-friendly.
    private static class SensorManagerConstants {
        static final float GRAVITY_EARTH = 9.80665f;
    }
}
