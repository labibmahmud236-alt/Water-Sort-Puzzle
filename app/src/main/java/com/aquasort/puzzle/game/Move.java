package com.aquasort.puzzle.game;

/**
 * Represents a liquid pour move from one tube to another.
 */
public class Move {
    private final int fromIndex;
    private final int toIndex;
    private final int color;
    private final int count;

    public Move(int fromIndex, int toIndex, int color, int count) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.color = color;
        this.count = count;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public int getColor() {
        return color;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Move{" + fromIndex + " -> " + toIndex + ", color=" + color + ", count=" + count + "}";
    }
}
