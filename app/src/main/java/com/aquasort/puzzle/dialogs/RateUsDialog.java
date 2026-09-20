package com.aquasort.puzzle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.StorageManager;

public class RateUsDialog extends Dialog {

    public RateUsDialog(@NonNull Context context) {
        super(context, R.style.Theme_AquaSort_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rate_us);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        findViewById(R.id.btn_rate_confirm).setOnClickListener(v -> {
            dismiss();
            PlayerData data = StorageManager.getInstance(getContext()).loadPlayerData();
            data.setHasRated(true);
            StorageManager.getInstance(getContext()).savePlayerData(data);

            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getContext().getPackageName())));
            } catch (Exception e) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
            }
        });

        findViewById(R.id.tv_rate_later).setOnClickListener(v -> dismiss());
    }
}
