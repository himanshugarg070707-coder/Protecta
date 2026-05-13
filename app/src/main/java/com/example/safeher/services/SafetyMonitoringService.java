package com.example.safeher.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.safeher.R;
import com.example.safeher.activities.MainActivity;
import com.example.safeher.utils.AppSettings;
import com.example.safeher.utils.PermissionUtils;
import com.example.safeher.utils.SOSManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.ArrayDeque;
import java.util.Deque;

public class SafetyMonitoringService extends Service implements SensorEventListener {

    private static final String CHANNEL_ID = "protecta_monitoring_channel";
    private static final int NOTIFICATION_ID = 1101;
    private static final long AUTO_SOS_COOLDOWN_MS = 60_000;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private float lastLinearAcceleration;
    private long lastHighMotionTimestamp;
    private long lastAutoSosTimestamp;
    private final Deque<Long> runningPeaks = new ArrayDeque<>();

    private Location lastKnownLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification(getString(R.string.monitor_service_active)));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNotification(getString(R.string.monitor_service_active));
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!AppSettings.isMotionDetectionEnabled(this)) {
            return;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
        float linearAcceleration = Math.abs(magnitude - SensorManager.GRAVITY_EARTH);
        long now = System.currentTimeMillis();

        if (linearAcceleration > 10f) {
            lastHighMotionTimestamp = now;
            runningPeaks.addLast(now);
        }

        clearOldPeaks(now);

        boolean runningPattern = runningPeaks.size() >= 8;
        boolean suddenStop = lastLinearAcceleration > 12f
                && linearAcceleration < 2f
                && (now - lastHighMotionTimestamp) < 2500;
        boolean abnormalSpike = linearAcceleration > 18f;

        String triggerReason = null;
        if (abnormalSpike) {
            triggerReason = "Abnormal movement spike detected";
        } else if (suddenStop) {
            triggerReason = "Sudden stop detected";
        } else if (runningPattern) {
            triggerReason = "Running pattern detected";
        }

        if (triggerReason != null && AppSettings.isAutoSosEnabled(this)) {
            triggerAutoSos(triggerReason);
        }

        lastLinearAcceleration = linearAcceleration;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No-op
    }

    private void clearOldPeaks(long now) {
        while (!runningPeaks.isEmpty() && now - runningPeaks.peekFirst() > 10_000) {
            runningPeaks.removeFirst();
        }
    }

    private void triggerAutoSos(String reason) {
        long now = System.currentTimeMillis();
        if (now - lastAutoSosTimestamp < AUTO_SOS_COOLDOWN_MS) {
            return;
        }

        lastAutoSosTimestamp = now;

        String fullReason = "Auto SOS: " + reason;
        SOSManager.sendSosToAllContacts(getApplicationContext(), fullReason, new SOSManager.SosCallback() {
            @Override
            public void onSuccess(int sentCount) {
                String message = "Auto SOS sent to " + sentCount + " contact(s)";
                updateNotification(message);
            }

            @Override
            public void onFailure(String failureReason) {
                updateNotification("Auto SOS failed: " + failureReason);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!PermissionUtils.hasLocationPermission(this)) {
            return;
        }

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 6000)
                .setMinUpdateIntervalMillis(3000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                lastKnownLocation = locationResult.getLastLocation();
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
    }

    private Notification buildNotification(String contentText) {
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String locationSuffix = "";
        if (lastKnownLocation != null) {
            locationSuffix = String.format(" (%.5f, %.5f)",
                    lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude());
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_shield)
                .setContentTitle(getString(R.string.monitor_service_title))
                .setContentText(contentText + locationSuffix)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText + locationSuffix))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
    }

    private void updateNotification(String contentText) {
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, buildNotification(contentText));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.monitor_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(getString(R.string.monitor_channel_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
