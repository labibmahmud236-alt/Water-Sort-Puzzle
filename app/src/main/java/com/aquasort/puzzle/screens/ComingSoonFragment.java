package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.MainActivity;
import com.aquasort.puzzle.R;
import com.aquasort.puzzle.services.SoundManager;

public class ComingSoonFragment extends Fragment {

    public static ComingSoonFragment newInstance() {
        return new ComingSoonFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coming_soon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_coming_soon_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).navigateTo(LevelSelectFragment.newInstance(), false);
            }
        });
    }
}
