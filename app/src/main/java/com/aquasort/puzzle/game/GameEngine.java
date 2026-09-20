package com.aquasort.puzzle.game;

import com.aquasort.puzzle.models.Level;
import java.util.ArrayList;
import java.util.List;

/**
 * Core game engine orchestrating Water Sort puzzle rules, move validation,
 * undo mechanics, hint calculation, and win detection.
 */
public class GameEngine {

    public interface GameListener {
        void onTubeSelected(int tubeIndex);
        void onTubeUnselected(int tubeIndex);
        void onPourRequested(Move move, Runnable onAnimationComplete);
        void onInvalidMove(int sourceIndex, int destIndex);
        void onMoveUndone(Move move);
        void onLevelWon(int stars, int coinsEarned);
        void onStateUpdated();
    }

    private GameState gameState;
    private Level currentLevelData;
    private GameListener listener;
    private boolean isAnimating = false;

    public GameEngine() {}

    public void setGameListener(GameListener listener) {
        this.listener = listener;
    }

    public void startLevel(int levelId, int playerCoins) {
        this.currentLevelData = LevelManager.getInstance().getLevel(levelId);
        List<Tube> tubes = new ArrayList<>();
        for (List<Integer> layers : currentLevelData.getInitialTubes()) {
            tubes.add(new Tube(currentLevelData.getCapacity(), layers));
        }
        this.gameState = new GameState(levelId, tubes, playerCoins);
        this.isAnimating = false;
        if (listener != null) {
            listener.onStateUpdated();
        }
    }

    public void resumeSavedState(GameState state) {
        this.gameState = state;
        this.currentLevelData = LevelManager.getInstance().getLevel(state.getCurrentLevel());
        this.isAnimating = false;
        if (listener != null) {
            listener.onStateUpdated();
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public Level getCurrentLevelData() {
        return currentLevelData;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setAnimating(boolean animating) {
        this.isAnimating = animating;
    }

    /**
     * Handles tapping on a tube.
     */
    public void onTubeClicked(int clickedIndex) {
        if (gameState == null || gameState.isCompleted() || gameState.isPaused() || isAnimating) {
            return;
        }

        int selectedIndex = gameState.getSelectedTubeIndex();

        // 1. If nothing is selected yet
        if (selectedIndex == -1) {
            Tube tube = gameState.getTube(clickedIndex);
            if (tube != null && !tube.isEmpty()) {
                gameState.setSelectedTubeIndex(clickedIndex);
                if (listener != null) {
                    listener.onTubeSelected(clickedIndex);
                    listener.onStateUpdated();
                }
            }
            return;
        }

        // 2. If the user taps the currently selected tube again -> Deselect
        if (selectedIndex == clickedIndex) {
            gameState.setSelectedTubeIndex(-1);
            if (listener != null) {
                listener.onTubeUnselected(clickedIndex);
                listener.onStateUpdated();
            }
            return;
        }

        // 3. User tapped a different destination tube -> Attempt pour
        Tube source = gameState.getTube(selectedIndex);
        Tube dest = gameState.getTube(clickedIndex);

        if (source == null || dest == null) {
            return;
        }

        if (source.canPourInto(dest)) {
            int pourCount = source.getPourableCount(dest);
            int color = source.getTopColor();
            final Move move = new Move(selectedIndex, clickedIndex, color, pourCount);

            // Execute logic
            source.pourInto(dest, selectedIndex, clickedIndex);
            gameState.recordMove(move);
            gameState.setSelectedTubeIndex(-1);
            isAnimating = true;

            if (listener != null) {
                listener.onPourRequested(move, new Runnable() {
                    @Override
                    public void run() {
                        isAnimating = false;
                        checkWin();
                        if (listener != null) {
                            listener.onStateUpdated();
                        }
                    }
                });
            }
        } else {
            // Invalid move
            if (listener != null) {
                listener.onInvalidMove(selectedIndex, clickedIndex);
            }
            // If the clicked tube is also not empty, switch selection to it
            if (!dest.isEmpty()) {
                gameState.setSelectedTubeIndex(clickedIndex);
                if (listener != null) {
                    listener.onTubeUnselected(selectedIndex);
                    listener.onTubeSelected(clickedIndex);
                    listener.onStateUpdated();
                }
            } else {
                gameState.setSelectedTubeIndex(-1);
                if (listener != null) {
                    listener.onTubeUnselected(selectedIndex);
                    listener.onStateUpdated();
                }
            }
        }
    }

    /**
     * Reverts the last move.
     */
    public boolean undo() {
        if (gameState == null || !gameState.canUndo() || isAnimating) {
            return false;
        }

        Move undoneMove = gameState.undoLastMove();
        if (undoneMove != null) {
            if (listener != null) {
                listener.onMoveUndone(undoneMove);
                listener.onStateUpdated();
            }
            return true;
        }
        return false;
    }

    /**
     * Restarts the current level from scratch.
     */
    public void restart() {
        if (gameState == null || isAnimating) {
            return;
        }
        startLevel(gameState.getCurrentLevel(), gameState.getCoins());
    }

    /**
     * Gets hint move.
     */
    public Move getHint() {
        if (gameState == null || gameState.isCompleted() || isAnimating) {
            return null;
        }
        return LevelSolver.getHintMove(gameState.getTubes());
    }

    private void checkWin() {
        if (gameState != null && gameState.checkWinCondition()) {
            gameState.setCompleted(true);
            int stars = calculateStars(gameState.getMoveCount(), currentLevelData.getParMoves());
            int coinsWon = 25; // Standard level reward
            if (listener != null) {
                listener.onLevelWon(stars, coinsWon);
            }
        }
    }

    /**
     * Determines stars earned (1, 2, or 3) based on par moves efficiency.
     */
    public int calculateStars(int moves, int parMoves) {
        if (moves <= parMoves + 2) {
            return 3;
        } else if (moves <= parMoves + 8) {
            return 2;
        } else {
            return 1;
        }
    }
}
