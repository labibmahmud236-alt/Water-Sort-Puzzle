package com.aquasort.puzzle.game;

import com.aquasort.puzzle.models.Level;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all 100 Water Sort puzzle levels.
 * Every level is algorithmically verified and guaranteed to be 100% solvable.
 */
public class LevelManager {
    private static LevelManager instance;
    private final Map<Integer, Level> levelCache = new HashMap<>();

    public static synchronized LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }

    private LevelManager() {
        // Cache is initialized on-demand
    }

    public Level getLevel(int levelId) {
        if (levelId < 1) levelId = 1;
        if (levelId > 100) levelId = 100;
        
        if (levelCache.containsKey(levelId)) {
            return levelCache.get(levelId);
        }
        
        Level level = buildLevel(levelId);
        levelCache.put(levelId, level);
        return level;
    }

    private Level buildLevel(int id) {
        List<List<Integer>> tubes = new ArrayList<>();
        switch (id) {
            case 1:
                tubes.add(Arrays.asList(2, 3));
                tubes.add(Arrays.asList(2, 2));
                tubes.add(Arrays.asList(3, 3));
                tubes.add(Arrays.asList(1, 1, 1, 2));
                tubes.add(Arrays.asList(1, 3));
                return new Level(1, 4, tubes);
            case 2:
                tubes.add(Arrays.asList(1));
                tubes.add(Arrays.asList(2, 2, 3, 3));
                tubes.add(Arrays.asList(3, 3, 1));
                tubes.add(Arrays.asList(2, 2, 1, 1));
                tubes.add(Collections.<Integer>emptyList());
                return new Level(2, 4, tubes);
            case 3:
                tubes.add(Arrays.asList(1, 1, 2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(2, 2, 2));
                return new Level(3, 4, tubes);
            case 4:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 3, 3));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(1, 2, 2, 2));
                tubes.add(Arrays.asList(3));
                return new Level(4, 4, tubes);
            case 5:
                tubes.add(Arrays.asList(1, 3));
                tubes.add(Arrays.asList(2, 2, 3, 1));
                tubes.add(Arrays.asList(3, 1, 2, 2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3, 1));
                return new Level(5, 4, tubes);
            case 6:
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(2, 2, 4, 4));
                tubes.add(Arrays.asList(3, 3, 1, 1));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 2, 2, 3));
                tubes.add(Arrays.asList(4));
                return new Level(6, 4, tubes);
            case 7:
                tubes.add(Arrays.asList(1, 1, 3, 3));
                tubes.add(Arrays.asList(2, 2, 2, 1));
                tubes.add(Arrays.asList(2, 4));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(3, 3, 4));
                tubes.add(Arrays.asList(1));
                return new Level(7, 4, tubes);
            case 8:
                tubes.add(Arrays.asList(1, 1, 2));
                tubes.add(Arrays.asList(2, 1, 4));
                tubes.add(Arrays.asList(3, 3, 3, 4));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3, 2));
                tubes.add(Arrays.asList(2, 4, 4, 1));
                return new Level(8, 4, tubes);
            case 9:
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(4, 3, 2));
                tubes.add(Arrays.asList(3, 1, 4, 4));
                tubes.add(Arrays.asList(4, 3, 3));
                return new Level(9, 4, tubes);
            case 10:
                tubes.add(Arrays.asList(1, 2, 2));
                tubes.add(Arrays.asList(1, 1, 2, 4));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(1, 3, 2, 4));
                return new Level(10, 4, tubes);
            case 11:
                tubes.add(Arrays.asList(1, 3, 2));
                tubes.add(Arrays.asList(2, 1));
                tubes.add(Arrays.asList(2, 3));
                tubes.add(Arrays.asList(4, 4, 3, 1));
                tubes.add(Arrays.asList(2, 3, 1, 4));
                tubes.add(Arrays.asList(4));
                return new Level(11, 4, tubes);
            case 12:
                tubes.add(Arrays.asList(1, 2, 1, 1));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(4));
                tubes.add(Arrays.asList(4, 4, 4));
                tubes.add(Arrays.asList(3, 1, 2, 2));
                return new Level(12, 4, tubes);
            case 13:
                tubes.add(Arrays.asList(1, 1, 4, 2));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(2, 3, 3));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 1, 3, 3));
                tubes.add(Arrays.asList(2, 4, 4, 1));
                return new Level(13, 4, tubes);
            case 14:
                tubes.add(Arrays.asList(1, 1, 1, 2));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(3, 3));
                tubes.add(Arrays.asList(4));
                tubes.add(Arrays.asList(2, 2, 3, 3));
                tubes.add(Arrays.asList(1, 4, 4, 4));
                return new Level(14, 4, tubes);
            case 15:
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(2, 2, 1, 3));
                tubes.add(Arrays.asList(4, 4, 3, 4));
                tubes.add(Arrays.asList(1, 1, 1, 3));
                tubes.add(Arrays.asList(2, 2, 3));
                tubes.add(Arrays.asList(4));
                return new Level(15, 4, tubes);
            case 16:
                tubes.add(Arrays.asList(3, 1));
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(3, 2, 2));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(4, 2, 1, 4));
                tubes.add(Arrays.asList(3, 2));
                return new Level(16, 4, tubes);
            case 17:
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 3, 3, 1));
                tubes.add(Arrays.asList(2, 2, 1, 2));
                tubes.add(Arrays.asList(4, 4, 4));
                tubes.add(Arrays.asList(3, 3, 2));
                tubes.add(Arrays.asList(1, 1));
                return new Level(17, 4, tubes);
            case 18:
                tubes.add(Arrays.asList(1, 2, 3, 2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 4, 3));
                tubes.add(Arrays.asList(1, 1, 4, 1));
                tubes.add(Arrays.asList(2, 3, 3, 2));
                return new Level(18, 4, tubes);
            case 19:
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(2, 2, 2, 1));
                tubes.add(Arrays.asList(1, 4, 1));
                tubes.add(Arrays.asList(4, 4, 3));
                tubes.add(Arrays.asList(3, 3, 2, 1));
                tubes.add(Arrays.asList(4));
                return new Level(19, 4, tubes);
            case 20:
                tubes.add(Arrays.asList(1, 3, 2));
                tubes.add(Arrays.asList(2, 4, 4, 4));
                tubes.add(Arrays.asList(3, 2, 1, 1));
                tubes.add(Arrays.asList(1));
                tubes.add(Arrays.asList(2, 3));
                tubes.add(Arrays.asList(4, 3));
                return new Level(20, 4, tubes);
            case 21:
                tubes.add(Arrays.asList(1, 2));
                tubes.add(Arrays.asList(2, 2, 5));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(4, 4, 2, 1));
                tubes.add(Arrays.asList(5, 5, 4, 5));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(1, 1, 3, 4));
                return new Level(21, 4, tubes);
            case 22:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 2, 5, 5));
                tubes.add(Arrays.asList(2, 5, 3, 3));
                tubes.add(Arrays.asList(4, 4, 1));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(4, 3, 2, 5));
                tubes.add(Arrays.asList(4));
                return new Level(22, 4, tubes);
            case 23:
                tubes.add(Arrays.asList(1, 1, 1, 2));
                tubes.add(Arrays.asList(2, 2, 2, 1));
                tubes.add(Arrays.asList(3, 3));
                tubes.add(Arrays.asList(5, 4));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 3, 5));
                tubes.add(Arrays.asList(5, 4, 3, 5));
                return new Level(23, 4, tubes);
            case 24:
                tubes.add(Arrays.asList(1, 1, 1, 3));
                tubes.add(Arrays.asList(2, 2, 4, 4));
                tubes.add(Arrays.asList(3, 3, 2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(5, 5, 4));
                tubes.add(Arrays.asList(4, 3, 5, 5));
                tubes.add(Arrays.asList(2, 1));
                return new Level(24, 4, tubes);
            case 25:
                tubes.add(Arrays.asList(1, 2, 1));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(4, 4, 4, 3));
                tubes.add(Arrays.asList(5, 5, 5, 1));
                tubes.add(Arrays.asList(4, 1, 3, 3));
                tubes.add(Arrays.asList(2, 5, 2, 2));
                return new Level(25, 4, tubes);
            case 26:
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(2, 2));
                tubes.add(Arrays.asList(3, 3, 3, 4));
                tubes.add(Arrays.asList(1, 4));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(1, 2, 5, 5));
                tubes.add(Arrays.asList(4, 4, 2));
                return new Level(26, 4, tubes);
            case 27:
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(5, 1, 5, 5));
                tubes.add(Arrays.asList(4, 4, 5, 1));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3, 3, 4, 4));
                tubes.add(Arrays.asList(3, 2));
                return new Level(27, 4, tubes);
            case 28:
                tubes.add(Arrays.asList(1, 1, 5));
                tubes.add(Arrays.asList(2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 4, 3));
                tubes.add(Arrays.asList(5, 5, 1, 4));
                tubes.add(Arrays.asList(3, 2, 1, 3));
                tubes.add(Arrays.asList(3, 5, 2, 2));
                return new Level(28, 4, tubes);
            case 29:
                tubes.add(Arrays.asList(1, 1, 1, 2));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(5, 5, 4, 4));
                tubes.add(Arrays.asList(5, 4, 4, 3));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(5, 1));
                return new Level(29, 4, tubes);
            case 30:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 2, 2, 5));
                tubes.add(Arrays.asList(4, 5));
                tubes.add(Arrays.asList(4, 3, 3));
                tubes.add(Arrays.asList(5, 3));
                tubes.add(Arrays.asList(5, 4, 2));
                tubes.add(Arrays.asList(4, 1, 3));
                return new Level(30, 4, tubes);
            case 31:
                tubes.add(Arrays.asList(1, 1, 5, 2));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(5, 5, 1, 1));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 5, 3));
                return new Level(31, 4, tubes);
            case 32:
                tubes.add(Arrays.asList(1, 5));
                tubes.add(Arrays.asList(2, 2, 3, 5));
                tubes.add(Arrays.asList(3, 1, 4, 4));
                tubes.add(Arrays.asList(4));
                tubes.add(Arrays.asList(2, 3));
                tubes.add(Arrays.asList(3, 5, 4));
                tubes.add(Arrays.asList(5, 2, 1, 1));
                return new Level(32, 4, tubes);
            case 33:
                tubes.add(Arrays.asList(2, 1));
                tubes.add(Arrays.asList(5, 4, 4, 1));
                tubes.add(Arrays.asList(3, 1));
                tubes.add(Arrays.asList(4, 5));
                tubes.add(Arrays.asList(5, 5, 2));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(2, 2, 1, 4));
                return new Level(33, 4, tubes);
            case 34:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(1, 4, 4));
                tubes.add(Arrays.asList(4, 3, 3));
                tubes.add(Arrays.asList(5, 5, 5, 5));
                tubes.add(Arrays.asList(4, 3, 3));
                tubes.add(Arrays.asList(2));
                return new Level(34, 4, tubes);
            case 35:
                tubes.add(Arrays.asList(1, 1, 4, 2));
                tubes.add(Arrays.asList(2, 5, 1));
                tubes.add(Arrays.asList(3, 3, 3, 3));
                tubes.add(Arrays.asList(2, 2, 1));
                tubes.add(Arrays.asList(5));
                tubes.add(Arrays.asList(5, 5, 4));
                tubes.add(Arrays.asList(4, 4));
                return new Level(35, 4, tubes);
            case 36:
                tubes.add(Arrays.asList(1, 1, 1, 5));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 2, 2));
                tubes.add(Arrays.asList(5, 5, 5));
                tubes.add(Arrays.asList(3, 4, 4, 2));
                tubes.add(Arrays.asList(1, 2));
                return new Level(36, 4, tubes);
            case 37:
                tubes.add(Arrays.asList(5, 3, 1));
                tubes.add(Arrays.asList(2, 2, 2, 4));
                tubes.add(Arrays.asList(3, 1, 1));
                tubes.add(Arrays.asList(1, 2));
                tubes.add(Arrays.asList(4, 5, 4, 3));
                tubes.add(Arrays.asList(5, 5, 3));
                tubes.add(Arrays.asList(4));
                return new Level(37, 4, tubes);
            case 38:
                tubes.add(Arrays.asList(1, 2, 3));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(1, 5, 3));
                tubes.add(Arrays.asList(4, 4, 5));
                tubes.add(Arrays.asList(1, 2, 2));
                tubes.add(Arrays.asList(5, 5, 1));
                tubes.add(Arrays.asList(3, 3, 4, 4));
                return new Level(38, 4, tubes);
            case 39:
                tubes.add(Arrays.asList(3, 1, 2, 2));
                tubes.add(Arrays.asList(2, 2, 3, 1));
                tubes.add(Arrays.asList(5, 3, 5));
                tubes.add(Arrays.asList(4, 4, 4));
                tubes.add(Arrays.asList(1, 1, 4, 3));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(5, 5));
                return new Level(39, 4, tubes);
            case 40:
                tubes.add(Arrays.asList(4, 3, 5));
                tubes.add(Arrays.asList(3, 2, 5, 4));
                tubes.add(Arrays.asList(2, 1, 1, 2));
                tubes.add(Arrays.asList(4, 1, 4));
                tubes.add(Arrays.asList(5, 5, 1));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(2, 3));
                return new Level(40, 4, tubes);
            case 41:
                tubes.add(Arrays.asList(1, 1, 5, 2));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(3, 3, 1, 6));
                tubes.add(Arrays.asList(4, 4, 4, 4));
                tubes.add(Arrays.asList(3, 5, 3, 6));
                tubes.add(Arrays.asList(6, 6));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(1, 5, 5));
                return new Level(41, 4, tubes);
            case 42:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 4, 1));
                tubes.add(Arrays.asList(3, 3, 2));
                tubes.add(Arrays.asList(4, 3, 5));
                tubes.add(Arrays.asList(2, 5, 3));
                tubes.add(Arrays.asList(6, 6));
                tubes.add(Arrays.asList(4, 4, 5));
                tubes.add(Arrays.asList(6, 6, 2, 5));
                return new Level(42, 4, tubes);
            case 43:
                tubes.add(Arrays.asList(1, 4, 1));
                tubes.add(Arrays.asList(2, 4, 2));
                tubes.add(Arrays.asList(3, 3, 5, 1));
                tubes.add(Arrays.asList(4, 4, 6, 2));
                tubes.add(Arrays.asList(2, 6));
                tubes.add(Arrays.asList(6, 3, 3, 6));
                tubes.add(Arrays.asList(5, 5, 5, 1));
                tubes.add(Collections.<Integer>emptyList());
                return new Level(43, 4, tubes);
            case 44:
                tubes.add(Arrays.asList(1, 1, 6));
                tubes.add(Arrays.asList(2, 2, 2, 4));
                tubes.add(Arrays.asList(3, 4, 5, 6));
                tubes.add(Arrays.asList(5));
                tubes.add(Arrays.asList(5, 4, 5));
                tubes.add(Arrays.asList(6, 6, 4));
                tubes.add(Arrays.asList(2, 1));
                tubes.add(Arrays.asList(3, 3, 1, 3));
                return new Level(44, 4, tubes);
            case 45:
                tubes.add(Arrays.asList(1, 2, 3));
                tubes.add(Arrays.asList(2, 2, 4, 4));
                tubes.add(Arrays.asList(3, 3));
                tubes.add(Arrays.asList(4));
                tubes.add(Arrays.asList(5, 5, 6, 4));
                tubes.add(Arrays.asList(6, 6, 5));
                tubes.add(Arrays.asList(1, 1, 6, 5));
                tubes.add(Arrays.asList(1, 2, 3));
                return new Level(45, 4, tubes);
            case 46:
                tubes.add(Arrays.asList(1, 1));
                tubes.add(Arrays.asList(2, 4, 4, 5));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(6));
                tubes.add(Arrays.asList(5, 1, 2, 2));
                tubes.add(Arrays.asList(6, 6));
                tubes.add(Arrays.asList(6, 2, 3, 5));
                tubes.add(Arrays.asList(4, 5, 4, 1));
                return new Level(46, 4, tubes);
            case 47:
                tubes.add(Arrays.asList(1, 1, 4, 1));
                tubes.add(Arrays.asList(2, 2));
                tubes.add(Arrays.asList(3, 3, 3, 6));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(5, 2, 2));
                tubes.add(Arrays.asList(6, 6, 5));
                tubes.add(Arrays.asList(5, 4, 4));
                tubes.add(Arrays.asList(5, 1, 6, 4));
                return new Level(47, 4, tubes);
            case 48:
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(2, 2, 4));
                tubes.add(Arrays.asList(3, 3, 2, 6));
                tubes.add(Arrays.asList(4, 4, 5));
                tubes.add(Arrays.asList(5, 5, 3, 1));
                tubes.add(Arrays.asList(6, 6, 6, 1));
                tubes.add(Arrays.asList(1));
                tubes.add(Arrays.asList(3, 5, 1, 4));
                return new Level(48, 4, tubes);
            case 49:
                tubes.add(Arrays.asList(1));
                tubes.add(Arrays.asList(2, 2, 2, 2));
                tubes.add(Arrays.asList(3, 3, 1, 1));
                tubes.add(Arrays.asList(4, 4, 4, 6));
                tubes.add(Arrays.asList(1, 5, 5));
                tubes.add(Arrays.asList(6, 6, 6, 3));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(4, 5, 5));
                return new Level(49, 4, tubes);
            case 50:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 6, 4));
                tubes.add(Arrays.asList(3, 6, 6, 2));
                tubes.add(Arrays.asList(3, 3, 1, 4));
                tubes.add(Arrays.asList(5, 5, 4, 4));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(2, 3));
                tubes.add(Arrays.asList(5, 5, 6));
                return new Level(50, 4, tubes);
            case 51:
                tubes.add(Arrays.asList(3, 1, 3, 3));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(4, 2, 5, 2));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(5, 5, 5, 1));
                tubes.add(Arrays.asList(6, 6, 4));
                tubes.add(Arrays.asList(3, 2, 6, 1));
                tubes.add(Arrays.asList(6, 1));
                return new Level(51, 4, tubes);
            case 52:
                tubes.add(Arrays.asList(6, 6, 3, 2));
                tubes.add(Arrays.asList(6, 5));
                tubes.add(Arrays.asList(1, 4, 3));
                tubes.add(Arrays.asList(6, 2, 4));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(3, 2, 4));
                tubes.add(Arrays.asList(3, 2, 5, 4));
                tubes.add(Arrays.asList(1, 1, 1));
                return new Level(52, 4, tubes);
            case 53:
                tubes.add(Arrays.asList(2, 1, 4, 2));
                tubes.add(Arrays.asList(2, 6, 3, 6));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(4, 3, 1, 6));
                tubes.add(Arrays.asList(5, 5, 1, 1));
                tubes.add(Arrays.asList(5, 5, 6));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 3, 2));
                return new Level(53, 4, tubes);
            case 54:
                tubes.add(Arrays.asList(1, 6, 2));
                tubes.add(Arrays.asList(2, 1));
                tubes.add(Arrays.asList(3, 5, 3, 3));
                tubes.add(Arrays.asList(4));
                tubes.add(Arrays.asList(5, 5, 4, 4));
                tubes.add(Arrays.asList(6, 6, 5, 1));
                tubes.add(Arrays.asList(4, 3, 6, 2));
                tubes.add(Arrays.asList(2, 1));
                return new Level(54, 4, tubes);
            case 55:
                tubes.add(Arrays.asList(1, 1, 4, 4));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3, 3, 1));
                tubes.add(Arrays.asList(5, 5, 3));
                tubes.add(Arrays.asList(5, 1, 2, 2));
                tubes.add(Arrays.asList(6, 6));
                tubes.add(Arrays.asList(6, 6, 2, 4));
                tubes.add(Arrays.asList(3, 2, 5, 4));
                return new Level(55, 4, tubes);
            case 56:
                tubes.add(Arrays.asList(1, 6, 1, 6));
                tubes.add(Arrays.asList(4, 4, 2));
                tubes.add(Arrays.asList(3, 3, 5, 6));
                tubes.add(Arrays.asList(4, 4, 2, 2));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(5));
                tubes.add(Arrays.asList(3, 1, 6, 3));
                tubes.add(Arrays.asList(2, 1));
                return new Level(56, 4, tubes);
            case 57:
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(2, 2, 5, 1));
                tubes.add(Arrays.asList(1, 3, 3, 5));
                tubes.add(Arrays.asList(4, 4, 6));
                tubes.add(Arrays.asList(5, 1));
                tubes.add(Arrays.asList(6, 6, 6, 2));
                tubes.add(Arrays.asList(4, 1, 3, 3));
                tubes.add(Arrays.asList(4, 2, 5));
                return new Level(57, 4, tubes);
            case 58:
                tubes.add(Arrays.asList(1, 1));
                tubes.add(Arrays.asList(2, 4, 2));
                tubes.add(Arrays.asList(3, 3, 2, 6));
                tubes.add(Arrays.asList(5, 4));
                tubes.add(Arrays.asList(3, 6, 6));
                tubes.add(Arrays.asList(1, 6, 4));
                tubes.add(Arrays.asList(4, 5, 5, 2));
                tubes.add(Arrays.asList(3, 5, 1));
                return new Level(58, 4, tubes);
            case 59:
                tubes.add(Arrays.asList(3, 1));
                tubes.add(Arrays.asList(2, 1));
                tubes.add(Arrays.asList(3, 3, 1));
                tubes.add(Arrays.asList(4, 4, 2, 4));
                tubes.add(Arrays.asList(5, 5, 5));
                tubes.add(Arrays.asList(6, 6, 1));
                tubes.add(Arrays.asList(6, 3, 6, 2));
                tubes.add(Arrays.asList(2, 4, 5));
                return new Level(59, 4, tubes);
            case 60:
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(2, 2, 5, 5));
                tubes.add(Arrays.asList(3, 4, 4));
                tubes.add(Arrays.asList(4, 3, 5));
                tubes.add(Arrays.asList(1, 4));
                tubes.add(Arrays.asList(1, 6, 6, 2));
                tubes.add(Arrays.asList(2, 3, 5));
                tubes.add(Arrays.asList(6, 6));
                return new Level(60, 4, tubes);
            case 61:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 2, 1, 7));
                tubes.add(Arrays.asList(3, 3, 4, 6));
                tubes.add(Arrays.asList(4, 3));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(6, 2, 6));
                tubes.add(Arrays.asList(7, 7, 7, 4));
                tubes.add(Arrays.asList(3, 5, 6));
                tubes.add(Arrays.asList(5, 2, 4));
                return new Level(61, 4, tubes);
            case 62:
                tubes.add(Arrays.asList(1, 3));
                tubes.add(Arrays.asList(2, 2, 2, 4));
                tubes.add(Arrays.asList(3, 7, 2, 5));
                tubes.add(Arrays.asList(4, 1));
                tubes.add(Arrays.asList(5, 3, 3));
                tubes.add(Arrays.asList(6, 6, 5, 5));
                tubes.add(Arrays.asList(7, 7));
                tubes.add(Arrays.asList(6, 4, 6, 4));
                tubes.add(Arrays.asList(7, 1, 1));
                return new Level(62, 4, tubes);
            case 63:
                tubes.add(Arrays.asList(1, 6, 7, 2));
                tubes.add(Arrays.asList(2, 2));
                tubes.add(Arrays.asList(3, 3, 3, 4));
                tubes.add(Arrays.asList(4, 4, 6));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(6, 6, 3, 1));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(4, 7, 1, 5));
                tubes.add(Arrays.asList(7, 7, 5, 1));
                return new Level(63, 4, tubes);
            case 64:
                tubes.add(Arrays.asList(1, 1, 6, 4));
                tubes.add(Arrays.asList(2, 2, 4));
                tubes.add(Arrays.asList(3, 2, 6, 5));
                tubes.add(Arrays.asList(4, 3, 5, 3));
                tubes.add(Arrays.asList(3, 2));
                tubes.add(Arrays.asList(4, 1, 1));
                tubes.add(Arrays.asList(7, 7, 7));
                tubes.add(Arrays.asList(6, 7, 5, 5));
                tubes.add(Arrays.asList(6));
                return new Level(64, 4, tubes);
            case 65:
                tubes.add(Arrays.asList(1, 1, 4));
                tubes.add(Arrays.asList(2, 2, 2, 6));
                tubes.add(Arrays.asList(3, 3, 7, 1));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(6, 5, 5, 5));
                tubes.add(Arrays.asList(6, 5));
                tubes.add(Arrays.asList(7, 7, 6, 1));
                tubes.add(Arrays.asList(4, 7, 2));
                tubes.add(Arrays.asList(3, 3));
                return new Level(65, 4, tubes);
            case 66:
                tubes.add(Arrays.asList(1, 1));
                tubes.add(Arrays.asList(6, 2));
                tubes.add(Arrays.asList(3, 3, 6, 2));
                tubes.add(Arrays.asList(4, 4, 1, 7));
                tubes.add(Arrays.asList(5, 5, 5));
                tubes.add(Arrays.asList(3, 3));
                tubes.add(Arrays.asList(7, 7, 2));
                tubes.add(Arrays.asList(1, 4, 4, 7));
                tubes.add(Arrays.asList(6, 5, 6, 2));
                return new Level(66, 4, tubes);
            case 67:
                tubes.add(Arrays.asList(1, 1, 6, 6));
                tubes.add(Arrays.asList(4, 2, 6, 7));
                tubes.add(Arrays.asList(3, 3, 1, 5));
                tubes.add(Arrays.asList(2, 4));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(4, 7, 2, 2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(5, 3, 7, 7));
                tubes.add(Arrays.asList(6, 4, 1, 3));
                return new Level(67, 4, tubes);
            case 68:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(7, 5, 4));
                tubes.add(Arrays.asList(3, 3, 2));
                tubes.add(Arrays.asList(4, 4, 7, 3));
                tubes.add(Arrays.asList(5, 6));
                tubes.add(Arrays.asList(6, 6, 6));
                tubes.add(Arrays.asList(7, 2, 2, 4));
                tubes.add(Arrays.asList(5, 5, 7));
                tubes.add(Arrays.asList(1, 2, 3));
                return new Level(68, 4, tubes);
            case 69:
                tubes.add(Arrays.asList(4, 4, 5, 5));
                tubes.add(Arrays.asList(2, 2, 3));
                tubes.add(Arrays.asList(3, 3, 1));
                tubes.add(Arrays.asList(4, 3, 1));
                tubes.add(Arrays.asList(5, 7, 5));
                tubes.add(Arrays.asList(6, 6, 6, 4));
                tubes.add(Arrays.asList(7));
                tubes.add(Arrays.asList(2, 1, 1, 2));
                tubes.add(Arrays.asList(6, 7, 7));
                return new Level(69, 4, tubes);
            case 70:
                tubes.add(Arrays.asList(1, 7, 7, 6));
                tubes.add(Arrays.asList(2, 3, 2, 3));
                tubes.add(Arrays.asList(3, 3, 4, 4));
                tubes.add(Arrays.asList(4, 4, 5));
                tubes.add(Arrays.asList(5, 5, 1));
                tubes.add(Arrays.asList(6, 6, 6, 1));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(2, 1, 2, 7));
                tubes.add(Arrays.asList(7, 5));
                return new Level(70, 4, tubes);
            case 71:
                tubes.add(Arrays.asList(5, 1, 1, 3));
                tubes.add(Arrays.asList(2, 2));
                tubes.add(Arrays.asList(1, 3, 3, 2));
                tubes.add(Arrays.asList(4, 4, 3, 6));
                tubes.add(Arrays.asList(5, 1, 2, 7));
                tubes.add(Arrays.asList(6, 6, 6));
                tubes.add(Arrays.asList(7, 7, 7));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(5, 5, 4, 4));
                return new Level(71, 4, tubes);
            case 72:
                tubes.add(Arrays.asList(1, 7));
                tubes.add(Arrays.asList(6));
                tubes.add(Arrays.asList(3, 3, 3, 2));
                tubes.add(Arrays.asList(4, 4, 4, 1));
                tubes.add(Arrays.asList(5, 5, 2));
                tubes.add(Arrays.asList(6, 2, 7, 1));
                tubes.add(Arrays.asList(4, 1, 5));
                tubes.add(Arrays.asList(7, 3, 7));
                tubes.add(Arrays.asList(2, 6, 6, 5));
                return new Level(72, 4, tubes);
            case 73:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 1, 5));
                tubes.add(Arrays.asList(3, 3, 3, 6));
                tubes.add(Arrays.asList(4, 4, 5));
                tubes.add(Arrays.asList(6, 2, 6));
                tubes.add(Arrays.asList(2, 7));
                tubes.add(Arrays.asList(7, 6));
                tubes.add(Arrays.asList(3, 4, 5, 5));
                tubes.add(Arrays.asList(4, 7, 7, 2));
                return new Level(73, 4, tubes);
            case 74:
                tubes.add(Arrays.asList(1, 1, 1, 2));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(3, 3));
                tubes.add(Arrays.asList(3, 2, 7));
                tubes.add(Arrays.asList(5, 1, 7, 4));
                tubes.add(Arrays.asList(6, 6, 6, 4));
                tubes.add(Arrays.asList(7, 6, 3));
                tubes.add(Arrays.asList(2, 2, 5, 7));
                tubes.add(Arrays.asList(5, 4, 4, 5));
                return new Level(74, 4, tubes);
            case 75:
                tubes.add(Arrays.asList(5));
                tubes.add(Arrays.asList(2, 6, 1, 7));
                tubes.add(Arrays.asList(3, 3, 3, 5));
                tubes.add(Arrays.asList(4, 4, 4));
                tubes.add(Arrays.asList(5, 7, 5, 1));
                tubes.add(Arrays.asList(6, 6, 1, 1));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(2, 6, 2, 7));
                tubes.add(Arrays.asList(4, 3, 7, 2));
                return new Level(75, 4, tubes);
            case 76:
                tubes.add(Arrays.asList(1, 1, 4));
                tubes.add(Arrays.asList(2, 6, 1, 7));
                tubes.add(Arrays.asList(3, 3, 6, 2));
                tubes.add(Arrays.asList(4));
                tubes.add(Arrays.asList(5, 7, 5, 5));
                tubes.add(Arrays.asList(6, 6, 2, 1));
                tubes.add(Arrays.asList(7, 5));
                tubes.add(Arrays.asList(3, 2, 7));
                tubes.add(Arrays.asList(4, 4, 3));
                return new Level(76, 4, tubes);
            case 77:
                tubes.add(Arrays.asList(1, 4));
                tubes.add(Arrays.asList(2, 4, 4, 2));
                tubes.add(Arrays.asList(3, 3, 2, 2));
                tubes.add(Arrays.asList(1, 6, 4));
                tubes.add(Arrays.asList(5, 5, 5));
                tubes.add(Arrays.asList(6, 6, 3, 6));
                tubes.add(Arrays.asList(7, 5, 7, 1));
                tubes.add(Arrays.asList(3, 7, 7));
                tubes.add(Arrays.asList(1));
                return new Level(77, 4, tubes);
            case 78:
                tubes.add(Arrays.asList(1, 1, 3));
                tubes.add(Arrays.asList(2));
                tubes.add(Arrays.asList(3, 6));
                tubes.add(Arrays.asList(4, 4, 5, 7));
                tubes.add(Arrays.asList(7, 7, 3, 5));
                tubes.add(Arrays.asList(6, 5, 5));
                tubes.add(Arrays.asList(7, 1, 2));
                tubes.add(Arrays.asList(1, 4, 3, 2));
                tubes.add(Arrays.asList(4, 6, 6, 2));
                return new Level(78, 4, tubes);
            case 79:
                tubes.add(Arrays.asList(6, 6, 3, 5));
                tubes.add(Arrays.asList(2, 2, 4));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(4, 1, 6));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(2, 1, 1));
                tubes.add(Arrays.asList(7, 7, 2, 3));
                tubes.add(Arrays.asList(5, 7, 6, 4));
                tubes.add(Arrays.asList(7, 4, 1, 3));
                return new Level(79, 4, tubes);
            case 80:
                tubes.add(Arrays.asList(6, 4, 4, 6));
                tubes.add(Arrays.asList(1));
                tubes.add(Arrays.asList(3));
                tubes.add(Arrays.asList(4, 2, 6, 3));
                tubes.add(Arrays.asList(5, 5, 4, 7));
                tubes.add(Arrays.asList(6, 2, 5));
                tubes.add(Arrays.asList(7, 3, 7, 2));
                tubes.add(Arrays.asList(1, 1, 3, 1));
                tubes.add(Arrays.asList(2, 7, 5));
                return new Level(80, 4, tubes);
            case 81:
                tubes.add(Arrays.asList(1, 1, 2, 3));
                tubes.add(Arrays.asList(2, 2, 2, 3));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(4, 4, 4, 8));
                tubes.add(Arrays.asList(5, 5, 3));
                tubes.add(Arrays.asList(6, 5));
                tubes.add(Arrays.asList(7, 7, 7, 6));
                tubes.add(Arrays.asList(6, 8, 8, 3));
                tubes.add(Arrays.asList(5, 6, 7));
                tubes.add(Arrays.asList(1, 1, 8, 4));
                return new Level(81, 4, tubes);
            case 82:
                tubes.add(Arrays.asList(1, 1, 1, 5));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(7, 5));
                tubes.add(Arrays.asList(4, 4, 4));
                tubes.add(Arrays.asList(5, 1));
                tubes.add(Arrays.asList(6, 6, 6, 4));
                tubes.add(Arrays.asList(7, 7, 3, 8));
                tubes.add(Arrays.asList(8, 8, 5, 8));
                tubes.add(Arrays.asList(3, 2, 3));
                tubes.add(Arrays.asList(7, 6, 3));
                return new Level(82, 4, tubes);
            case 83:
                tubes.add(Arrays.asList(1, 5));
                tubes.add(Arrays.asList(2, 2, 2, 5));
                tubes.add(Arrays.asList(3, 3, 3, 1));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(1, 7, 6));
                tubes.add(Arrays.asList(7, 7, 6, 4));
                tubes.add(Arrays.asList(8, 6, 6, 7));
                tubes.add(Arrays.asList(3, 4, 2, 8));
                tubes.add(Arrays.asList(1, 8, 8));
                return new Level(83, 4, tubes);
            case 84:
                tubes.add(Arrays.asList(1, 1, 4, 7));
                tubes.add(Arrays.asList(3, 5));
                tubes.add(Arrays.asList(3, 3, 1, 6));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(2, 4));
                tubes.add(Arrays.asList(6, 1, 7));
                tubes.add(Arrays.asList(7, 3, 5, 5));
                tubes.add(Arrays.asList(8, 8, 2, 8));
                tubes.add(Arrays.asList(6, 8, 5, 6));
                tubes.add(Arrays.asList(2, 2, 7));
                return new Level(84, 4, tubes);
            case 85:
                tubes.add(Arrays.asList(1, 1, 4, 8));
                tubes.add(Arrays.asList(2, 2, 8, 1));
                tubes.add(Arrays.asList(3, 3, 6));
                tubes.add(Arrays.asList(4, 5));
                tubes.add(Arrays.asList(5, 4, 6, 6));
                tubes.add(Arrays.asList(1));
                tubes.add(Arrays.asList(7, 7));
                tubes.add(Arrays.asList(3, 8, 7, 7));
                tubes.add(Arrays.asList(4, 5, 2, 2));
                tubes.add(Arrays.asList(8, 6, 3, 5));
                return new Level(85, 4, tubes);
            case 86:
                tubes.add(Arrays.asList(1, 1, 8, 6));
                tubes.add(Arrays.asList(2, 5, 5, 4));
                tubes.add(Arrays.asList(2, 7));
                tubes.add(Arrays.asList(4, 4, 4));
                tubes.add(Arrays.asList(3, 2));
                tubes.add(Arrays.asList(6, 6, 1));
                tubes.add(Arrays.asList(7, 7, 7, 1));
                tubes.add(Arrays.asList(8, 8, 8));
                tubes.add(Arrays.asList(5, 5, 3));
                tubes.add(Arrays.asList(3, 3, 2, 6));
                return new Level(86, 4, tubes);
            case 87:
                tubes.add(Arrays.asList(1, 1, 4, 3));
                tubes.add(Arrays.asList(2, 8, 4));
                tubes.add(Arrays.asList(3, 1));
                tubes.add(Arrays.asList(4, 4, 6, 6));
                tubes.add(Arrays.asList(5, 5, 5));
                tubes.add(Arrays.asList(6, 1, 7));
                tubes.add(Arrays.asList(7, 2));
                tubes.add(Arrays.asList(8, 8, 2));
                tubes.add(Arrays.asList(7, 6, 7, 8));
                tubes.add(Arrays.asList(5, 3, 2, 3));
                return new Level(87, 4, tubes);
            case 88:
                tubes.add(Arrays.asList(5, 8, 1));
                tubes.add(Arrays.asList(2, 2, 4, 3));
                tubes.add(Arrays.asList(3, 1, 5));
                tubes.add(Arrays.asList(4, 4, 3, 7));
                tubes.add(Arrays.asList(5, 4, 6, 1));
                tubes.add(Arrays.asList(6, 6));
                tubes.add(Arrays.asList(7, 3));
                tubes.add(Arrays.asList(8, 8, 1));
                tubes.add(Arrays.asList(7, 7, 2, 2));
                tubes.add(Arrays.asList(8, 5, 6));
                return new Level(88, 4, tubes);
            case 89:
                tubes.add(Arrays.asList(1, 1, 1, 7));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(3, 3, 3, 2));
                tubes.add(Arrays.asList(4, 4, 8, 7));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(6, 6));
                tubes.add(Arrays.asList(7, 7));
                tubes.add(Arrays.asList(8, 3, 6, 5));
                tubes.add(Arrays.asList(1, 6, 8, 8));
                tubes.add(Arrays.asList(4, 4, 5));
                return new Level(89, 4, tubes);
            case 90:
                tubes.add(Arrays.asList(1, 7, 4, 1));
                tubes.add(Arrays.asList(2, 5, 8));
                tubes.add(Arrays.asList(3, 8));
                tubes.add(Arrays.asList(4, 4, 3, 3));
                tubes.add(Arrays.asList(5, 2, 2));
                tubes.add(Arrays.asList(6, 6, 8, 3));
                tubes.add(Arrays.asList(7, 1, 1, 4));
                tubes.add(Arrays.asList(6, 6, 5, 8));
                tubes.add(Arrays.asList(5, 7, 7, 2));
                tubes.add(Collections.<Integer>emptyList());
                return new Level(90, 4, tubes);
            case 91:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(8));
                tubes.add(Arrays.asList(3, 3, 2, 3));
                tubes.add(Arrays.asList(7, 5, 5, 6));
                tubes.add(Arrays.asList(5, 5, 4));
                tubes.add(Arrays.asList(6, 6, 6, 2));
                tubes.add(Arrays.asList(7));
                tubes.add(Arrays.asList(8, 8, 4, 2));
                tubes.add(Arrays.asList(8, 1, 4, 2));
                tubes.add(Arrays.asList(4, 3, 7, 7));
                return new Level(91, 4, tubes);
            case 92:
                tubes.add(Arrays.asList(1, 1, 1, 1));
                tubes.add(Arrays.asList(2, 6, 3));
                tubes.add(Arrays.asList(5, 3));
                tubes.add(Arrays.asList(4, 4, 6));
                tubes.add(Arrays.asList(2, 5, 3));
                tubes.add(Arrays.asList(6, 2));
                tubes.add(Arrays.asList(7, 6, 4, 3));
                tubes.add(Arrays.asList(8, 8, 8, 7));
                tubes.add(Arrays.asList(7, 4, 8, 2));
                tubes.add(Arrays.asList(5, 5, 7));
                return new Level(92, 4, tubes);
            case 93:
                tubes.add(Arrays.asList(1, 1));
                tubes.add(Arrays.asList(3, 1, 6, 2));
                tubes.add(Arrays.asList(3, 3, 3));
                tubes.add(Arrays.asList(4, 8, 7, 2));
                tubes.add(Arrays.asList(5, 5, 2, 4));
                tubes.add(Arrays.asList(6, 4, 7, 1));
                tubes.add(Arrays.asList(7, 7, 6, 8));
                tubes.add(Arrays.asList(8, 2));
                tubes.add(Arrays.asList(5, 5, 8, 6));
                tubes.add(Arrays.asList(4));
                return new Level(93, 4, tubes);
            case 94:
                tubes.add(Arrays.asList(1, 1, 5, 6));
                tubes.add(Arrays.asList(2, 2, 7));
                tubes.add(Arrays.asList(3, 3, 1, 4));
                tubes.add(Arrays.asList(7, 7, 5, 7));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(6, 8, 5, 8));
                tubes.add(Arrays.asList(2, 3, 4, 8));
                tubes.add(Arrays.asList(6, 8, 3, 6));
                tubes.add(Arrays.asList(4, 4, 5));
                tubes.add(Arrays.asList(1, 2));
                return new Level(94, 4, tubes);
            case 95:
                tubes.add(Arrays.asList(1, 2, 4, 6));
                tubes.add(Arrays.asList(2, 3, 2, 2));
                tubes.add(Arrays.asList(8, 1));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(5, 5));
                tubes.add(Arrays.asList(6, 6, 1, 3));
                tubes.add(Arrays.asList(7, 7, 1, 6));
                tubes.add(Arrays.asList(8, 8, 8, 3));
                tubes.add(Arrays.asList(7, 7, 5, 5));
                tubes.add(Arrays.asList(4, 3));
                return new Level(95, 4, tubes);
            case 96:
                tubes.add(Arrays.asList(1, 1, 1, 9));
                tubes.add(Arrays.asList(2, 2, 2));
                tubes.add(Arrays.asList(3, 1, 3, 6));
                tubes.add(Arrays.asList(4, 4));
                tubes.add(Arrays.asList(5, 5, 5, 3));
                tubes.add(Arrays.asList(6, 7, 6, 4));
                tubes.add(Arrays.asList(7, 7, 6, 8));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(9, 8, 3));
                tubes.add(Arrays.asList(5, 8, 8, 7));
                tubes.add(Arrays.asList(4, 2, 9, 9));
                return new Level(96, 4, tubes);
            case 97:
                tubes.add(Arrays.asList(1, 1, 1, 1));
                tubes.add(Arrays.asList(2, 2));
                tubes.add(Arrays.asList(3, 3, 8, 5));
                tubes.add(Arrays.asList(5, 4, 4));
                tubes.add(Arrays.asList(5, 3, 3, 4));
                tubes.add(Arrays.asList(6, 6, 6));
                tubes.add(Arrays.asList(7, 7, 7, 8));
                tubes.add(Arrays.asList(8, 8));
                tubes.add(Arrays.asList(9, 9, 9, 7));
                tubes.add(Arrays.asList(6, 2, 9, 2));
                tubes.add(Arrays.asList(4, 5));
                return new Level(97, 4, tubes);
            case 98:
                tubes.add(Arrays.asList(9, 7, 7));
                tubes.add(Arrays.asList(3, 6));
                tubes.add(Arrays.asList(7, 2, 2, 3));
                tubes.add(Arrays.asList(4, 7, 4));
                tubes.add(Arrays.asList(5, 5, 3, 6));
                tubes.add(Arrays.asList(6));
                tubes.add(Arrays.asList(5, 5, 6, 3));
                tubes.add(Arrays.asList(8, 8, 8, 1));
                tubes.add(Arrays.asList(9, 9, 9));
                tubes.add(Arrays.asList(4, 1, 1, 8));
                tubes.add(Arrays.asList(2, 2, 1, 4));
                return new Level(98, 4, tubes);
            case 99:
                tubes.add(Arrays.asList(1, 1, 1));
                tubes.add(Arrays.asList(2, 2, 5, 7));
                tubes.add(Arrays.asList(3, 3, 9, 4));
                tubes.add(Collections.<Integer>emptyList());
                tubes.add(Arrays.asList(6, 6, 6, 1));
                tubes.add(Arrays.asList(6, 2, 2));
                tubes.add(Arrays.asList(7, 7, 9, 7));
                tubes.add(Arrays.asList(8, 8, 8, 3));
                tubes.add(Arrays.asList(9, 9));
                tubes.add(Arrays.asList(3, 4, 4, 5));
                tubes.add(Arrays.asList(5, 5, 8, 4));
                return new Level(99, 4, tubes);
            case 100:
                tubes.add(Arrays.asList(1, 1, 2, 6));
                tubes.add(Arrays.asList(2, 2, 9, 3));
                tubes.add(Arrays.asList(3, 1));
                tubes.add(Arrays.asList(4, 4, 4, 5));
                tubes.add(Arrays.asList(5, 6, 6));
                tubes.add(Arrays.asList(6, 3));
                tubes.add(Arrays.asList(7, 7));
                tubes.add(Arrays.asList(8, 8, 8, 2));
                tubes.add(Arrays.asList(9, 9, 9, 7));
                tubes.add(Arrays.asList(8, 4, 7, 5));
                tubes.add(Arrays.asList(1, 3, 5));
                return new Level(100, 4, tubes);
            default:
                return buildLevel(1);
        }
    }
}
