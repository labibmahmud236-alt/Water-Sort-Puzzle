package com.aquasort.puzzle.screens;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.dialogs.HowToPlayDialog;
import com.aquasort.puzzle.dialogs.RateUsDialog;
import com.aquasort.puzzle.game.GameState;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.ShareManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.utils.Constants;
import com.aquasort.puzzle.views.TubeView;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private TextView tvCoinCount;
    private TextView tvCurrentLevel;
    private TextView tvProgressDesc;
    private AppCompatButton btnPlay;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCoinCount = view.findViewById(R.id.tv_home_coin_count);
        tvCurrentLevel = view.findViewById(R.id.tv_home_current_level);
        tvProgressDesc = view.findViewById(R.id.tv_home_progress_desc);
        btnPlay = view.findViewById(R.id.btn_home_play);

        TubeView tube1 = view.findViewById(R.id.home_tube_1);
        TubeView tube2 = view.findViewById(R.id.home_tube_2);
        TubeView tube3 = view.findViewById(R.id.home_tube_3);

        if (tube1 != null) {
            tube1.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_CYAN, Constants.COLOR_BLUE, Constants.COLOR_PURPLE)));
            startBobbing(tube1, -12f, 2400);
        }
        if (tube2 != null) {
            tube2.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_ORANGE, Constants.COLOR_YELLOW, Constants.COLOR_GREEN)));
            tube2.setSelectedState(true);
            startBobbing(tube2, -18f, 2700);
        }
        if (tube3 != null) {
            tube3.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_PINK, Constants.COLOR_PURPLE, Constants.COLOR_LIME)));
            startBobbing(tube3, -12f, 2500);
        }

        view.findViewById(R.id.btn_home_settings).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            navigate(SettingsFragment.newInstance());
        });

        view.findViewById(R.id.btn_home_levels).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            navigate(LevelSelectFragment.newInstance());
        });

        view.findViewById(R.id.btn_home_daily_reward).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            navigate(DailyRewardFragment.newInstance());
        });

        view.findViewById(R.id.btn_home_shop).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            navigate(ShopFragment.newInstance());
        });

        view.findViewById(R.id.btn_home_how_to_play).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            new HowToPlayDialog(requireContext(), null).show();
        });

        view.findViewById(R.id.btn_home_share).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            ShareManager.shareApp(getActivity());
        });

        view.findViewById(R.id.btn_home_rate).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            new RateUsDialog(requireContext()).show();
        });

        btnPlay.setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            PlayerData data = StorageManager.getInstance(requireContext()).loadPlayerData();
            int targetLevel = data.getCurrentLevel();
            if (targetLevel > Constants.TOTAL_LEVELS) {
                navigate(ComingSoonFragment.newInstance());
            } else {
                navigate(GameFragment.newInstance(targetLevel));
            }
        });

        refreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
        SoundManager.getInstance(requireContext()).resumeMusic();
    }

    private void refreshData() {
        if (!isAdded()) return;
        StorageManager storage = StorageManager.getInstance(requireContext());
        PlayerData data = storage.loadPlayerData();

        tvCoinCount.setText(String.valueOf(data.getCoins()));
        int currentLevel = data.getCurrentLevel();
        if (currentLevel > Constants.TOTAL_LEVELS) {
            tvCurrentLevel.setText(R.string.coming_soon_headline);
            btnPlay.setText(R.string.action_level_select);
        } else {
            tvCurrentLevel.setText(getString(R.string.level_title_format, currentLevel));
            GameState savedState = storage.loadActiveGameState(data.getCoins());
            if (savedState != null && savedState.getCurrentLevel() == currentLevel && savedState.getMoveCount() > 0) {
                btnPlay.setText(R.string.action_continue);
            } else {
                btnPlay.setText(R.string.action_play);
            }
        }

        int completed = storage.getCompletedLevelCount();
        tvProgressDesc.setText(getString(R.string.level_progress_format, completed));
    }

    private void navigate(Fragment fragment) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.navigateTo(fragment, true);
        }
    }

    private void startBobbing(View view, float deltaY, long duration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), view.getTranslationY() + deltaY);
        anim.setDuration(duration);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }
}
