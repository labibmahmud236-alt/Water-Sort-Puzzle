package com.aquasort.puzzle.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.services.ContainerSkinManager;
import com.aquasort.puzzle.views.SkinPreviewView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for browsing and purchasing container skins in the Shop.
 */
public class ContainerSkinAdapter extends RecyclerView.Adapter<ContainerSkinAdapter.SkinViewHolder> {

    public interface SkinActionListener {
        void onSkinSelected(ContainerSkin skin);
        void onSkinBuy(ContainerSkin skin);
        void onSkinEquip(ContainerSkin skin);
    }

    private final Context context;
    private final ContainerSkinManager skinManager;
    private final List<ContainerSkin> skinList = new ArrayList<>();
    private final SkinActionListener listener;

    public ContainerSkinAdapter(Context context, List<ContainerSkin> skins, SkinActionListener listener) {
        this.context = context;
        this.skinManager = ContainerSkinManager.getInstance(context);
        this.listener = listener;
        if (skins != null) {
            this.skinList.addAll(skins);
        }
    }

    public void updateList(List<ContainerSkin> newSkins) {
        skinList.clear();
        if (newSkins != null) {
            skinList.addAll(newSkins);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SkinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_skin, parent, false);
        return new SkinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkinViewHolder holder, int position) {
        ContainerSkin skin = skinList.get(position);
        holder.bind(skin);
    }

    @Override
    public int getItemCount() {
        return skinList.size();
    }

    class SkinViewHolder extends RecyclerView.ViewHolder {

        private final View cardView;
        private final TextView tvRarity;
        private final SkinPreviewView previewView;
        private final TextView tvName;
        private final View layoutBtnBuy;
        private final TextView tvPrice;
        private final AppCompatButton btnEquip;
        private final View layoutEquipped;

        public SkinViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_skin_container);
            tvRarity = itemView.findViewById(R.id.tv_skin_rarity);
            previewView = itemView.findViewById(R.id.preview_skin_view);
            tvName = itemView.findViewById(R.id.tv_skin_name);
            layoutBtnBuy = itemView.findViewById(R.id.layout_btn_buy);
            tvPrice = itemView.findViewById(R.id.tv_skin_price);
            btnEquip = itemView.findViewById(R.id.btn_skin_equip);
            layoutEquipped = itemView.findViewById(R.id.layout_skin_equipped);
        }

        public void bind(final ContainerSkin skin) {
            tvName.setText(skin.getName());
            previewView.setSkin(skin);

            // Rarity Tag Styling
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
                layoutEquipped.setVisibility(View.VISIBLE);
                btnEquip.setVisibility(View.GONE);
                layoutBtnBuy.setVisibility(View.GONE);
            } else if (isUnlocked) {
                layoutEquipped.setVisibility(View.GONE);
                btnEquip.setVisibility(View.VISIBLE);
                layoutBtnBuy.setVisibility(View.GONE);
            } else {
                layoutEquipped.setVisibility(View.GONE);
                btnEquip.setVisibility(View.GONE);
                layoutBtnBuy.setVisibility(View.VISIBLE);
                tvPrice.setText(skin.isFree() ? "FREE" : String.valueOf(skin.getPrice()));
            }

            cardView.setOnClickListener(v -> {
                if (listener != null) listener.onSkinSelected(skin);
            });

            btnEquip.setOnClickListener(v -> {
                if (listener != null) listener.onSkinEquip(skin);
            });

            layoutBtnBuy.setOnClickListener(v -> {
                if (listener != null) listener.onSkinBuy(skin);
            });
        }
    }
}
