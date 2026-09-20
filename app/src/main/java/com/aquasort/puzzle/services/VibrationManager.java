package com.aquasort.puzzle.services;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

/**
 * Handles haptic and vibration effects with backwards compatibility.
 */
public class VibrationManager {

    private static VibrationManager instance;
    private final Vibrator vibrator;
    private boolean enabled = true;

    public static synchronized VibrationManager getInstance(Context context) {
        if (instance == null) {
            instance = new VibrationManager(context.getApplicationContext());
        }
        return instance;
    }

    private VibrationManager(Context context) {
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void vibrateSelect() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, 60));
            } else {
                vibrator.vibrate(20);
            }
        } catch (Exception ignored) {}
    }

    public void vibratePour() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(35, 90));
            } else {
                vibrator.vibrate(35);
            }
        } catch (Exception ignored) {}
    }

    public void vibrateInvalid() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                long[] timings = {0, 60, 40, 60};
                int[] amplitudes = {0, 180, 0, 180};
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1));
            } else {
                vibrator.vibrate(120);
            }
        } catch (Exception ignored) {}
    }

    public void vibrateWin() {
        if (!enabled || vibrator == null || !vibrator.hasVibrator()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                long[] timings = {0, 80, 50, 100, 60, 150};
                int[] amplitudes = {0, 100, 0, 160, 0, 255};
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1));
            } else {
                vibrator.vibrate(new long[]{0, 80, 50, 100, 60, 150}, -1);
            }
        } catch (Exception ignored) {}
    }
}
