package com.aquasort.puzzle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.ContainerSkinManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.views.SkinPreviewView;

/**
 * Interactive preview dialog showcasing a container skin in high resolution.
 * Allows tapping the preview view to trigger a demo pour tilt animation,
 * and performing purchase/equip actions directly.
 */
public class SkinPreviewDialog extends Dialog {

    public interface SkinPreviewListener {
        void onSkinEquipped(ContainerSkin skin);
        void onSkinPurchased(ContainerSkin skin);
    }

    private final ContainerSkin skin;
    private final SkinPreviewListener listener;
    private final ContainerSkinManager skinManager;
    private final StorageManager storage;

    public SkinPreviewDialog(@NonNull Context context, ContainerSkin skin, SkinPreviewListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.skin = skin;
        this.listener = listener;
        this.skinManager = ContainerSkinManager.getInstance(context);
        this.storage = StorageManager.getInstance(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_skin_preview);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        findViewById(R.id.btn_preview_close).setOnClickListener(v -> {
            SoundManager.getInstance(getContext()).playClick();
            dismiss();
        });

        SkinPreviewView previewView = findViewById(R.id.preview_large_view);
        TextView tvName = findViewById(R.id.tv_preview_skin_name);
        TextView tvRarity = findViewById(R.id.tv_preview_skin_rarity);
        TextView tvDesc = findViewById(R.id.tv_preview_skin_desc);
        AppCompatButton btnAction = findViewById(R.id.btn_preview_action);

        previewView.setSkin(skin);
        tvName.setText(skin.getName());
        tvDesc.setText(skin.getDescription());

        // Rarity styling
        tvRarity.setText(skin.getRarity());
        if (ContainerSkin.RARITY_LEGENDARY.equals(skin.getRarity())) {
            tvRarity.setBackgroundResource(R.drawable.bg_rarity_legendary);
            tvRarity.setTextColor(Color.parseColor("#FFD166"));
        } else if (ContainerSkin.RARITY_EPIC.equals(skin.getRarity())) {
            tvRarity.setBackgroundResource(R.drawable.bg_rarity_epic);
            tvRarity.setTextColor(Color.parseColor("#A78BFA"));
        } else if (ContainerSkin.RARITY_RARE.equals(skin.getRarity())) {
            tvRarity.setBackgroundResource(R.drawable.bg_rarity_rare);
            tvRarity.setTextColor(Color.parseColor("#60A5FA"));
        } else {
            tvRarity.setBackgroundResource(R.drawable.bg_rarity_common);
            tvRarity.setTextColor(Color.parseColor("#00E5FF"));
        }

        boolean isUnlocked = skinManager.isSkinUnlocked(skin.getId());
        boolean isEquipped = skinManager.getEquippedSkin().getId().equals(skin.getId());

        if (isEquipped) {
            btnAction.setText("CURRENTLY EQUIPPED");
            btnAction.setBackgroundResource(R.drawable.bg_btn_equipped);
            btnAction.setTextColor(Color.parseColor("#06D6A0"));
            btnAction.setEnabled(false);
        } else if (isUnlocked) {
            btnAction.setText("EQUIP SKIN");
            btnAction.setBackgroundResource(R.drawable.bg_btn_secondary);
            btnAction.setTextColor(Color.parseColor("#FFD166"));
            btnAction.setEnabled(true);
            btnAction.setOnClickListener(v -> {
                SoundManager.getInstance(getContext()).playClick();
                skinManager.setEquippedSkin(skin.getId());
                Toast.makeText(getContext(), skin.getName() + " Equipped!", Toast.LENGTH_SHORT).show();
                if (listener != null) listener.onSkinEquipped(skin);
                dismiss();
            });
        } else {
            btnAction.setText(skin.isFree() ? "CLAIM FOR FREE" : "BUY FOR " + skin.getPrice() + " COINS");
            btnAction.setBackgroundResource(R.drawable.bg_btn_primary);
            btnAction.setTextColor(Color.parseColor("#0A192F"));
            btnAction.setEnabled(true);
            btnAction.setOnClickListener(v -> {
                PlayerData player = storage.loadPlayerData();
                if (skin.isFree() || player.spendCoins(skin.getPrice())) {
                    storage.savePlayerData(player);
                    skinManager.unlockSkin(skin.getId());
                    skinManager.setEquippedSkin(skin.getId());
                    SoundManager.getInstance(getContext()).playCoin();
                    Toast.makeText(getContext(), skin.getName() + " Purchased & Equipped!", Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onSkinPurchased(skin);
                    dismiss();
                } else {
                    SoundManager.getInstance(getContext()).playInvalid();
                    Toast.makeText(getContext(), R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Auto-play demo tilt once when opened
        previewView.postDelayed(previewView::playDemoTiltAnimation, 300);
    }
}
