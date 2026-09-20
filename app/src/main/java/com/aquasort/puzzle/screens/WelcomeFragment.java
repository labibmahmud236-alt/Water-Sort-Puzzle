package com.aquasort.puzzle.screens;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.dialogs.HowToPlayDialog;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.utils.Constants;
import com.aquasort.puzzle.views.TubeView;

import java.util.Arrays;

public class WelcomeFragment extends Fragment {

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TubeView tubeLeft = view.findViewById(R.id.welcome_tube_left);
        TubeView tubeCenter = view.findViewById(R.id.welcome_tube_center);
        TubeView tubeRight = view.findViewById(R.id.welcome_tube_right);

        if (tubeLeft != null) {
            tubeLeft.setTubeData(new Tube(4, Arrays.asList(
                    Constants.COLOR_RED,
                    Constants.COLOR_YELLOW,
                    Constants.COLOR_BLUE,
                    Constants.COLOR_CYAN
            )));
            startFloatingAnim(tubeLeft, -14f, 2200);
        }

        if (tubeCenter != null) {
            tubeCenter.setTubeData(new Tube(4, Arrays.asList(
                    Constants.COLOR_CYAN,
                    Constants.COLOR_PURPLE,
                    Constants.COLOR_PINK,
                    Constants.COLOR_GREEN
            )));
            tubeCenter.setSelectedState(true);
            startFloatingAnim(tubeCenter, -24f, 2600);
        }

        if (tubeRight != null) {
            tubeRight.setTubeData(new Tube(4, Arrays.asList(
                    Constants.COLOR_ORANGE,
                    Constants.COLOR_GREEN,
                    Constants.COLOR_RED,
                    Constants.COLOR_PURPLE
            )));
            startFloatingAnim(tubeRight, -14f, 2400);
        }

        view.findViewById(R.id.btn_welcome_start).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            PlayerData data = StorageManager.getInstance(requireContext()).loadPlayerData();
            data.setOnboardingCompleted(true);
            StorageManager.getInstance(requireContext()).savePlayerData(data);

            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.navigateTo(GameFragment.newInstance(1), true);
            }
        });

        view.findViewById(R.id.btn_welcome_how_to_play).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            HowToPlayDialog dialog = new HowToPlayDialog(requireContext(), () -> {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.navigateTo(HomeFragment.newInstance(), false);
                }
            });
            dialog.show();
        });
    }

    private void startFloatingAnim(View view, float deltaY, long duration) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), view.getTranslationY() + deltaY);
        anim.setDuration(duration);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();
    }
}
