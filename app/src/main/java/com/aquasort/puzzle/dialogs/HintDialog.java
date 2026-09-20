package com.aquasort.puzzle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.aquasort.puzzle.R;

public class HintDialog extends Dialog {

    public interface HintDialogListener {
        void onUseCoinsForHint();
        void onWatchAdForHint();
    }

    private final HintDialogListener listener;

    public HintDialog(@NonNull Context context, HintDialogListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hint);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        findViewById(R.id.btn_hint_coins).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onUseCoinsForHint();
        });

        findViewById(R.id.btn_hint_ad).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onWatchAdForHint();
        });

        findViewById(R.id.tv_hint_cancel).setOnClickListener(v -> dismiss());
    }
}
