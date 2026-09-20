package com.aquasort.puzzle.screens;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.dialogs.HintDialog;
import com.aquasort.puzzle.dialogs.NeedMoreUndosDialog;
import com.aquasort.puzzle.dialogs.PauseDialog;
import com.aquasort.puzzle.dialogs.RestartDialog;
import com.aquasort.puzzle.dialogs.RewardDialog;
import com.aquasort.puzzle.game.GameEngine;
import com.aquasort.puzzle.game.GameState;
import com.aquasort.puzzle.game.Move;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.LevelProgress;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.AdManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.services.VibrationManager;
import com.aquasort.puzzle.utils.Constants;
import com.aquasort.puzzle.views.PourOverlayView;
import com.aquasort.puzzle.views.TubeView;

import java.util.ArrayList;
import java.util.List;

public class GameFragment extends Fragment implements GameEngine.GameListener {

    private static final String ARG_LEVEL_ID = "arg_level_id";

    private int levelId;
    private GameEngine gameEngine;
    private StorageManager storage;
    private SoundManager sound;
    private VibrationManager vibration;
    private AdManager adManager;

    private TextView tvLevelTitle;
    private TextView tvMovesCount;
    private TextView tvCoinCount;
    private TextView tvUndoBadge;
    private LinearLayout tubesContainer;
    private PourOverlayView pourOverlay;
    private final List<TubeView> tubeViews = new ArrayList<>();

