package com.aquasort.puzzle.models;

/**
 * Encapsulates global player state (coins, current level, hints, boosters).
 */
public class PlayerData {
    private int coins;
    private int currentLevel;
    private int hintsRemaining;
    private int extraUndosRemaining;
    private int dailyRewardDay;
    private long lastDailyRewardClaimTime;
    private boolean onboardingCompleted;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private boolean vibrationEnabled;
    private boolean notificationsEnabled;
    private boolean hasRated;

    public PlayerData() {
        this.coins = 100; // Starting bonus
        this.currentLevel = 1;
        this.hintsRemaining = 3;
        this.extraUndosRemaining = 3;
        this.dailyRewardDay = 1;
        this.lastDailyRewardClaimTime = 0;
        this.onboardingCompleted = false;
        this.soundEnabled = true;
        this.musicEnabled = true;
        this.vibrationEnabled = true;
        this.notificationsEnabled = true;
        this.hasRated = false;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    public void addCoins(int amount) {
        if (amount > 0) {
            this.coins += amount;
        }
    }

    public boolean spendCoins(int amount) {
        if (amount > 0 && this.coins >= amount) {
            this.coins -= amount;
            return true;
        }
        return false;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = Math.max(1, currentLevel);
    }

    public int getHintsRemaining() {
        return hintsRemaining;
    }

    public void setHintsRemaining(int hintsRemaining) {
        this.hintsRemaining = Math.max(0, hintsRemaining);
    }

    public void addHints(int amount) {
        this.hintsRemaining += amount;
    }

    public int getExtraUndosRemaining() {
        return extraUndosRemaining;
    }

    public void setExtraUndosRemaining(int extraUndosRemaining) {
        this.extraUndosRemaining = Math.max(0, extraUndosRemaining);
    }

    public void addExtraUndos(int amount) {
        this.extraUndosRemaining += amount;
    }

    public int getDailyRewardDay() {
        return dailyRewardDay;
    }

    public void setDailyRewardDay(int dailyRewardDay) {
        this.dailyRewardDay = dailyRewardDay;
    }

    public long getLastDailyRewardClaimTime() {
        return lastDailyRewardClaimTime;
    }

    public void setLastDailyRewardClaimTime(long lastDailyRewardClaimTime) {
        this.lastDailyRewardClaimTime = lastDailyRewardClaimTime;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isVibrationEnabled() {
        return vibrationEnabled;
    }

    public void setVibrationEnabled(boolean vibrationEnabled) {
        this.vibrationEnabled = vibrationEnabled;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean hasRated() {
        return hasRated;
    }

    public void setHasRated(boolean hasRated) {
        this.hasRated = hasRated;
    }
}
