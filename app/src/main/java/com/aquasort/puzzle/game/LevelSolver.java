package com.aquasort.puzzle.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * High-performance Water Sort puzzle solver and hint provider.
 * Uses canonical state representation and BFS search with symmetry pruning.
 */
public class LevelSolver {

    public static class SolutionResult {
        public final boolean isSolvable;
        public final List<Move> solutionMoves;

        public SolutionResult(boolean isSolvable, List<Move> solutionMoves) {
            this.isSolvable = isSolvable;
            this.solutionMoves = solutionMoves != null ? solutionMoves : Collections.emptyList();
        }
    }

    private static class Node {
        final List<Tube> tubes;
        final Node parent;
        final Move moveFromParent;
        final int depth;

        Node(List<Tube> tubes, Node parent, Move moveFromParent, int depth) {
            this.tubes = tubes;
            this.parent = parent;
            this.moveFromParent = moveFromParent;
            this.depth = depth;
        }

        boolean isSolved() {
            for (Tube tube : tubes) {
                if (!tube.isEmpty() && !tube.isPure()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Solves the given tube configuration.
     * Returns SolutionResult containing isSolvable and sequence of Moves.
     */
    public static SolutionResult solve(List<Tube> initialTubes, int maxDepth) {
        if (initialTubes == null || initialTubes.isEmpty()) {
            return new SolutionResult(false, null);
        }

        List<Tube> startTubes = new ArrayList<>(initialTubes.size());
        for (Tube t : initialTubes) {
            startTubes.add(t.copy());
        }

        Node root = new Node(startTubes, null, null, 0);
        if (root.isSolved()) {
            return new SolutionResult(true, Collections.emptyList());
        }

        Queue<Node> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(root);
        visited.add(getCanonicalState(startTubes));

        int maxIterations = 80000;
        int iterations = 0;

        while (!queue.isEmpty() && iterations++ < maxIterations) {
            Node current = queue.poll();

            if (current.depth >= maxDepth) {
                continue;
            }

            int n = current.tubes.size();
            boolean pouredIntoEmptyAlready = false;

            for (int i = 0; i < n; i++) {
                Tube src = current.tubes.get(i);
                if (src.isEmpty() || src.isPure()) {
                    continue; // Skip empty or already completed pure tubes
                }

                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    Tube dst = current.tubes.get(j);

                    if (dst.isEmpty()) {
                        if (pouredIntoEmptyAlready) {
                            continue; // Symmetry pruning: only pour into one empty tube per state
                        }
                        if (src.isSingleColor()) {
                            continue; // Pointless to move pure segment to empty tube
                        }
                    }

                    if (src.canPourInto(dst)) {
                        if (dst.isEmpty()) {
                            pouredIntoEmptyAlready = true;
                        }

                        // Clone state for next node
                        List<Tube> nextTubes = new ArrayList<>(n);
                        for (Tube t : current.tubes) {
                            nextTubes.add(t.copy());
                        }

                        Tube nextSrc = nextTubes.get(i);
                        Tube nextDst = nextTubes.get(j);
                        Move move = nextSrc.pourInto(nextDst, i, j);

                        if (move == null) continue;

                        String canonical = getCanonicalState(nextTubes);
                        if (!visited.contains(canonical)) {
                            visited.add(canonical);
                            Node child = new Node(nextTubes, current, move, current.depth + 1);
                            if (child.isSolved()) {
                                return new SolutionResult(true, reconstructPath(child));
                            }
                            queue.add(child);
                        }
                    }
                }
            }
        }

        return new SolutionResult(false, null);
    }

    /**
     * Finds the next best move from the current game state to serve as a Hint.
     */
    public static Move getHintMove(List<Tube> currentTubes) {
        SolutionResult result = solve(currentTubes, 40);
        if (result.isSolvable && !result.solutionMoves.isEmpty()) {
            return result.solutionMoves.get(0);
        }
        // Fallback: any valid move that doesn't just undo immediately
        int n = currentTubes.size();
        for (int i = 0; i < n; i++) {
            Tube src = currentTubes.get(i);
            if (src.isEmpty() || src.isPure()) continue;
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                Tube dst = currentTubes.get(j);
                if (src.canPourInto(dst)) {
                    int count = src.getPourableCount(dst);
                    if (count > 0) {
                        return new Move(i, j, src.getTopColor(), count);
                    }
                }
            }
        }
        return null;
    }

    private static List<Move> reconstructPath(Node target) {
        List<Move> path = new ArrayList<>();
        Node curr = target;
        while (curr.parent != null) {
            path.add(curr.moveFromParent);
            curr = curr.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static String getCanonicalState(List<Tube> tubes) {
        String[] tubeStrings = new String[tubes.size()];
        for (int i = 0; i < tubes.size(); i++) {
            Tube t = tubes.get(i);
            StringBuilder sb = new StringBuilder();
            for (int c : t.getLayers()) {
                sb.append((char) ('0' + c));
            }
            tubeStrings[i] = sb.toString();
        }
        Arrays.sort(tubeStrings);
        StringBuilder result = new StringBuilder();
        for (String s : tubeStrings) {
            result.append(s).append('|');
        }
        return result.toString();
    }
}
