package com.aquasort.puzzle.models;

/**
 * Represents player's completion status and score for a level.
 */
public class LevelProgress {
    private final int levelId;
    private boolean completed;
    private int stars; // 0, 1, 2, or 3
    private int bestMoves;

    public LevelProgress(int levelId, boolean completed, int stars, int bestMoves) {
        this.levelId = levelId;
        this.completed = completed;
        this.stars = stars;
        this.bestMoves = bestMoves;
    }

    public int getLevelId() {
        return levelId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = Math.max(this.stars, stars);
    }

    public int getBestMoves() {
        return bestMoves;
    }

    public void setBestMoves(int moves) {
        if (this.bestMoves == 0 || moves < this.bestMoves) {
            this.bestMoves = moves;
        }
    }
}
