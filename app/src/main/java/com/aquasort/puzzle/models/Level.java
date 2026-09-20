package com.aquasort.puzzle.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Water Sort puzzle level definition.
 */
public class Level {
    private final int id;
    private final int tubeCount;
    private final int capacity;
    private final List<List<Integer>> initialTubes;
    private final int colorCount;
    private final int parMoves;

    public Level(int id, int capacity, List<List<Integer>> initialTubes) {
        this.id = id;
        this.capacity = capacity;
        this.initialTubes = new ArrayList<>();
        int colorsFound = 0;
        List<Integer> distinctColors = new ArrayList<>();
        
        for (List<Integer> tube : initialTubes) {
            List<Integer> copy = new ArrayList<>(tube);
            this.initialTubes.add(copy);
            for (int color : copy) {
                if (color > 0 && !distinctColors.contains(color)) {
                    distinctColors.add(color);
                }
            }
        }
        this.tubeCount = initialTubes.size();
        this.colorCount = distinctColors.size();
        // Estimated par moves roughly 3 to 4 moves per color
        this.parMoves = Math.max(colorCount * 3, 5);
    }

    public int getId() {
        return id;
    }

    public int getTubeCount() {
        return tubeCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<List<Integer>> getInitialTubes() {
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> tube : initialTubes) {
            copy.add(new ArrayList<>(tube));
        }
        return copy;
    }

    public int getColorCount() {
        return colorCount;
    }

    public int getParMoves() {
        return parMoves;
    }
}
