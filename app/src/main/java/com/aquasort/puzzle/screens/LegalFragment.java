package com.aquasort.puzzle.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.services.SoundManager;

public class LegalFragment extends Fragment {

    public static final int TYPE_PRIVACY = 1;
    public static final int TYPE_TERMS = 2;

    private static final String ARG_TYPE = "arg_legal_type";

    public static LegalFragment newInstance(int type) {
        LegalFragment fragment = new LegalFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_legal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int type = getArguments() != null ? getArguments().getInt(ARG_TYPE, TYPE_PRIVACY) : TYPE_PRIVACY;

        view.findViewById(R.id.btn_legal_back).setOnClickListener(v -> {
            SoundManager.getInstance(requireContext()).playClick();
            requireActivity().onBackPressed();
        });

        TextView tvTitle = view.findViewById(R.id.tv_legal_title);
        TextView tvContent = view.findViewById(R.id.tv_legal_content);

        if (type == TYPE_PRIVACY) {
            tvTitle.setText(R.string.action_privacy);
            tvContent.setText(getPrivacyText());
        } else {
            tvTitle.setText(R.string.action_terms);
            tvContent.setText(getTermsText());
        }
    }

    private String getPrivacyText() {
        return "PRIVACY POLICY FOR AQUA SORT\n\n" +
                "Effective Date: September 2026\n\n" +
                "1. INTRODUCTION\n" +
                "Welcome to Aqua Sort (\"we\", \"our\", or \"us\"). We respect your privacy and are committed to protecting your personal data. This Privacy Policy outlines how your information is handled when you enjoy Aqua Sort.\n\n" +
                "2. DATA WE COLLECT\n" +
                "Aqua Sort does not collect any personal identity information such as your name, address, or email address. All gameplay data, coin balances, completed levels, and puzzle progress are stored locally on your device using Android SharedPreferences.\n\n" +
                "3. THIRD-PARTY SERVICES & ADVERTISING\n" +
                "We use Google AdMob to serve non-intrusive advertisements (banners, interstitials, and rewarded videos). Google AdMob may collect device identifiers, approximate location, and diagnostic information in accordance with Google's Privacy Policy.\n\n" +
                "4. CHILDREN'S PRIVACY\n" +
                "Our application does not knowingly collect personal information from children under the age of 13. All content is family-friendly.\n\n" +
                "5. CONTACT US\n" +
                "If you have any questions or feedback regarding this Privacy Policy, please contact us at support@aquasort.puzzle.";
    }

    private String getTermsText() {
        return "TERMS & CONDITIONS FOR AQUA SORT\n\n" +
                "Effective Date: September 2026\n\n" +
                "1. ACCEPTANCE OF TERMS\n" +
                "By downloading, installing, or playing Aqua Sort, you agree to be bound by these Terms & Conditions. If you do not agree, please do not use the application.\n\n" +
                "2. INTELLECTUAL PROPERTY\n" +
                "All trademarks, visual designs, liquid rendering mechanics, audio effects, level configurations, and code are the exclusive intellectual property of Aqua Sort and its licensors.\n\n" +
                "3. IN-GAME VIRTUAL CURRENCY\n" +
                "Coins and power-up boosters in Aqua Sort are virtual entertainment items. They hold no real-world monetary value and cannot be redeemed for legal currency.\n\n" +
                "4. DISCLAIMER & LIMITATION OF LIABILITY\n" +
                "Aqua Sort is provided on an \"AS IS\" and \"AS AVAILABLE\" basis without warranties of any kind. In no event shall developers be held liable for any damages arising out of your use of the application.\n\n" +
                "5. CHANGES TO TERMS\n" +
                "We reserve the right to modify these Terms at any time. Continued use of the application signifies your acceptance of updated terms.";
    }
}
