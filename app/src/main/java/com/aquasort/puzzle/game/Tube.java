package com.aquasort.puzzle.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a test tube containing stacked liquid layers.
 */
public class Tube {
    public static final int DEFAULT_CAPACITY = 4;

    private final int capacity;
    private final List<Integer> layers;

    public Tube() {
        this(DEFAULT_CAPACITY);
    }

    public Tube(int capacity) {
        this.capacity = capacity;
        this.layers = new ArrayList<>(capacity);
    }

    public Tube(int capacity, List<Integer> initialLayers) {
        this.capacity = capacity;
        this.layers = new ArrayList<>(capacity);
        if (initialLayers != null) {
            for (Integer color : initialLayers) {
                if (color != null && color > 0 && this.layers.size() < capacity) {
                    this.layers.add(color);
                }
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int size() {
        return layers.size();
    }

    public boolean isEmpty() {
        return layers.isEmpty();
    }

    public boolean isFull() {
        return layers.size() >= capacity;
    }

    public int getAvailableCapacity() {
        return capacity - layers.size();
    }

    public List<Integer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    public int getColorAt(int index) {
        if (index >= 0 && index < layers.size()) {
            return layers.get(index);
        }
        return 0;
    }

    public int getTopColor() {
        if (layers.isEmpty()) {
            return 0;
        }
        return layers.get(layers.size() - 1);
    }

    /**
     * Returns how many consecutive matching layers are at the top.
     */
    public int getTopColorGroupCount() {
        if (layers.isEmpty()) {
            return 0;
        }
        int topColor = getTopColor();
        int count = 0;
        for (int i = layers.size() - 1; i >= 0; i--) {
            if (layers.get(i) == topColor) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * Returns true if all layers currently in the tube share the same color.
     */
    public boolean isSingleColor() {
        if (layers.isEmpty()) {
            return true;
        }
        int first = layers.get(0);
        for (int c : layers) {
            if (c != first) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if tube is completely filled to capacity with a single color.
     */
    public boolean isPure() {
        return isFull() && isSingleColor();
    }

    /**
     * Checks if liquid can be poured from this tube into dest tube according to rules.
     */
    public boolean canPourInto(Tube dest) {
        if (this == dest || this.isEmpty() || dest == null) {
            return false;
        }
        if (dest.isFull()) {
            return false;
        }
        if (dest.isEmpty()) {
            // Pouring a tube that is already pure into an empty tube is meaningless
            return !this.isPure();
        }
        return this.getTopColor() == dest.getTopColor();
    }

    /**
     * Returns number of liquid units that would pour into dest.
     */
    public int getPourableCount(Tube dest) {
        if (!canPourInto(dest)) {
            return 0;
        }
        int groupCount = getTopColorGroupCount();
        int room = dest.getAvailableCapacity();
        return Math.min(groupCount, room);
    }

    /**
     * Executes the pour move into dest.
     * Returns the Move performed, or null if invalid.
     */
    public Move pourInto(Tube dest, int fromIndex, int toIndex) {
        int count = getPourableCount(dest);
        if (count <= 0) {
            return null;
        }
        int color = getTopColor();
        for (int i = 0; i < count; i++) {
            layers.remove(layers.size() - 1);
            dest.layers.add(color);
        }
        return new Move(fromIndex, toIndex, color, count);
    }

    /**
     * Reverts a move (used for undo).
     */
    public void revertPour(Tube dest, int color, int count) {
        for (int i = 0; i < count; i++) {
            if (!dest.layers.isEmpty()) {
                dest.layers.remove(dest.layers.size() - 1);
                this.layers.add(color);
            }
        }
    }

    public void addLayer(int color) {
        if (layers.size() < capacity && color > 0) {
            layers.add(color);
        }
    }

    public void clear() {
        layers.clear();
    }

    public Tube copy() {
        Tube copy = new Tube(capacity);
        copy.layers.addAll(this.layers);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tube tube = (Tube) o;
        return capacity == tube.capacity && Objects.equals(layers, tube.layers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capacity, layers);
    }

    @Override
    public String toString() {
        return layers.toString();
    }
}
