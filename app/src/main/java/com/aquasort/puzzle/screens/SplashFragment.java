package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.AdManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.utils.Constants;
import com.aquasort.puzzle.views.TubeView;

import java.util.Arrays;

public class SplashFragment extends Fragment {

    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TubeView tubeView = view.findViewById(R.id.splash_tube_view);
        if (tubeView != null) {
            tubeView.setTubeData(new Tube(4, Arrays.asList(
                    Constants.COLOR_BLUE,
                    Constants.COLOR_CYAN,
                    Constants.COLOR_PURPLE,
                    Constants.COLOR_PINK
            )));
            tubeView.setSelectedState(true);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;

            PlayerData data = StorageManager.getInstance(requireContext()).loadPlayerData();
            MainActivity activity = (MainActivity) getActivity();
            if (activity == null) return;

            // App open ad trigger attempt
            AdManager.getInstance(requireContext()).showAppOpenAdIfAvailable(activity, () -> {
                if (!isAdded()) return;
                if (!data.isOnboardingCompleted()) {
                    activity.navigateTo(WelcomeFragment.newInstance(), false);
                } else {
                    activity.navigateTo(HomeFragment.newInstance(), false);
                }
            });
        }, 1800);
    }
}
