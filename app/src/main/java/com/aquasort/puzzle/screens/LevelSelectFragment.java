package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.adapters.LevelAdapter;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.AdManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;

public class LevelSelectFragment extends Fragment {

    public static LevelSelectFragment newInstance() {
        return new LevelSelectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_level_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StorageManager storage = StorageManager.getInstance(requireContext());
        PlayerData playerData = storage.loadPlayerData();

        view.findViewById(R.id.btn_levels_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            requireActivity().onBackPressed();
        });

        TextView tvCoins = view.findViewById(R.id.tv_levels_coin_count);
        tvCoins.setText(String.valueOf(playerData.getCoins()));

        TextView tvProgress = view.findViewById(R.id.tv_level_select_progress_count);
        ProgressBar pbLevels = view.findViewById(R.id.pb_levels_total);

        int completed = storage.getCompletedLevelCount();
        tvProgress.setText(getString(R.string.level_progress_format, completed));
        pbLevels.setProgress(completed);

        RecyclerView rv = view.findViewById(R.id.rv_levels_grid);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 4));

        int unlocked = Math.max(1, playerData.getCurrentLevel());
        LevelAdapter adapter = new LevelAdapter(requireContext(), unlocked, levelId -> {
            SoundManager.getInstance(requireContext()).playClick();
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.navigateTo(GameFragment.newInstance(levelId), true);
            }
        });
        rv.setAdapter(adapter);

        // Scroll to near current level
        int targetPosition = Math.max(0, unlocked - 1);
        rv.scrollToPosition(targetPosition);

        // Optional Banner Ad
        FrameLayout adContainer = view.findViewById(R.id.banner_ad_container_levels);
        if (adContainer != null && getActivity() != null) {
            AdManager.getInstance(requireContext()).createBannerAd(getActivity(), adContainer);
        }
    }
}
