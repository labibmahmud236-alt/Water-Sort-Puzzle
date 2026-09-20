package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;

import java.util.Calendar;

public class DailyRewardFragment extends Fragment {

    private static final int[] REWARDS = {20, 30, 40, 50, 75, 100, 200};

    private StorageManager storage;
    private PlayerData playerData;
    private TextView tvCoinCount;
    private AppCompatButton btnClaim;
    private LinearLayout[] dayCards;

    public static DailyRewardFragment newInstance() {
        return new DailyRewardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_reward, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = StorageManager.getInstance(requireContext());
        playerData = storage.loadPlayerData();

        view.findViewById(R.id.btn_daily_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            requireActivity().onBackPressed();
        });

        tvCoinCount = view.findViewById(R.id.tv_daily_coin_count);
        btnClaim = view.findViewById(R.id.btn_daily_claim);

        dayCards = new LinearLayout[]{
                view.findViewById(R.id.card_day_1),
                view.findViewById(R.id.card_day_2),
                view.findViewById(R.id.card_day_3),
                view.findViewById(R.id.card_day_4),
                view.findViewById(R.id.card_day_5),
                view.findViewById(R.id.card_day_6),
                view.findViewById(R.id.card_day_7)
        };

        btnClaim.setOnClickListener(v -> claimReward());

        updateUI();
    }

    private void updateUI() {
        tvCoinCount.setText(String.valueOf(playerData.getCoins()));

        boolean canClaim = canClaimToday();
        int currentDay = playerData.getDailyRewardDay(); // 1 to 7

        for (int i = 0; i < 7; i++) {
            int dayNum = i + 1;
            LinearLayout card = dayCards[i];
            if (dayNum < currentDay) {
                // Past days: claimed
                card.setAlpha(0.5f);
                card.setBackgroundResource(R.drawable.bg_glass_card);
            } else if (dayNum == currentDay) {
                // Today
                card.setAlpha(1.0f);
                card.setBackgroundResource(R.drawable.bg_glass_card_accent);
            } else {
                // Future days: locked
                card.setAlpha(0.65f);
                card.setBackgroundResource(R.drawable.bg_glass_card);
            }
        }

        if (canClaim) {
            btnClaim.setEnabled(true);
            btnClaim.setText(getString(R.string.action_claim) + " (+" + REWARDS[currentDay - 1] + " COINS)");
            btnClaim.setAlpha(1.0f);
        } else {
            btnClaim.setEnabled(false);
            btnClaim.setText(R.string.action_claimed);
            btnClaim.setAlpha(0.5f);
        }
    }

    private boolean canClaimToday() {
        long lastClaim = playerData.getLastDailyRewardClaimTime();
        if (lastClaim == 0) return true;

        Calendar now = Calendar.getInstance();
        Calendar last = Calendar.getInstance();
        last.setTimeInMillis(lastClaim);

        return now.get(Calendar.YEAR) != last.get(Calendar.YEAR) ||
                now.get(Calendar.DAY_OF_YEAR) != last.get(Calendar.DAY_OF_YEAR);
    }

    private void claimReward() {
        if (!canClaimToday()) {
            Toast.makeText(requireContext(), R.string.already_claimed_today, Toast.LENGTH_SHORT).show();
            return;
        }

        SoundManager.getInstance(requireContext()).playReward();

        int currentDay = playerData.getDailyRewardDay();
        int rewardCoins = REWARDS[currentDay - 1];

        playerData.addCoins(rewardCoins);
        playerData.setLastDailyRewardClaimTime(System.currentTimeMillis());

        if (currentDay >= 7) {
            playerData.setDailyRewardDay(1); // Cycle back
        } else {
            playerData.setDailyRewardDay(currentDay + 1);
        }

        storage.savePlayerData(playerData);
        updateUI();

        Toast.makeText(requireContext(), "+" + rewardCoins + " Coins Claimed!", Toast.LENGTH_SHORT).show();
    }
}
