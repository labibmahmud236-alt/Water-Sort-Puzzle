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

import com.aquasort.puzzle.R;

public class PauseDialog extends Dialog {

    public interface PauseListener {
        void onResumeClicked();
        void onRestartClicked();
        void onLevelsClicked();
        void onHomeClicked();
    }

    private final int currentLevel;
    private final PauseListener listener;

    public PauseDialog(@NonNull Context context, int currentLevel, PauseListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.currentLevel = currentLevel;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pause);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setCancelable(false);

        TextView tvLevel = findViewById(R.id.tv_pause_level);
        if (tvLevel != null) {
            tvLevel.setText(getContext().getString(R.string.level_title_format, currentLevel));
        }

        findViewById(R.id.btn_pause_resume).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onResumeClicked();
        });

        findViewById(R.id.btn_pause_restart).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onRestartClicked();
        });

        findViewById(R.id.btn_pause_levels).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onLevelsClicked();
        });

        findViewById(R.id.btn_pause_home).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onHomeClicked();
        });
    }
}
