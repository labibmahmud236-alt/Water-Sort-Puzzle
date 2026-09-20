package com.aquasort.puzzle.models;

/**
 * Represents a single day item in the 7-day daily reward calendar.
 */
public class DailyReward {
    private final int day;
    private final int coinAmount;
    private boolean claimed;
    private boolean availableToday;

    public DailyReward(int day, int coinAmount, boolean claimed, boolean availableToday) {
        this.day = day;
        this.coinAmount = coinAmount;
        this.claimed = claimed;
        this.availableToday = availableToday;
    }

    public int getDay() {
        return day;
    }

    public int getCoinAmount() {
        return coinAmount;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public boolean isAvailableToday() {
        return availableToday;
    }

    public void setAvailableToday(boolean availableToday) {
        this.availableToday = availableToday;
    }
}
