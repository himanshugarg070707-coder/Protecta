package com.example.safeher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.safeher.R;
import com.example.safeher.services.SafetyMonitoringService;
import com.example.safeher.utils.AppSettings;
import com.example.safeher.utils.PermissionUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchMaterial switchShake = findViewById(R.id.switchShakeDetection);
        SwitchMaterial switchTap = findViewById(R.id.switchTapTrigger);
        SwitchMaterial switchVoice = findViewById(R.id.switchVoiceDetection);
        SwitchMaterial switchMotion = findViewById(R.id.switchMotionDetection);
        SwitchMaterial switchAutoSos = findViewById(R.id.switchAutoSos);
        SwitchMaterial switchMonitoringService = findViewById(R.id.switchMonitoringService);

        switchShake.setChecked(AppSettings.isShakeDetectionEnabled(this));
        switchTap.setChecked(AppSettings.isTapTriggerEnabled(this));
        switchVoice.setChecked(AppSettings.isVoiceDetectionEnabled(this));
        switchMotion.setChecked(AppSettings.isMotionDetectionEnabled(this));
        switchAutoSos.setChecked(AppSettings.isAutoSosEnabled(this));
        switchMonitoringService.setChecked(AppSettings.isMonitoringServiceEnabled(this));

        switchShake.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setShakeDetectionEnabled(this, isChecked));

        switchTap.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setTapTriggerEnabled(this, isChecked));

        switchVoice.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setVoiceDetectionEnabled(this, isChecked));

        switchMotion.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setMotionDetectionEnabled(this, isChecked));

        switchAutoSos.setOnCheckedChangeListener((buttonView, isChecked) ->
                AppSettings.setAutoSosEnabled(this, isChecked));

        switchMonitoringService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppSettings.setMonitoringServiceEnabled(this, isChecked);
            if (isChecked) {
                if (!PermissionUtils.hasLocationPermission(this)) {
                    Toast.makeText(this, "Location permission is recommended for better monitoring", Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(this, SafetyMonitoringService.class);
                ContextCompat.startForegroundService(this, intent);
                Toast.makeText(this, "Monitoring service started", Toast.LENGTH_SHORT).show();
            } else {
                stopService(new Intent(this, SafetyMonitoringService.class));
                Toast.makeText(this, "Monitoring service stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
