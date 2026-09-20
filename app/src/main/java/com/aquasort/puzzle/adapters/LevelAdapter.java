package com.aquasort.puzzle.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.models.LevelProgress;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.utils.Constants;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {

    public interface OnLevelClickListener {
        void onLevelClick(int levelId);
    }

    private final Context context;
    private final int currentMaxUnlockedLevel;
    private final OnLevelClickListener listener;
    private final StorageManager storage;

    public LevelAdapter(Context context, int currentMaxUnlockedLevel, OnLevelClickListener listener) {
        this.context = context;
        this.currentMaxUnlockedLevel = currentMaxUnlockedLevel;
        this.listener = listener;
        this.storage = StorageManager.getInstance(context);
    }

    @NonNull
    @Override
    public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_level, parent, false);
        return new LevelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
        int levelId = position + 1;
        holder.tvLevelNumber.setText(String.valueOf(levelId));

        LevelProgress progress = storage.getLevelProgress(levelId);
        boolean isUnlocked = levelId <= currentMaxUnlockedLevel;
        boolean isCurrent = levelId == currentMaxUnlockedLevel && !progress.isCompleted();

        if (progress.isCompleted()) {
            // Completed state: show stars
            holder.card.setBackgroundResource(R.drawable.bg_glass_card);
            holder.tvLevelNumber.setTextColor(context.getResources().getColor(R.color.level_completed));
            holder.ivLock.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.GONE);
            holder.starsLayout.setVisibility(View.VISIBLE);

            int stars = Math.max(1, progress.getStars());
            holder.star1.setImageResource(stars >= 1 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
            holder.star2.setImageResource(stars >= 2 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
            holder.star3.setImageResource(stars >= 3 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);

            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onLevelClick(levelId);
            });
        } else if (isCurrent) {
            // Current level to play: prominent cyan highlight
            holder.card.setBackgroundResource(R.drawable.bg_glass_card_accent);
            holder.tvLevelNumber.setTextColor(context.getResources().getColor(R.color.primary));
            holder.starsLayout.setVisibility(View.GONE);
            holder.ivLock.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onLevelClick(levelId);
            });
        } else if (isUnlocked) {
            // Unlocked but not current/completed
            holder.card.setBackgroundResource(R.drawable.bg_glass_card);
            holder.tvLevelNumber.setTextColor(context.getResources().getColor(R.color.text_primary));
            holder.starsLayout.setVisibility(View.GONE);
            holder.ivLock.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onLevelClick(levelId);
            });
        } else {
            // Locked level
            holder.card.setBackgroundResource(R.drawable.bg_glass_card);
            holder.tvLevelNumber.setTextColor(context.getResources().getColor(R.color.level_locked_text));
            holder.starsLayout.setVisibility(View.GONE);
            holder.ivPlay.setVisibility(View.GONE);
            holder.ivLock.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(0.55f);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return Constants.TOTAL_LEVELS;
    }

    static class LevelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout card;
        TextView tvLevelNumber;
        LinearLayout starsLayout;
        ImageView star1, star2, star3;
        ImageView ivLock;
        ImageView ivPlay;

        public LevelViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.layout_level_card);
            tvLevelNumber = itemView.findViewById(R.id.tv_level_number);
            starsLayout = itemView.findViewById(R.id.layout_level_stars);
            star1 = itemView.findViewById(R.id.iv_level_star1);
            star2 = itemView.findViewById(R.id.iv_level_star2);
            star3 = itemView.findViewById(R.id.iv_level_star3);
            ivLock = itemView.findViewById(R.id.iv_level_lock);
            ivPlay = itemView.findViewById(R.id.iv_level_play);
        }
    }
}
