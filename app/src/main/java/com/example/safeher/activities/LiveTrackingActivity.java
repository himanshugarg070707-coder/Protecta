package com.example.safeher.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.safeher.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LiveTrackingActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 2002;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private TextView tvTrackingStatus;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAccuracy;
    private TextView tvSpeed;
    private TextView tvUpdatedAt;

    private boolean isTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        tvTrackingStatus = findViewById(R.id.tvTrackingStatus);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);

        Button btnStartTracking = findViewById(R.id.btnStartTracking);
        Button btnStopTracking = findViewById(R.id.btnStopTracking);

        btnStartTracking.setOnClickListener(view -> startTracking());
        btnStopTracking.setOnClickListener(view -> stopTracking());
    }

    @SuppressLint("MissingPermission")
    private void startTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
            return;
        }

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    return;
                }

                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                float accuracy = locationResult.getLastLocation().getAccuracy();
                float speed = locationResult.getLastLocation().getSpeed();

                tvLatitude.setText(String.format(Locale.US, "Latitude: %.6f", latitude));
                tvLongitude.setText(String.format(Locale.US, "Longitude: %.6f", longitude));
                tvAccuracy.setText(String.format(Locale.US, "Accuracy: %.2f m", accuracy));
                tvSpeed.setText(String.format(Locale.US, "Speed: %.2f m/s", speed));
                tvUpdatedAt.setText("Updated: " + new SimpleDateFormat("hh:mm:ss a", Locale.US).format(new Date()));
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        isTracking = true;
        tvTrackingStatus.setText(getString(R.string.tracking_status_on));
        Toast.makeText(this, "Live tracking started", Toast.LENGTH_SHORT).show();
    }

    private void stopTracking() {
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

        isTracking = false;
        tvTrackingStatus.setText(getString(R.string.tracking_status_off));
        Toast.makeText(this, "Live tracking stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTracking) {
            stopTracking();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Toast.makeText(this, "Location permission is required for tracking", Toast.LENGTH_LONG).show();
            }
        }
    }
}