    public static GameFragment newInstance(int levelId) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LEVEL_ID, levelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            levelId = getArguments().getInt(ARG_LEVEL_ID, 1);
        }
        storage = StorageManager.getInstance(requireContext());
        sound = SoundManager.getInstance(requireContext());
        vibration = VibrationManager.getInstance(requireContext());
        adManager = AdManager.getInstance(requireContext());
        gameEngine = new GameEngine();
        gameEngine.setGameListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLevelTitle = view.findViewById(R.id.tv_game_level_title);
        tvMovesCount = view.findViewById(R.id.tv_game_moves_count);
        tvCoinCount = view.findViewById(R.id.tv_game_coin_count);
        tvUndoBadge = view.findViewById(R.id.tv_undo_count_badge);
        tubesContainer = view.findViewById(R.id.tubes_grid_layout);
        pourOverlay = view.findViewById(R.id.pour_overlay_view);

        // Pause
        view.findViewById(R.id.btn_game_pause).setOnClickListener(v -> {
            sound.playClick();
            showPauseDialog();
        });

        // Undo
        view.findViewById(R.id.btn_game_undo).setOnClickListener(v -> handleUndoClick());

        // Hint
        view.findViewById(R.id.btn_game_hint).setOnClickListener(v -> handleHintClick());

        // Restart
        view.findViewById(R.id.btn_game_restart).setOnClickListener(v -> {
            sound.playClick();
            showRestartDialog();
        });

        // Initialize Level
        initGameLevel();

        // Banner Ad
        FrameLayout adContainer = view.findViewById(R.id.banner_ad_container_game);
        if (adContainer != null && getActivity() != null) {
            adManager.createBannerAd(getActivity(), adContainer);
        }
    }

    private void initGameLevel() {
        PlayerData data = storage.loadPlayerData();
        GameState saved = storage.loadActiveGameState(data.getCoins());

        if (saved != null && saved.getCurrentLevel() == levelId && saved.getMoveCount() > 0) {
            gameEngine.resumeSavedState(saved);
        } else {
            gameEngine.startLevel(levelId, data.getCoins());
        }

        buildTubesUI();
        updateTopBar();
    }

    private void buildTubesUI() {
        tubesContainer.removeAllViews();
        tubeViews.clear();

        List<Tube> tubes = gameEngine.getGameState().getTubes();
        int count = tubes.size();

        // Determine rows and columns
        int rows = 1;
        int tubesPerRow = count;

        if (count > 5 && count <= 9) {
            rows = 2;
            tubesPerRow = (count + 1) / 2;
        } else if (count > 9) {
            rows = count <= 10 ? 2 : 3;
            tubesPerRow = (count + rows - 1) / rows;
        }

        // Responsive tube width and height
        float density = getResources().getDisplayMetrics().density;
        int tubeW = (int) (count <= 6 ? 56 * density : count <= 9 ? 48 * density : 40 * density);
        int tubeH = (int) (rows == 1 ? 190 * density : rows == 2 ? 160 * density : 135 * density);
        int marginH = (int) (count <= 6 ? 10 * density : 6 * density);
        int marginV = (int) (rows == 1 ? 0 : 16 * density);

        int tubeIndex = 0;
        for (int r = 0; r < rows; r++) {
            LinearLayout rowLayout = new LinearLayout(requireContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (r > 0) rowParams.topMargin = marginV;
            rowLayout.setLayoutParams(rowParams);

            int perThisRow = (r == rows - 1) ? (count - tubeIndex) : tubesPerRow;
            for (int c = 0; c < perThisRow; c++) {
                if (tubeIndex >= count) break;

                final int currentIdx = tubeIndex;
                Tube tubeData = tubes.get(currentIdx);

                TubeView tv = new TubeView(requireContext());
                LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(tubeW, tubeH);
                tvParams.leftMargin = marginH;
                tvParams.rightMargin = marginH;
                tv.setLayoutParams(tvParams);

                tv.setTubeData(tubeData);
                tv.setOnClickListener(v -> gameEngine.onTubeClicked(currentIdx));

                rowLayout.addView(tv);
                tubeViews.add(tv);
                tubeIndex++;
            }
            tubesContainer.addView(rowLayout);
        }
    }

    private void updateTopBar() {
        if (!isAdded() || gameEngine.getGameState() == null) return;
        GameState state = gameEngine.getGameState();
        tvLevelTitle.setText(getString(R.string.level_title_format, state.getCurrentLevel()));
        tvMovesCount.setText(getString(R.string.moves_format, state.getMoveCount()));
        tvCoinCount.setText(String.valueOf(storage.loadPlayerData().getCoins()));

        int undos = state.getFreeUndosRemaining();
        if (undos > 0) {
            tvUndoBadge.setText(String.valueOf(undos));
        } else {
            tvUndoBadge.setText("+");
        }
    }

    // ==================== ENGINE CALLBACKS ====================

    @Override
    public void onTubeSelected(int tubeIndex) {
        sound.playSelect();
        vibration.vibrateSelect();
        if (tubeIndex >= 0 && tubeIndex < tubeViews.size()) {
            tubeViews.get(tubeIndex).setSelectedState(true);
        }
    }

    @Override
    public void onTubeUnselected(int tubeIndex) {
        if (tubeIndex >= 0 && tubeIndex < tubeViews.size()) {
            tubeViews.get(tubeIndex).setSelectedState(false);
            tubeViews.get(tubeIndex).setHintActive(false);
        }
    }

    @Override
    public void onPourRequested(Move move, Runnable onAnimationComplete) {
        final TubeView srcView = tubeViews.get(move.getFromIndex());
        final TubeView dstView = tubeViews.get(move.getToIndex());

        sound.playPour();
        vibration.vibratePour();

        // Calculate positions
        int[] srcPos = new int[2];
        int[] dstPos = new int[2];
        srcView.getLocationOnScreen(srcPos);
        dstView.getLocationOnScreen(dstPos);

        float deltaX = dstPos[0] - srcPos[0];
        boolean isRight = deltaX >= 0;
        float tiltAngle = isRight ? 45f : -45f;
        float liftY = -srcView.getHeight() * 0.25f;

        // Animate source tube translation towards target
        float approachX = isRight ? (deltaX - srcView.getWidth() * 0.8f) : (deltaX + dstView.getWidth() * 0.8f);

        ObjectAnimator animX = ObjectAnimator.ofFloat(srcView, "translationX", 0f, approachX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(srcView, "translationY", srcView.getTranslationY(), liftY);
        ObjectAnimator animRot = ObjectAnimator.ofFloat(srcView, "rotation", 0f, tiltAngle);

        AnimatorSet pourSet = new AnimatorSet();
        pourSet.playTogether(animX, animY, animRot);
        pourSet.setDuration(280);
        pourSet.setInterpolator(new AccelerateDecelerateInterpolator());

        pourSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Draw pouring stream
                int[] overlayPos = new int[2];
                pourOverlay.getLocationOnScreen(overlayPos);

                int[] sPos = new int[2];
                int[] dPos = new int[2];
                srcView.getLocationOnScreen(sPos);
                dstView.getLocationOnScreen(dPos);

                float startX = (isRight ? sPos[0] + srcView.getWidth() * 0.8f : sPos[0] + srcView.getWidth() * 0.2f) - overlayPos[0];
                float startY = sPos[1] + srcView.getHeight() * 0.15f - overlayPos[1];
                float endX = dPos[0] + dstView.getWidth() * 0.5f - overlayPos[0];
                float endY = dPos[1] + dstView.getHeight() * 0.1f - overlayPos[1];

                pourOverlay.startStream(startX, startY, endX, endY, move.getColor());

                // Smooth layer change
                long flowDuration = 350;
                int srcLayerIdx = gameEngine.getGameState().getTube(move.getFromIndex()).size();
                int dstLayerIdx = gameEngine.getGameState().getTube(move.getToIndex()).size() - 1;

                srcView.animateDrainTop(srcLayerIdx, flowDuration, null);
                dstView.animateFillTarget(dstLayerIdx, flowDuration, () -> {
                    pourOverlay.stopStream();

                    // Return source tube to place
                    ObjectAnimator retX = ObjectAnimator.ofFloat(srcView, "translationX", 0f);
                    ObjectAnimator retY = ObjectAnimator.ofFloat(srcView, "translationY", 0f);
                    ObjectAnimator retRot = ObjectAnimator.ofFloat(srcView, "rotation", 0f);

                    AnimatorSet returnSet = new AnimatorSet();
                    returnSet.playTogether(retX, retY, retRot);
                    returnSet.setDuration(240);
                    returnSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            srcView.setTubeData(gameEngine.getGameState().getTube(move.getFromIndex()));
                            dstView.setTubeData(gameEngine.getGameState().getTube(move.getToIndex()));
                            srcView.setSelectedState(false);
                            dstView.setSelectedState(false);
                            onAnimationComplete.run();
                        }
                    });
                    returnSet.start();
                });
            }
        });
        pourSet.start();
    }

    @Override
    public void onInvalidMove(int sourceIndex, int destIndex) {
        sound.playInvalid();
        vibration.vibrateInvalid();
        if (sourceIndex >= 0 && sourceIndex < tubeViews.size()) {
            tubeViews.get(sourceIndex).playInvalidShake();
        }
    }

    @Override
    public void onMoveUndone(Move move) {
        sound.playUndo();
        vibration.vibrateSelect();
        tubeViews.get(move.getFromIndex()).setTubeData(gameEngine.getGameState().getTube(move.getFromIndex()));
        tubeViews.get(move.getToIndex()).setTubeData(gameEngine.getGameState().getTube(move.getToIndex()));
    }

    @Override
    public void onLevelWon(int stars, int coinsEarned) {
        sound.playWin();
        vibration.vibrateWin();

        // Clear active paused state
        storage.clearActiveGameState();

        // Save progress
        PlayerData data = storage.loadPlayerData();
        data.addCoins(coinsEarned);

        if (levelId >= data.getCurrentLevel()) {
            data.setCurrentLevel(levelId + 1);
        }
        storage.savePlayerData(data);

        LevelProgress prog = storage.getLevelProgress(levelId);
        prog.setCompleted(true);
        prog.setStars(stars);
        prog.setBestMoves(gameEngine.getGameState().getMoveCount());
        storage.saveLevelProgress(prog);

        // Show celebration reward popup
        RewardDialog rewardDialog = new RewardDialog(requireContext(), levelId, stars, coinsEarned, new RewardDialog.RewardDialogListener() {
            @Override
            public void onNextLevelClicked() {
                checkInterstitialAndProceed(levelId + 1);
            }

            @Override
            public void onReplayClicked() {
                gameEngine.startLevel(levelId, storage.loadPlayerData().getCoins());
                buildTubesUI();
                updateTopBar();
            }

            @Override
            public void onHomeClicked() {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).navigateTo(HomeFragment.newInstance(), false);
                }
            }

            @Override
            public void onDoubleRewardWithAdClicked() {
                if (getActivity() != null) {
                    adManager.showRewardedAd(getActivity(), "coins_double", coinsEarned, new AdManager.RewardCallback() {
                        @Override
                        public void onRewardEarned(int amount, String rewardType) {
                            PlayerData d = storage.loadPlayerData();
                            d.addCoins(amount);
                            storage.savePlayerData(d);
                            updateTopBar();
                            Toast.makeText(requireContext(), "Earned extra " + amount + " coins!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdFailed() {
                            Toast.makeText(requireContext(), "Ad not available right now.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        rewardDialog.show();
    }

    private void checkInterstitialAndProceed(int nextLevel) {
        if (getActivity() == null) return;
        adManager.showInterstitialIfEligible(getActivity(), levelId, () -> {
            if (!isAdded()) return;
            if (nextLevel > Constants.TOTAL_LEVELS) {
                ((MainActivity) requireActivity()).navigateTo(ComingSoonFragment.newInstance(), false);
            } else {
                levelId = nextLevel;
                gameEngine.startLevel(levelId, storage.loadPlayerData().getCoins());
                buildTubesUI();
                updateTopBar();
            }
        });
    }

    @Override
    public void onStateUpdated() {
        updateTopBar();
        if (gameEngine.getGameState() != null && !gameEngine.getGameState().isCompleted()) {
            storage.saveActiveGameState(gameEngine.getGameState());
        }
    }

    // ==================== USER ACTIONS ====================

    private void handleUndoClick() {
        sound.playClick();
        GameState state = gameEngine.getGameState();
        if (state == null || state.isCompleted()) return;

        if (state.getFreeUndosRemaining() > 0) {
            if (gameEngine.undo()) {
                state.decrementFreeUndos();
                updateTopBar();
            }
        } else {
            // Need More Undos Dialog
            NeedMoreUndosDialog dialog = new NeedMoreUndosDialog(requireContext(), new NeedMoreUndosDialog.UndosDialogListener() {
                @Override
                public void onUseCoinsForUndos() {
                    PlayerData data = storage.loadPlayerData();
                    if (data.spendCoins(50)) {
                        storage.savePlayerData(data);
                        state.addUndos(3);
                        updateTopBar();
                        gameEngine.undo();
                        Toast.makeText(requireContext(), "+3 Undos added!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onWatchAdForUndos() {
                    if (getActivity() != null) {
                        adManager.showRewardedAd(getActivity(), "undos", 3, new AdManager.RewardCallback() {
                            @Override
                            public void onRewardEarned(int amount, String rewardType) {
                                state.addUndos(amount);
                                updateTopBar();
                                gameEngine.undo();
                                Toast.makeText(requireContext(), "+3 Undos added!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdFailed() {
                                Toast.makeText(requireContext(), "Ad not ready.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            dialog.show();
        }
    }

    private void handleHintClick() {
        sound.playClick();
        PlayerData data = storage.loadPlayerData();

        if (data.getHintsRemaining() > 0) {
            data.setHintsRemaining(data.getHintsRemaining() - 1);
            storage.savePlayerData(data);
            showHintOnBoard();
        } else {
            HintDialog dialog = new HintDialog(requireContext(), new HintDialog.HintDialogListener() {
                @Override
                public void onUseCoinsForHint() {
                    PlayerData d = storage.loadPlayerData();
                    if (d.spendCoins(30)) {
                        storage.savePlayerData(d);
                        updateTopBar();
                        showHintOnBoard();
                    } else {
                        Toast.makeText(requireContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onWatchAdForHint() {
                    if (getActivity() != null) {
                        adManager.showRewardedAd(getActivity(), "hint", 1, new AdManager.RewardCallback() {
                            @Override
                            public void onRewardEarned(int amount, String rewardType) {
                                showHintOnBoard();
                            }

                            @Override
                            public void onAdFailed() {
                                Toast.makeText(requireContext(), "Ad not ready.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            dialog.show();
        }
    }

    private void showHintOnBoard() {
        Move hint = gameEngine.getHint();
        if (hint != null) {
            sound.playHint();
            int src = hint.getFromIndex();
            int dst = hint.getToIndex();
            if (src >= 0 && src < tubeViews.size()) {
                tubeViews.get(src).setHintActive(true);
            }
            if (dst >= 0 && dst < tubeViews.size()) {
                tubeViews.get(dst).setHintActive(true);
            }
        } else {
            Toast.makeText(requireContext(), R.string.no_moves_available, Toast.LENGTH_LONG).show();
        }
    }

    private void showPauseDialog() {
        PauseDialog dialog = new PauseDialog(requireContext(), levelId, new PauseDialog.PauseListener() {
            @Override
            public void onResumeClicked() {}

            @Override
            public void onRestartClicked() {
                gameEngine.restart();
                buildTubesUI();
                updateTopBar();
            }

            @Override
            public void onLevelsClicked() {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).navigateTo(LevelSelectFragment.newInstance(), false);
                }
            }

            @Override
            public void onHomeClicked() {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).navigateTo(HomeFragment.newInstance(), false);
                }
            }
        });
        dialog.show();
    }

    private void showRestartDialog() {
        RestartDialog dialog = new RestartDialog(requireContext(), new RestartDialog.RestartListener() {
            @Override
            public void onConfirmRestart() {
                gameEngine.restart();
                buildTubesUI();
                updateTopBar();
            }

            @Override
            public void onCancelRestart() {}
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gameEngine != null && gameEngine.getGameState() != null && !gameEngine.getGameState().isCompleted()) {
            storage.saveActiveGameState(gameEngine.getGameState());
        }
    }
}
