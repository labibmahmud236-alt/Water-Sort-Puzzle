package com.aquasort.puzzle.services;

import android.app.Activity;
import android.content.Intent;

import com.aquasort.puzzle.R;

/**
 * Handles Android native sharing for Aqua Sort.
 */
public class ShareManager {

    public static void shareApp(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
            sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_text));
            activity.startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share_chooser_title)));
        } catch (Exception ignored) {}
    }
}
