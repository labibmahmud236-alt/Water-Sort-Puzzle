package com.aquasort.puzzle.services;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.aquasort.puzzle.R;

/**
 * Manages SoundPool sound effects and MediaPlayer looping ambient music.
 * Fully respects user audio preferences and handles lifecycle safely.
 */
public class SoundManager {

    private static SoundManager instance;
    private final Context appContext;

    private SoundPool soundPool;
    private MediaPlayer mediaPlayer;

    private int sfxClick = 0;
    private int sfxSelect = 0;
    private int sfxPour = 0;
    private int sfxInvalid = 0;
    private int sfxUndo = 0;
    private int sfxHint = 0;
    private int sfxWin = 0;
    private int sfxCoin = 0;
    private int sfxReward = 0;

    private boolean soundEnabled = true;
    private boolean musicEnabled = true;

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context.getApplicationContext());
        }
        return instance;
    }

    private SoundManager(Context context) {
        this.appContext = context;
        initSoundPool();
        initMusic();
    }

    private void initSoundPool() {
        try {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(8)
                    .setAudioAttributes(audioAttributes)
                    .build();

            sfxClick = soundPool.load(appContext, R.raw.sfx_click, 1);
            sfxSelect = soundPool.load(appContext, R.raw.sfx_select, 1);
            sfxPour = soundPool.load(appContext, R.raw.sfx_pour, 1);
            sfxInvalid = soundPool.load(appContext, R.raw.sfx_invalid, 1);
            sfxUndo = soundPool.load(appContext, R.raw.sfx_undo, 1);
            sfxHint = soundPool.load(appContext, R.raw.sfx_hint, 1);
            sfxWin = soundPool.load(appContext, R.raw.sfx_win, 1);
            sfxCoin = soundPool.load(appContext, R.raw.sfx_coin, 1);
            sfxReward = soundPool.load(appContext, R.raw.sfx_reward, 1);
        } catch (Exception ignored) {
        }
    }

    private void initMusic() {
        try {
            mediaPlayer = MediaPlayer.create(appContext, R.raw.bgm_loop);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0.4f, 0.4f);
            }
        } catch (Exception ignored) {
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (enabled) {
            startMusic();
        } else {
            pauseMusic();
        }
    }

    public void startMusic() {
        if (!musicEnabled) return;
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception ignored) {
        }
    }

    public void pauseMusic() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception ignored) {
        }
    }

    public void resumeMusic() {
        if (musicEnabled) {
            startMusic();
        }
    }

    public void playClick() {
        playSfx(sfxClick, 0.7f);
    }

    public void playSelect() {
        playSfx(sfxSelect, 0.8f);
    }

    public void playPour() {
        playSfx(sfxPour, 0.85f);
    }

    public void playInvalid() {
        playSfx(sfxInvalid, 0.75f);
    }

    public void playUndo() {
        playSfx(sfxUndo, 0.8f);
    }

    public void playHint() {
        playSfx(sfxHint, 0.9f);
    }

    public void playWin() {
        playSfx(sfxWin, 1.0f);
    }

    public void playCoin() {
        playSfx(sfxCoin, 0.9f);
    }

    public void playReward() {
        playSfx(sfxReward, 0.95f);
    }

    private void playSfx(int soundId, float volume) {
        if (!soundEnabled || soundPool == null || soundId == 0) return;
        try {
            soundPool.play(soundId, volume, volume, 1, 0, 1.0f);
        } catch (Exception ignored) {
        }
    }

    public void release() {
        try {
            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception ignored) {
        }
    }
}
