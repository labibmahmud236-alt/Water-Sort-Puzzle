package com.aquasort.puzzle.utils;

/**
 * Global game constants.
 */
public final class Constants {
    private Constants() {}

    // Color IDs
    public static final int COLOR_EMPTY = 0;
    public static final int COLOR_RED = 1;
    public static final int COLOR_ORANGE = 2;
    public static final int COLOR_YELLOW = 3;
    public static final int COLOR_GREEN = 4;
    public static final int COLOR_CYAN = 5;
    public static final int COLOR_BLUE = 6;
    public static final int COLOR_PURPLE = 7;
    public static final int COLOR_PINK = 8;
    public static final int COLOR_LIME = 9;
    public static final int COLOR_BROWN = 10;
    public static final int COLOR_GREY = 11;
    public static final int COLOR_INDIGO = 12;

    // Total Levels
    public static final int TOTAL_LEVELS = 100;
    public static final int TUBE_DEFAULT_CAPACITY = 4;

    // Preferences Keys
    public static final String PREFS_NAME = "aqua_sort_prefs";
    public static final String KEY_CURRENT_LEVEL = "current_level";
    public static final String KEY_COINS = "coins_balance";
    public static final String KEY_SOUND_ENABLED = "sound_enabled";
    public static final String KEY_MUSIC_ENABLED = "music_enabled";
    public static final String KEY_VIBRATION_ENABLED = "vibration_enabled";
    public static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String KEY_ONBOARDING_DONE = "onboarding_done";
    public static final String KEY_DAILY_REWARD_DAY = "daily_reward_day";
    public static final String KEY_LAST_DAILY_CLAIM = "last_daily_claim";
    public static final String KEY_RATED_APP = "rated_app";
    public static final String KEY_SAVED_STATE_LEVEL = "saved_state_level";
    public static final String KEY_SAVED_STATE_DATA = "saved_state_data";
    public static final String KEY_LEVEL_PROGRESS_PREFIX = "level_prog_";

    // AdMob Configurations
    public static final int INTERSTITIAL_LEVEL_INTERVAL = 2; // Show every 2 levels
    public static final int REWARD_COINS_AMOUNT = 100;
    public static final int REWARD_UNDOS_AMOUNT = 3;
    public static final int REWARD_HINTS_AMOUNT = 1;

    // Container Skin Keys
    public static final String KEY_UNLOCKED_SKINS = "unlocked_skins_v1";
    public static final String KEY_EQUIPPED_SKIN = "equipped_skin_v1";
    public static final String DEFAULT_SKIN_ID = "classic_tube";
}
