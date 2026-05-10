package com.example.trabajopracticoinmobiliaria.ui.login;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.annotation.NonNull;

public final class ShakeToDialListener implements SensorEventListener {

    private static final int MIN_SAMPLE_GAP_MS = 100;
    private static final float SPEED_THRESHOLD = 850f;
    private static final long COOLDOWN_MS = 2_500L;

    private final Runnable onShake;
    private long lastSampleTime;
    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastFiredAt;

    public ShakeToDialListener(@NonNull Runnable onShake) {
        this.onShake = onShake;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastSampleTime <= MIN_SAMPLE_GAP_MS) {
            return;
        }
        long deltaTime = now - lastSampleTime;
        lastSampleTime = now;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / deltaTime * 10_000f;

        lastX = x;
        lastY = y;
        lastZ = z;

        if (speed > SPEED_THRESHOLD && now - lastFiredAt >= COOLDOWN_MS) {
            lastFiredAt = now;
            onShake.run();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
