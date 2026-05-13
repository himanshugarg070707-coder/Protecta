package com.example.safeher.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.safeher.R;

public class FakeCallActivity extends AppCompatActivity {

    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        TextView tvCallerName = findViewById(R.id.tvCallerName);
        TextView tvCallState = findViewById(R.id.tvCallState);
        Button btnAccept = findViewById(R.id.btnAcceptCall);
        Button btnReject = findViewById(R.id.btnRejectCall);

        String callerName = getIntent().getStringExtra("caller_name");
        if (callerName == null || callerName.trim().isEmpty()) {
            callerName = "Unknown Caller";
        }
        tvCallerName.setText(callerName);

        startAlertToneAndVibration();

        btnAccept.setOnClickListener(view -> {
            stopAlertToneAndVibration();
            tvCallState.setText("Call connected. Stay calm.");
            btnAccept.setEnabled(false);
            btnReject.setText("End");
        });

        btnReject.setOnClickListener(view -> {
            stopAlertToneAndVibration();
            finish();
        });
    }

    private void startAlertToneAndVibration() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        if (ringtone != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone.setLooping(true);
            }
            ringtone.play();
        }

        startVibration();
    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("deprecation")
    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }

        long[] pattern = {0, 500, 500};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
        } else {
            vibrator.vibrate(pattern, 0);
        }
    }

    private void stopAlertToneAndVibration() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlertToneAndVibration();
    }
}
