package com.aquasort.puzzle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.aquasort.puzzle.R;

public class NeedMoreUndosDialog extends Dialog {

    public interface UndosDialogListener {
        void onUseCoinsForUndos();
        void onWatchAdForUndos();
    }

    private final UndosDialogListener listener;

    public NeedMoreUndosDialog(@NonNull Context context, UndosDialogListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_need_more_undos);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        findViewById(R.id.btn_undos_coins).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onUseCoinsForUndos();
        });

        findViewById(R.id.btn_undos_ad).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onWatchAdForUndos();
        });

        findViewById(R.id.tv_undos_cancel).setOnClickListener(v -> dismiss());
    }
}
