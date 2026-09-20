package com.aquasort.puzzle;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.aquasort.puzzle.screens.SplashFragment;
import com.aquasort.puzzle.services.AdManager;
import com.aquasort.puzzle.services.SoundManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AdManager early
        AdManager.getInstance(this);

        if (savedInstanceState == null) {
            navigateTo(SplashFragment.newInstance(), false);
        }
    }

    public void navigateTo(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundManager.getInstance(this).resumeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.getInstance(this).pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundManager.getInstance(this).release();
    }
}
