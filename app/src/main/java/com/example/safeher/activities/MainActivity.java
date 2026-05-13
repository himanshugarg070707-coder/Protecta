package com.example.safeher.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.safeher.R;
import com.example.safeher.services.SafetyMonitoringService;
import com.example.safeher.utils.AppSettings;
import com.example.safeher.utils.MultiTapDetector;
import com.example.safeher.utils.PanicKeywordDetector;
import com.example.safeher.utils.PermissionUtils;
import com.example.safeher.utils.SOSManager;
import com.example.safeher.utils.ShakeDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VOICE_RECOGNITION = 1201;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    private MultiTapDetector multiTapDetector;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private TextView tvServiceStatus;
    private TextView tvLocationStatus;
    private TextView tvFeatureStatus;
    private TextView tvTapInstruction;

    private String pendingSosReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        multiTapDetector = new MultiTapDetector(4, 2500);

        initViews();
        initSensors();
        initButtons();
        startMonitoringServiceIfEnabled();

        if (!PermissionUtils.hasPermissions(this, PermissionUtils.getMainPermissions())) {
            PermissionUtils.requestMainPermissions(this);
        }

        updateStatusIndicators();
        refreshLocationStatus();
    }

    private void initViews() {
        tvServiceStatus = findViewById(R.id.tvServiceStatus);
        tvLocationStatus = findViewById(R.id.tvLocationStatus);
        tvFeatureStatus = findViewById(R.id.tvFeatureStatus);
        tvTapInstruction = findViewById(R.id.tvTapInstruction);
    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        shakeDetector = new ShakeDetector(() -> runOnUiThread(() -> {
            if (AppSettings.isShakeDetectionEnabled(MainActivity.this)) {
                triggerSos("Shake pattern detected");
            }
        }));
    }

    private void initButtons() {
        Button btnSos = findViewById(R.id.btnSos);
        Button btnContacts = findViewById(R.id.btnContacts);
        Button btnTracking = findViewById(R.id.btnLiveTracking);
        Button btnFakeCall = findViewById(R.id.btnFakeCall);
        Button btnVoiceDetection = findViewById(R.id.btnVoiceDetection);
        Button btnSuggestions = findViewById(R.id.btnSafetySuggestions);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnSos.setOnClickListener(view -> handleSosTap());
        btnSos.setOnLongClickListener(view -> {
            triggerSos("Manual SOS long press");
            return true;
        });

        btnContacts.setOnClickListener(view -> startActivity(new Intent(this, EmergencyContactsActivity.class)));
        btnTracking.setOnClickListener(view -> startActivity(new Intent(this, LiveTrackingActivity.class)));
        btnFakeCall.setOnClickListener(view -> {
            Intent intent = new Intent(this, FakeCallActivity.class);
            intent.putExtra("caller_name", "Mom");
            startActivity(intent);
        });
        btnVoiceDetection.setOnClickListener(view -> startVoiceRecognition());
        btnSuggestions.setOnClickListener(view -> startActivity(new Intent(this, SafetySuggestionsActivity.class)));
        btnSettings.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void handleSosTap() {
        if (!AppSettings.isTapTriggerEnabled(this)) {
            triggerSos("Manual SOS button tap");
            return;
        }

        boolean isTriggered = multiTapDetector.registerTap();
        if (isTriggered) {
            triggerSos("SOS triggered by multiple taps");
        } else {
            Toast.makeText(this, "Tap SOS 4 times quickly to trigger alert", Toast.LENGTH_SHORT).show();
        }
    }

    private void triggerSos(String reason) {
        boolean hasSms = PermissionUtils.hasSmsPermission(this);
        boolean hasLocation = PermissionUtils.hasLocationPermission(this);

        if (!hasSms || !hasLocation) {
            pendingSosReason = reason;
            PermissionUtils.requestMainPermissions(this);
            Toast.makeText(this, "Please allow SMS and location permissions", Toast.LENGTH_LONG).show();
            return;
        }

        SOSManager.sendSosToAllContacts(this, reason, new SOSManager.SosCallback() {
            @Override
            public void onSuccess(int sentCount) {
                runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "SOS sent to " + sentCount + " contact(s)",
                        Toast.LENGTH_LONG
                ).show());
            }

            @Override
            public void onFailure(String failureReason) {
                runOnUiThread(() -> Toast.makeText(
                        MainActivity.this,
                        "SOS failed: " + failureReason,
                        Toast.LENGTH_LONG
                ).show());
            }
        });
    }

    private void startVoiceRecognition() {
        if (!AppSettings.isVoiceDetectionEnabled(this)) {
            Toast.makeText(this, "Voice detection is OFF in settings", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!PermissionUtils.hasAudioPermission(this)) {
            PermissionUtils.requestMainPermissions(this);
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, REQUEST_VOICE_RECOGNITION);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, "Speech recognition is not available on this device", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void refreshLocationStatus() {
        if (!PermissionUtils.hasLocationPermission(this)) {
            tvLocationStatus.setText(getString(R.string.location_status_missing_permission));
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        tvLocationStatus.setText(getString(R.string.location_status_not_ready));
                    } else {
                        String text = String.format(
                                Locale.US,
                                "Last location: %.5f, %.5f",
                                location.getLatitude(),
                                location.getLongitude()
                        );
                        tvLocationStatus.setText(text);
                    }
                })
                .addOnFailureListener(error -> tvLocationStatus.setText(getString(R.string.location_status_not_ready)));
    }

    private void updateStatusIndicators() {
        boolean monitorEnabled = AppSettings.isMonitoringServiceEnabled(this);
        tvServiceStatus.setText(
                monitorEnabled ? getString(R.string.monitor_status_enabled) : getString(R.string.monitor_status_disabled)
        );

        String featureStatus = "Shake: " + state(AppSettings.isShakeDetectionEnabled(this))
                + " | Tap: " + state(AppSettings.isTapTriggerEnabled(this))
                + " | Voice: " + state(AppSettings.isVoiceDetectionEnabled(this))
                + " | Motion: " + state(AppSettings.isMotionDetectionEnabled(this));
        tvFeatureStatus.setText(featureStatus);

        tvTapInstruction.setText(AppSettings.isTapTriggerEnabled(this)
                ? getString(R.string.tap_instruction_on)
                : getString(R.string.tap_instruction_off));
    }

    private String state(boolean enabled) {
        return enabled ? "ON" : "OFF";
    }

    private void startMonitoringServiceIfEnabled() {
        if (AppSettings.isMonitoringServiceEnabled(this)) {
            Intent intent = new Intent(this, SafetyMonitoringService.class);
            ContextCompat.startForegroundService(this, intent);
        }
    }

    private void registerShakeListenerIfEnabled() {
        if (sensorManager == null || accelerometer == null || shakeDetector == null) {
            return;
        }

        sensorManager.unregisterListener(shakeDetector);
        if (AppSettings.isShakeDetectionEnabled(this)) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerShakeListenerIfEnabled();
        updateStatusIndicators();
        refreshLocationStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null && shakeDetector != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VOICE_RECOGNITION && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results == null || results.isEmpty()) {
                return;
            }

            String spokenText = results.get(0);
            String detectedKeyword = PanicKeywordDetector.detectKeyword(spokenText);

            if (detectedKeyword != null) {
                triggerSos("Voice panic keyword: " + detectedKeyword);
            } else {
                Toast.makeText(this, "No panic keyword detected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionUtils.REQUEST_MAIN_PERMISSIONS) {
            if (pendingSosReason != null && PermissionUtils.hasSmsPermission(this)
                    && PermissionUtils.hasLocationPermission(this)) {
                String reason = pendingSosReason;
                pendingSosReason = null;
                triggerSos(reason);
            }
            refreshLocationStatus();
            updateStatusIndicators();
        }
    }
}
