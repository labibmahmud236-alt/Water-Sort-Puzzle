package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.dialogs.HowToPlayDialog;
import com.aquasort.puzzle.dialogs.RateUsDialog;
import com.aquasort.puzzle.models.PlayerData;
import com.aquasort.puzzle.services.ShareManager;
import com.aquasort.puzzle.services.SoundManager;
import com.aquasort.puzzle.services.StorageManager;
import com.aquasort.puzzle.services.VibrationManager;

public class SettingsFragment extends Fragment {

    private StorageManager storage;
    private PlayerData playerData;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = StorageManager.getInstance(requireContext());
        playerData = storage.loadPlayerData();

        view.findViewById(R.id.btn_settings_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            requireActivity().onBackPressed();
        });

        // Switches
        SwitchCompat switchSound = view.findViewById(R.id.switch_sound);
        SwitchCompat switchMusic = view.findViewById(R.id.switch_music);
        SwitchCompat switchVibration = view.findViewById(R.id.switch_vibration);
        SwitchCompat switchNotifications = view.findViewById(R.id.switch_notifications);

        switchSound.setChecked(playerData.isSoundEnabled());
        switchMusic.setChecked(playerData.isMusicEnabled());
        switchVibration.setChecked(playerData.isVibrationEnabled());
        switchNotifications.setChecked(playerData.isNotificationsEnabled());

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playerData.setSoundEnabled(isChecked);
            storage.savePlayerData(playerData);
            SoundManager.getInstance(requireContext()).setSoundEnabled(isChecked);
        });

        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playerData.setMusicEnabled(isChecked);
            storage.savePlayerData(playerData);
            SoundManager.getInstance(requireContext()).setMusicEnabled(isChecked);
        });

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playerData.setVibrationEnabled(isChecked);
            storage.savePlayerData(playerData);
            VibrationManager.getInstance(requireContext()).setEnabled(isChecked);
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playerData.setNotificationsEnabled(isChecked);
            storage.savePlayerData(playerData);
        });

        // Action Buttons
        view.findViewById(R.id.btn_settings_how_to_play).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            new HowToPlayDialog(requireContext(), null).show();
        });

        view.findViewById(R.id.btn_settings_rate_us).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            new RateUsDialog(requireContext()).show();
        });

        view.findViewById(R.id.btn_settings_share).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            ShareManager.shareApp(getActivity());
        });

        view.findViewById(R.id.btn_settings_privacy).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateTo(LegalFragment.newInstance(LegalFragment.TYPE_PRIVACY), true);
            }
        });

        view.findViewById(R.id.btn_settings_terms).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateTo(LegalFragment.newInstance(LegalFragment.TYPE_TERMS), true);
            }
        });

        TextView tvVersion = view.findViewById(R.id.tv_settings_version);
        tvVersion.setText(getString(R.string.settings_version_format, "1.0.0"));
    }
}
