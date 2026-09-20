package com.aquasort.puzzle.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Encapsulates the complete in-memory state of an active puzzle.
 */
public class GameState {
    public static final int DEFAULT_FREE_UNDOS = 3;

    private int currentLevel;
    private List<Tube> tubes;
    private int selectedTubeIndex;
    private int moveCount;
    private final Stack<Move> moveHistory;
    private int freeUndosRemaining;
    private int coins;
    private boolean isPaused;
    private boolean isCompleted;

    public GameState(int currentLevel, List<Tube> initialTubes, int initialCoins) {
        this.currentLevel = currentLevel;
        this.tubes = new ArrayList<>();
        if (initialTubes != null) {
            for (Tube t : initialTubes) {
                this.tubes.add(t.copy());
            }
        }
        this.selectedTubeIndex = -1;
        this.moveCount = 0;
        this.moveHistory = new Stack<>();
        this.freeUndosRemaining = DEFAULT_FREE_UNDOS;
        this.coins = initialCoins;
        this.isPaused = false;
        this.isCompleted = false;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public void setTubes(List<Tube> tubes) {
        this.tubes = tubes;
    }

    public Tube getTube(int index) {
        if (index >= 0 && index < tubes.size()) {
            return tubes.get(index);
        }
        return null;
    }

    public int getSelectedTubeIndex() {
        return selectedTubeIndex;
    }

    public void setSelectedTubeIndex(int selectedTubeIndex) {
        this.selectedTubeIndex = selectedTubeIndex;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public Stack<Move> getMoveHistory() {
        return moveHistory;
    }

    public int getFreeUndosRemaining() {
        return freeUndosRemaining;
    }

    public void setFreeUndosRemaining(int freeUndosRemaining) {
        this.freeUndosRemaining = Math.max(0, freeUndosRemaining);
    }

    public void decrementFreeUndos() {
        if (this.freeUndosRemaining > 0) {
            this.freeUndosRemaining--;
        }
    }

    public void addUndos(int count) {
        this.freeUndosRemaining += count;
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

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean canUndo() {
        return !moveHistory.isEmpty() && !isCompleted;
    }

    /**
     * Reverts the most recent move and decrements moveCount.
     */
    public Move undoLastMove() {
        if (!canUndo()) {
            return null;
        }
        Move lastMove = moveHistory.pop();
        Tube source = tubes.get(lastMove.getFromIndex());
        Tube dest = tubes.get(lastMove.getToIndex());
        source.revertPour(dest, lastMove.getColor(), lastMove.getCount());
        if (moveCount > 0) {
            moveCount--;
        }
        selectedTubeIndex = -1;
        return lastMove;
    }

    public void recordMove(Move move) {
        if (move != null) {
            moveHistory.push(move);
            moveCount++;
        }
    }

    /**
     * Checks if all tubes are solved (either pure or empty).
     */
    public boolean checkWinCondition() {
        for (Tube tube : tubes) {
            if (!tube.isEmpty() && !tube.isPure()) {
                return false;
            }
        }
        return true;
    }

    public GameState copy() {
        List<Tube> copiedTubes = new ArrayList<>(tubes.size());
        for (Tube t : tubes) {
            copiedTubes.add(t.copy());
        }
        GameState copy = new GameState(currentLevel, copiedTubes, coins);
        copy.selectedTubeIndex = this.selectedTubeIndex;
        copy.moveCount = this.moveCount;
        copy.freeUndosRemaining = this.freeUndosRemaining;
        copy.isPaused = this.isPaused;
        copy.isCompleted = this.isCompleted;
        copy.moveHistory.addAll(this.moveHistory);
        return copy;
    }
}
