package com.aquasort.puzzle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.utils.Constants;
import com.aquasort.puzzle.views.TubeView;

import java.util.Arrays;

public class HowToPlayDialog extends Dialog {

    public interface TutorialDismissListener {
        void onTutorialFinished();
    }

    private int currentStep = 1;
    private final TutorialDismissListener listener;

    private TubeView tube1;
    private TubeView tube2;
    private TextView tvTitle;
    private TextView tvDesc;
    private AppCompatButton btnNext;
    private View dot1, dot2, dot3, dot4;

    public HowToPlayDialog(@NonNull Context context, TutorialDismissListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_how_to_play);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        tube1 = findViewById(R.id.tutorial_tube_1);
        tube2 = findViewById(R.id.tutorial_tube_2);
        tvTitle = findViewById(R.id.tv_tutorial_step_title);
        tvDesc = findViewById(R.id.tv_tutorial_step_desc);
        btnNext = findViewById(R.id.btn_tutorial_next);

        dot1 = findViewById(R.id.dot_1);
        dot2 = findViewById(R.id.dot_2);
        dot3 = findViewById(R.id.dot_3);
        dot4 = findViewById(R.id.dot_4);

        findViewById(R.id.btn_tutorial_skip).setOnClickListener(v -> finishTutorial());

        btnNext.setOnClickListener(v -> {
            SoundManager.getInstance(getContext()).playClick();
            if (currentStep < 4) {
                currentStep++;
                updateStep();
            } else {
                finishTutorial();
            }
        });

        updateStep();
    }

    private void updateStep() {
        switch (currentStep) {
            case 1:
                tvTitle.setText(R.string.tutorial_step1_title);
                tvDesc.setText(R.string.tutorial_step1_desc);
                tube1.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_BLUE, Constants.COLOR_BLUE, Constants.COLOR_RED)));
                tube2.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_RED)));
                tube1.setSelectedState(true);
                tube2.setSelectedState(false);
                btnNext.setText(R.string.action_next);
                break;
            case 2:
                tvTitle.setText(R.string.tutorial_step2_title);
                tvDesc.setText(R.string.tutorial_step2_desc);
                tube1.setSelectedState(false);
                tube2.setSelectedState(true);
                btnNext.setText(R.string.action_next);
                break;
            case 3:
                tvTitle.setText(R.string.tutorial_step3_title);
                tvDesc.setText(R.string.tutorial_step3_desc);
                tube1.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_BLUE, Constants.COLOR_BLUE)));
                tube2.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_RED, Constants.COLOR_RED)));
                tube1.setSelectedState(false);
                tube2.setSelectedState(false);
                btnNext.setText(R.string.action_next);
                break;
            case 4:
                tvTitle.setText(R.string.tutorial_step4_title);
                tvDesc.setText(R.string.tutorial_step4_desc);
                tube1.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_BLUE, Constants.COLOR_BLUE, Constants.COLOR_BLUE, Constants.COLOR_BLUE)));
                tube2.setTubeData(new Tube(4, Arrays.asList(Constants.COLOR_RED, Constants.COLOR_RED, Constants.COLOR_RED, Constants.COLOR_RED)));
                tube1.setHintActive(true);
                tube2.setHintActive(true);
                btnNext.setText(R.string.action_play);
                break;
        }

        updateDots();
    }

    private void updateDots() {
        View[] dots = {dot1, dot2, dot3, dot4};
        for (int i = 0; i < dots.length; i++) {
            if (i == currentStep - 1) {
                dots[i].setBackgroundResource(R.drawable.bg_btn_primary);
            } else {
                dots[i].setBackgroundResource(R.drawable.bg_btn_secondary);
            }
        }
    }

    private void finishTutorial() {
        PlayerData data = StorageManager.getInstance(getContext()).loadPlayerData();
        data.setOnboardingCompleted(true);
        StorageManager.getInstance(getContext()).savePlayerData(data);
        dismiss();
        if (listener != null) {
            listener.onTutorialFinished();
        }
    }
}
