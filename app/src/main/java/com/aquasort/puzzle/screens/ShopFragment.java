package com.aquasort.puzzle.screens;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.adapters.ContainerSkinAdapter;
import com.aquasort.puzzle.dialogs.SkinPreviewDialog;
import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.AdManager;
import com.aquasort.puzzle.services.ContainerSkinManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;

import java.util.List;

public class ShopFragment extends Fragment implements ContainerSkinAdapter.SkinActionListener {

    private StorageManager storage;
    private ContainerSkinManager skinManager;
    private PlayerData playerData;

    private TextView tvCoinCount;
    private TextView tvUserUndos;
    private TextView tvUserHints;

    private RecyclerView rvContainerSkins;
    private ContainerSkinAdapter skinAdapter;
    private String selectedCategory = ContainerSkin.CAT_ALL;

    private TextView tabAll, tabGlass, tabBottles, tabCups, tabMugs, tabFantasy, tabPremium;

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
        skinManager = ContainerSkinManager.getInstance(requireContext());
        playerData = storage.loadPlayerData();

        view.findViewById(R.id.btn_shop_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            requireActivity().onBackPressed();
        });

        tvCoinCount = view.findViewById(R.id.tv_shop_coin_count);
        tvUserUndos = view.findViewById(R.id.tv_shop_user_undos);
        tvUserHints = view.findViewById(R.id.tv_shop_user_hints);

        // Container Skins Grid
        rvContainerSkins = view.findViewById(R.id.rv_container_skins);
        rvContainerSkins.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        skinAdapter = new ContainerSkinAdapter(requireContext(), skinManager.getAllSkins(), this);
        rvContainerSkins.setAdapter(skinAdapter);

        // Category Tabs
        setupCategoryTabs(view);

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

    private void setupCategoryTabs(View view) {
        tabAll = view.findViewById(R.id.tab_cat_all);
        tabGlass = view.findViewById(R.id.tab_cat_glass);
        tabBottles = view.findViewById(R.id.tab_cat_bottles);
        tabCups = view.findViewById(R.id.tab_cat_cups);
        tabMugs = view.findViewById(R.id.tab_cat_mugs);
        tabFantasy = view.findViewById(R.id.tab_cat_fantasy);
        tabPremium = view.findViewById(R.id.tab_cat_premium);

        tabAll.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_ALL, tabAll));
        tabGlass.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_GLASS, tabGlass));
        tabBottles.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_BOTTLES, tabBottles));
        tabCups.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_CUPS, tabCups));
        tabMugs.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_MUGS, tabMugs));
        tabFantasy.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_FANTASY, tabFantasy));
        tabPremium.setOnClickListener(v -> selectCategory(ContainerSkin.CAT_PREMIUM, tabPremium));
    }

    private void selectCategory(String category, TextView selectedTab) {
        SoundManager.getInstance(requireContext()).playClick();
        selectedCategory = category;

        // Reset all tabs
        TextView[] tabs = new TextView[]{tabAll, tabGlass, tabBottles, tabCups, tabMugs, tabFantasy, tabPremium};
        for (TextView t : tabs) {
            t.setBackgroundResource(R.drawable.bg_tab_unselected);
            t.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // Highlight selected
        selectedTab.setBackgroundResource(R.drawable.bg_tab_selected);
        selectedTab.setTextColor(Color.parseColor("#0A192F"));

        List<ContainerSkin> filtered = skinManager.getSkinsByCategory(selectedCategory);
        skinAdapter.updateList(filtered);
    }

    // ==================== CONTAINER SKIN ACTIONS ====================

    @Override
    public void onSkinSelected(ContainerSkin skin) {
        SoundManager.getInstance(requireContext()).playClick();
        SkinPreviewDialog dialog = new SkinPreviewDialog(requireContext(), skin, new SkinPreviewDialog.SkinPreviewListener() {
            @Override
            public void onSkinEquipped(ContainerSkin equipped) {
                updateUI();
            }

            @Override
            public void onSkinPurchased(ContainerSkin purchased) {
                playerData = storage.loadPlayerData();
                updateUI();
            }
        });
        dialog.show();
    }

    @Override
    public void onSkinBuy(ContainerSkin skin) {
        if (skin.isFree() || playerData.spendCoins(skin.getPrice())) {
            storage.savePlayerData(playerData);
            skinManager.unlockSkin(skin.getId());
            skinManager.setEquippedSkin(skin.getId());
            SoundManager.getInstance(requireContext()).playCoin();
            Toast.makeText(requireContext(), skin.getName() + " Unlocked & Equipped!", Toast.LENGTH_SHORT).show();
            updateUI();
        } else {
            SoundManager.getInstance(requireContext()).playInvalid();
            Toast.makeText(requireContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSkinEquip(ContainerSkin skin) {
        SoundManager.getInstance(requireContext()).playClick();
        skinManager.setEquippedSkin(skin.getId());
        Toast.makeText(requireContext(), skin.getName() + " Equipped!", Toast.LENGTH_SHORT).show();
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
        if (!isAdded()) return;
        playerData = storage.loadPlayerData();
        tvCoinCount.setText(String.valueOf(playerData.getCoins()));
        tvUserUndos.setText("Have: " + playerData.getExtraUndosRemaining());
        tvUserHints.setText("Have: " + playerData.getHintsRemaining());

        if (skinAdapter != null) {
            skinAdapter.updateList(skinManager.getSkinsByCategory(selectedCategory));
        }
    }
}
