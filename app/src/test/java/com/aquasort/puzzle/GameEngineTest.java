package com.aquasort.puzzle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.aquasort.puzzle.game.GameState;
import com.aquasort.puzzle.game.LevelManager;
import com.aquasort.puzzle.game.LevelSolver;
import com.aquasort.puzzle.game.Move;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.Level;
import com.aquasort.puzzle.utils.Constants;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameEngineTest {

    @Test
    public void testTubePouringRules() {
        Tube src = new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_BLUE, Constants.COLOR_BLUE));
        Tube dst = new Tube(4, Arrays.asList(Constants.COLOR_GREEN, Constants.COLOR_BLUE));

        // Can pour matching blue onto blue
        assertTrue("Source should be able to pour into destination with matching top", src.canPourInto(dst));
        assertEquals("Should be able to pour 2 matching layers", 2, src.getPourableCount(dst));

        Move move = src.pourInto(dst, 0, 1);
        assertNotNull(move);
        assertEquals(Constants.COLOR_BLUE, move.getColor());
        assertEquals(2, move.getCount());

        assertEquals(1, src.size());
        assertEquals(Constants.COLOR_RED, src.getTopColor());

        assertEquals(4, dst.size());
        assertTrue(dst.isFull());
        assertFalse(dst.isPure()); // Contains Green and Blue
    }

    @Test
    public void testPurityAndWinCondition() {
        List<Tube> tubes = new ArrayList<>();
        tubes.add(new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_RED, Constants.COLOR_RED, Constants.COLOR_RED)));
        tubes.add(new Tube(4, Arrays.asList(Constants.COLOR_BLUE, Constants.COLOR_BLUE, Constants.COLOR_BLUE, Constants.COLOR_BLUE)));
        tubes.add(new Tube(4, Collections.<Integer>emptyList()));

        GameState state = new GameState(1, tubes, 100);
        assertTrue("Should detect victory when all tubes are pure or empty", state.checkWinCondition());
    }

    @Test
    public void testUndoMechanism() {
        Tube src = new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_CYAN));
        Tube dst = new Tube(4, Collections.<Integer>emptyList());

        List<Tube> tubes = new ArrayList<>();
        tubes.add(src);
        tubes.add(dst);

        GameState state = new GameState(1, tubes, 100);
        Move move = src.pourInto(dst, 0, 1);
        state.recordMove(move);

        assertEquals(1, state.getMoveCount());
        assertEquals(1, src.size());
        assertEquals(1, dst.size());

        state.undoLastMove();
        assertEquals(0, state.getMoveCount());
        assertEquals(2, state.getTube(0).size());
        assertEquals(Constants.COLOR_CYAN, state.getTube(0).getTopColor());
        assertEquals(0, state.getTube(1).size());
    }

    @Test
    public void testHintGeneration() {
        Level level = LevelManager.getInstance().getLevel(1);
        assertNotNull(level);
        List<Tube> tubes = new ArrayList<>();
        for (List<Integer> layers : level.getInitialTubes()) {
            tubes.add(new Tube(level.getCapacity(), layers));
        }
        Move hint = LevelSolver.getHintMove(tubes);
        assertNotNull("Hint must not be null for valid level", hint);
        assertTrue("Hint must be a valid move", tubes.get(hint.getFromIndex()).canPourInto(tubes.get(hint.getToIndex())));
    }

    @Test
    public void testAll100LevelsExistAndValid() {
        LevelManager manager = LevelManager.getInstance();
        for (int i = 1; i <= 100; i++) {
            Level level = manager.getLevel(i);
            assertNotNull("Level " + i + " must exist", level);
            assertEquals("Level ID must match", i, level.getId());
            assertTrue("Level " + i + " must have tubes", level.getTubeCount() >= 4);
            assertTrue("Level " + i + " must have colors", level.getColorCount() >= 3);
        }
    }
}
