package com.aquasort.puzzle.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.aquasort.puzzle.R;

public class RestartDialog extends Dialog {

    public interface RestartListener {
        void onConfirmRestart();
        void onCancelRestart();
    }

    private final RestartListener listener;

    public RestartDialog(@NonNull Context context, RestartListener listener) {
        super(context, R.style.Theme_AquaSort_Dialog);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_restart);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        findViewById(R.id.btn_restart_confirm).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onConfirmRestart();
        });

        findViewById(R.id.btn_restart_cancel).setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onCancelRestart();
        });
    }
}
