package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.AdManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;

public class ShopFragment extends Fragment {

    private StorageManager storage;
    private PlayerData playerData;
    private TextView tvCoinCount;
    private TextView tvUserUndos;
    private TextView tvUserHints;

    public static ShopFragment newInstance() {
        return new ShopFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = StorageManager.getInstance(requireContext());
        playerData = storage.loadPlayerData();

        view.findViewById(R.id.btn_shop_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            requireActivity().onBackPressed();
        });

        tvCoinCount = view.findViewById(R.id.tv_shop_coin_count);
        tvUserUndos = view.findViewById(R.id.tv_shop_user_undos);
        tvUserHints = view.findViewById(R.id.tv_shop_user_hints);

        // Free Ad Coins
        view.findViewById(R.id.btn_shop_watch_ad).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            if (getActivity() != null) {
                AdManager.getInstance(requireContext()).showRewardedAd(getActivity(), "shop_coins", 100, new AdManager.RewardCallback() {
                    @Override
                    public void onRewardEarned(int amount, String rewardType) {
                        playerData.addCoins(amount);
                        storage.savePlayerData(playerData);
                        SoundManager.getInstance(requireContext()).playCoin();
                        updateUI();
                        Toast.makeText(requireContext(), "+100 Coins Received!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdFailed() {
                        Toast.makeText(requireContext(), "Ad not available right now. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Booster: Undos (50 coins)
        view.findViewById(R.id.btn_buy_booster_undo).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            if (playerData.spendCoins(50)) {
                playerData.addExtraUndos(3);
                storage.savePlayerData(playerData);
                SoundManager.getInstance(requireContext()).playCoin();
                updateUI();
                Toast.makeText(requireContext(), R.string.purchase_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
            }
        });

        // Booster: Hint (30 coins)
        view.findViewById(R.id.btn_buy_booster_hint).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            if (playerData.spendCoins(30)) {
                playerData.addHints(1);
                storage.savePlayerData(playerData);
                SoundManager.getInstance(requireContext()).playCoin();
                updateUI();
                Toast.makeText(requireContext(), R.string.purchase_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
            }
        });

        // Coin Packs
        view.findViewById(R.id.btn_buy_pack_1).setOnClickListener(v -> simulatePurchase(500));
        view.findViewById(R.id.btn_buy_pack_2).setOnClickListener(v -> simulatePurchase(1000));
        view.findViewById(R.id.btn_buy_pack_3).setOnClickListener(v -> simulatePurchase(2500));
        view.findViewById(R.id.btn_buy_pack_4).setOnClickListener(v -> simulatePurchase(5000));

        updateUI();
    }

    private void simulatePurchase(int coins) {
        SoundManager.getInstance(requireContext()).playCoin();
        playerData.addCoins(coins);
        storage.savePlayerData(playerData);
        updateUI();
        Toast.makeText(requireContext(), "+" + coins + " Coins Added!", Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        tvCoinCount.setText(String.valueOf(playerData.getCoins()));
        tvUserUndos.setText("Have: " + playerData.getExtraUndosRemaining());
        tvUserHints.setText("Have: " + playerData.getHintsRemaining());
    }
}
