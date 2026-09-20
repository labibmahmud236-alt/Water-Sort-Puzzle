package com.aquasort.puzzle.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.aquasort.puzzle.game.GameState;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.LevelProgress;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Robust persistence manager using SharedPreferences.
 * Safely saves and retrieves player data, level completions, settings, and paused puzzles.
 */
public class StorageManager {

    private static StorageManager instance;
    private final SharedPreferences prefs;

    public static synchronized StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new StorageManager(context.getApplicationContext());
        }
        return instance;
    }

    private StorageManager(Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public PlayerData loadPlayerData() {
        PlayerData data = new PlayerData();
        data.setCurrentLevel(prefs.getInt(Constants.KEY_CURRENT_LEVEL, 1));
        data.setCoins(prefs.getInt(Constants.KEY_COINS, 100));
        data.setSoundEnabled(prefs.getBoolean(Constants.KEY_SOUND_ENABLED, true));
        data.setMusicEnabled(prefs.getBoolean(Constants.KEY_MUSIC_ENABLED, true));
        data.setVibrationEnabled(prefs.getBoolean(Constants.KEY_VIBRATION_ENABLED, true));
        data.setNotificationsEnabled(prefs.getBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, true));
        data.setOnboardingCompleted(prefs.getBoolean(Constants.KEY_ONBOARDING_DONE, false));
        data.setDailyRewardDay(prefs.getInt(Constants.KEY_DAILY_REWARD_DAY, 1));
        data.setLastDailyRewardClaimTime(prefs.getLong(Constants.KEY_LAST_DAILY_CLAIM, 0));
        data.setHasRated(prefs.getBoolean(Constants.KEY_RATED_APP, false));
        return data;
    }

    public void savePlayerData(PlayerData data) {
        if (data == null) return;
        prefs.edit()
                .putInt(Constants.KEY_CURRENT_LEVEL, data.getCurrentLevel())
                .putInt(Constants.KEY_COINS, data.getCoins())
                .putBoolean(Constants.KEY_SOUND_ENABLED, data.isSoundEnabled())
                .putBoolean(Constants.KEY_MUSIC_ENABLED, data.isMusicEnabled())
                .putBoolean(Constants.KEY_VIBRATION_ENABLED, data.isVibrationEnabled())
                .putBoolean(Constants.KEY_NOTIFICATIONS_ENABLED, data.isNotificationsEnabled())
                .putBoolean(Constants.KEY_ONBOARDING_DONE, data.isOnboardingCompleted())
                .putInt(Constants.KEY_DAILY_REWARD_DAY, data.getDailyRewardDay())
                .putLong(Constants.KEY_LAST_DAILY_CLAIM, data.getLastDailyRewardClaimTime())
                .putBoolean(Constants.KEY_RATED_APP, data.hasRated())
                .apply();
    }

    public LevelProgress getLevelProgress(int levelId) {
        String key = Constants.KEY_LEVEL_PROGRESS_PREFIX + levelId;
        String raw = prefs.getString(key, null);
        if (raw == null || raw.isEmpty()) {
            return new LevelProgress(levelId, false, 0, 0);
        }
        try {
            String[] parts = raw.split(",");
            boolean completed = Boolean.parseBoolean(parts[0]);
            int stars = Integer.parseInt(parts[1]);
            int bestMoves = Integer.parseInt(parts[2]);
            return new LevelProgress(levelId, completed, stars, bestMoves);
        } catch (Exception e) {
            return new LevelProgress(levelId, false, 0, 0);
        }
    }

    public void saveLevelProgress(LevelProgress progress) {
        if (progress == null) return;
        String key = Constants.KEY_LEVEL_PROGRESS_PREFIX + progress.getLevelId();
        String raw = progress.isCompleted() + "," + progress.getStars() + "," + progress.getBestMoves();
        prefs.edit().putString(key, raw).apply();
    }

    public int getCompletedLevelCount() {
        int count = 0;
        for (int i = 1; i <= Constants.TOTAL_LEVELS; i++) {
            if (getLevelProgress(i).isCompleted()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Saves active mid-game state so player can seamlessly continue.
     */
    public void saveActiveGameState(GameState state) {
        if (state == null || state.isCompleted()) {
            clearActiveGameState();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(state.getCurrentLevel()).append(";");
        sb.append(state.getMoveCount()).append(";");
        sb.append(state.getFreeUndosRemaining()).append(";");

        List<Tube> tubes = state.getTubes();
        for (int i = 0; i < tubes.size(); i++) {
            Tube t = tubes.get(i);
            List<Integer> layers = t.getLayers();
            for (int j = 0; j < layers.size(); j++) {
                sb.append(layers.get(j));
                if (j < layers.size() - 1) sb.append(",");
            }
            if (i < tubes.size() - 1) sb.append("/");
        }

        prefs.edit()
                .putInt(Constants.KEY_SAVED_STATE_LEVEL, state.getCurrentLevel())
                .putString(Constants.KEY_SAVED_STATE_DATA, sb.toString())
                .apply();
    }

    public GameState loadActiveGameState(int playerCoins) {
        int level = prefs.getInt(Constants.KEY_SAVED_STATE_LEVEL, 0);
        String data = prefs.getString(Constants.KEY_SAVED_STATE_DATA, null);
        if (level <= 0 || data == null || data.isEmpty()) {
            return null;
        }
        try {
            String[] sections = data.split(";");
            int lvl = Integer.parseInt(sections[0]);
            int moves = Integer.parseInt(sections[1]);
            int undos = Integer.parseInt(sections[2]);
            String tubesStr = sections.length > 3 ? sections[3] : "";

            List<Tube> tubes = new ArrayList<>();
            if (!tubesStr.isEmpty()) {
                String[] tubeArray = tubesStr.split("/");
                for (String tStr : tubeArray) {
                    Tube tube = new Tube();
                    if (!tStr.isEmpty()) {
                        String[] layerArray = tStr.split(",");
                        for (String lStr : layerArray) {
                            if (!lStr.isEmpty()) {
                                tube.addLayer(Integer.parseInt(lStr));
                            }
                        }
                    }
                    tubes.add(tube);
                }
            }

            GameState state = new GameState(lvl, tubes, playerCoins);
            state.setMoveCount(moves);
            state.setFreeUndosRemaining(undos);
            return state;
        } catch (Exception e) {
            clearActiveGameState();
            return null;
        }
    }

    public void clearActiveGameState() {
        prefs.edit()
                .remove(Constants.KEY_SAVED_STATE_LEVEL)
                .remove(Constants.KEY_SAVED_STATE_DATA)
                .apply();
    }

    // ==================== CONTAINER SKINS ====================

    public java.util.Set<String> getUnlockedSkins() {
        java.util.Set<String> set = new java.util.HashSet<>();
        set.add(Constants.DEFAULT_SKIN_ID); // Classic tube is always unlocked
        String raw = prefs.getString(Constants.KEY_UNLOCKED_SKINS, "");
        if (raw != null && !raw.isEmpty()) {
            String[] parts = raw.split(",");
            for (String s : parts) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    set.add(trimmed);
                }
            }
        }
        return set;
    }

    public boolean isSkinUnlocked(String skinId) {
        if (Constants.DEFAULT_SKIN_ID.equals(skinId)) return true;
        return getUnlockedSkins().contains(skinId);
    }

    public void unlockSkin(String skinId) {
        if (skinId == null || skinId.isEmpty()) return;
        java.util.Set<String> set = getUnlockedSkins();
        set.add(skinId);
        StringBuilder sb = new StringBuilder();
        for (String id : set) {
            if (sb.length() > 0) sb.append(",");
            sb.append(id);
        }
        prefs.edit().putString(Constants.KEY_UNLOCKED_SKINS, sb.toString()).apply();
    }

    public String getEquippedSkinId() {
        String equipped = prefs.getString(Constants.KEY_EQUIPPED_SKIN, Constants.DEFAULT_SKIN_ID);
        if (equipped == null || equipped.isEmpty()) {
            return Constants.DEFAULT_SKIN_ID;
        }
        return equipped;
    }

    public void setEquippedSkinId(String skinId) {
        if (skinId == null || skinId.isEmpty()) {
            skinId = Constants.DEFAULT_SKIN_ID;
        }
        prefs.edit().putString(Constants.KEY_EQUIPPED_SKIN, skinId).apply();
    }
}
