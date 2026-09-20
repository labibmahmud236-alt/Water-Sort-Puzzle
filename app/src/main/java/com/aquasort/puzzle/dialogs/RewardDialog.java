package com.aquasort.puzzle.dialogs;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.views.ConfettiView;

public class RewardDialog extends Dialog {

    public interface RewardDialogListener {
        void onNextLevelClicked();
        void onReplayClicked();
        void onHomeClicked();
        void onDoubleRewardWithAdClicked();
    }

    private final int levelId;
    private final int stars;
    private final int baseCoins;
    private final RewardDialogListener listener;

    public RewardDialog(@NonNull Context context, int levelId, int stars, int baseCoins, RewardDialogListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.levelId = levelId;
        this.stars = stars;
        this.baseCoins = baseCoins;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reward);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setCancelable(false);

        TextView tvLevel = findViewById(R.id.tv_reward_level_number);
        if (tvLevel != null) {
            tvLevel.setText(getContext().getString(R.string.level_title_format, levelId));
        }

        TextView tvCoins = findViewById(R.id.tv_coins_earned);
        if (tvCoins != null) {
            tvCoins.setText(getContext().getString(R.string.level_complete_coins, baseCoins));
        }

        ConfettiView confetti = findViewById(R.id.confetti_view);
        if (confetti != null) {
            confetti.post(confetti::startExplosion);
        }

        animateStars();

        findViewById(R.id.btn_reward_double).setOnClickListener(v -> {
            if (listener != null) listener.onDoubleRewardWithAdClicked();
        });

        findViewById(R.id.btn_reward_next).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onNextLevelClicked();
        });

        findViewById(R.id.btn_reward_replay).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onReplayClicked();
        });

        findViewById(R.id.btn_reward_home).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onHomeClicked();
        });
    }

    public void updateCoinsText(int newCoins) {
        TextView tvCoins = findViewById(R.id.tv_coins_earned);
        if (tvCoins != null) {
            tvCoins.setText(getContext().getString(R.string.level_complete_coins, newCoins));
        }
        View btnDouble = findViewById(R.id.btn_reward_double);
        if (btnDouble != null) {
            btnDouble.setVisibility(View.GONE);
        }
    }

    private void animateStars() {
        ImageView s1 = findViewById(R.id.iv_star_1);
        ImageView s2 = findViewById(R.id.iv_star_2);
        ImageView s3 = findViewById(R.id.iv_star_3);

        ImageView[] starViews = {s1, s2, s3};
        Handler handler = new Handler(Looper.getMainLooper());

        for (int i = 0; i < stars; i++) {
            final int index = i;
            final ImageView iv = starViews[i];
            handler.postDelayed(() -> {
                iv.setImageResource(R.drawable.ic_star_filled);
                iv.setScaleX(0f);
                iv.setScaleY(0f);

                ObjectAnimator sx = ObjectAnimator.ofFloat(iv, "scaleX", 0f, 1.3f, 1.0f);
                ObjectAnimator sy = ObjectAnimator.ofFloat(iv, "scaleY", 0f, 1.3f, 1.0f);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(sx, sy);
                set.setDuration(400);
                set.setInterpolator(new OvershootInterpolator(1.8f));
                set.start();

                SoundManager.getInstance(getContext()).playCoin();
            }, 300L + (i * 280L));
        }
    }
}
