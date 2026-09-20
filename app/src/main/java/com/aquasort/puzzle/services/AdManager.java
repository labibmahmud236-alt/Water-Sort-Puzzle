package com.aquasort.puzzle.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.aquasort.puzzle.R;
import com.aquasort.puzzle.utils.Constants;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * Encapsulates Google Mobile Ads SDK integration (Banner, Interstitial, Rewarded, App Open).
 * Ad Unit IDs are pulled from resources for easy production release configuration.
 */
public class AdManager {

    private static final String TAG = "AquaSort_AdManager";
    private static AdManager instance;

    private final Context appContext;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private AppOpenAd appOpenAd;
    private boolean isAdShowing = false;
    private boolean isAppOpenLoading = false;
    private boolean isInterstitialLoading = false;
    private boolean isRewardedLoading = false;

    public interface RewardCallback {
        void onRewardEarned(int amount, String rewardType);
        void onAdFailed();
    }

    public static synchronized AdManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdManager(context.getApplicationContext());
        }
        return instance;
    }

    private AdManager(Context context) {
        this.appContext = context;
        try {
            MobileAds.initialize(context, initializationStatus -> {
                Log.d(TAG, "AdMob MobileAds initialized.");
                loadInterstitialAd();
                loadRewardedAd();
                loadAppOpenAd();
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed initializing MobileAds", e);
        }
    }

    // ==================== BANNER AD ====================

    public AdView createBannerAd(Activity activity, ViewGroup container) {
        try {
            AdView adView = new AdView(activity);
            adView.setAdUnitId(activity.getString(R.string.admob_banner_id));
            adView.setAdSize(AdSize.BANNER);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            container.removeAllViews();
            container.addView(adView);
            return adView;
        } catch (Exception e) {
            Log.e(TAG, "Error creating banner ad", e);
            return null;
        }
    }

    // ==================== INTERSTITIAL AD ====================

    public void loadInterstitialAd() {
        if (isInterstitialLoading || interstitialAd != null) return;
        isInterstitialLoading = true;

        AdRequest adRequest = new AdRequest.Builder().build();
        String adUnitId = appContext.getString(R.string.admob_interstitial_id);

        InterstitialAd.load(appContext, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                interstitialAd = ad;
                isInterstitialLoading = false;
                Log.d(TAG, "Interstitial loaded.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                interstitialAd = null;
                isInterstitialLoading = false;
                Log.d(TAG, "Interstitial failed to load: " + loadAdError.getMessage());
            }
        });
    }

    public void showInterstitialIfEligible(Activity activity, int completedLevel, final Runnable onDismiss) {
        // Show after every INTERSTITIAL_LEVEL_INTERVAL (e.g. 2, 4, 6, 8...)
        if (completedLevel % Constants.INTERSTITIAL_LEVEL_INTERVAL != 0) {
            if (onDismiss != null) onDismiss.run();
            return;
        }

        if (interstitialAd != null && !isAdShowing && activity != null && !activity.isFinishing()) {
            isAdShowing = true;
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isAdShowing = false;
                    interstitialAd = null;
                    loadInterstitialAd();
                    if (onDismiss != null) onDismiss.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    isAdShowing = false;
                    interstitialAd = null;
                    loadInterstitialAd();
                    if (onDismiss != null) onDismiss.run();
                }
            });
            interstitialAd.show(activity);
        } else {
            loadInterstitialAd();
            if (onDismiss != null) onDismiss.run();
        }
    }

    // ==================== REWARDED AD ====================

    public void loadRewardedAd() {
        if (isRewardedLoading || rewardedAd != null) return;
        isRewardedLoading = true;

        AdRequest adRequest = new AdRequest.Builder().build();
        String adUnitId = appContext.getString(R.string.admob_rewarded_id);

        RewardedAd.load(appContext, adUnitId, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                rewardedAd = ad;
                isRewardedLoading = false;
                Log.d(TAG, "Rewarded ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                rewardedAd = null;
                isRewardedLoading = false;
                Log.d(TAG, "Rewarded ad failed to load: " + loadAdError.getMessage());
            }
        });
    }

    public void showRewardedAd(Activity activity, final String rewardType, final int amount, final RewardCallback callback) {
        if (rewardedAd != null && !isAdShowing && activity != null && !activity.isFinishing()) {
            isAdShowing = true;
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isAdShowing = false;
                    rewardedAd = null;
                    loadRewardedAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    isAdShowing = false;
                    rewardedAd = null;
                    loadRewardedAd();
                    if (callback != null) callback.onAdFailed();
                }
            });

            rewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    if (callback != null) {
                        callback.onRewardEarned(amount, rewardType);
                    }
                }
            });
        } else {
            loadRewardedAd();
            if (callback != null) {
                callback.onAdFailed();
            }
        }
    }

    // ==================== APP OPEN AD ====================

    public void loadAppOpenAd() {
        if (isAppOpenLoading || appOpenAd != null) return;
        isAppOpenLoading = true;

        AdRequest adRequest = new AdRequest.Builder().build();
        String adUnitId = appContext.getString(R.string.admob_app_open_id);

        AppOpenAd.load(appContext, adUnitId, adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isAppOpenLoading = false;
                        Log.d(TAG, "App Open ad loaded.");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        appOpenAd = null;
                        isAppOpenLoading = false;
                        Log.d(TAG, "App Open ad failed to load: " + loadAdError.getMessage());
                    }
                });
    }

    public void showAppOpenAdIfAvailable(Activity activity, final Runnable onComplete) {
        if (appOpenAd != null && !isAdShowing && activity != null && !activity.isFinishing()) {
            isAdShowing = true;
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isAdShowing = false;
                    appOpenAd = null;
                    loadAppOpenAd();
                    if (onComplete != null) onComplete.run();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    isAdShowing = false;
                    appOpenAd = null;
                    loadAppOpenAd();
                    if (onComplete != null) onComplete.run();
                }
            });
            appOpenAd.show(activity);
        } else {
            if (onComplete != null) onComplete.run();
        }
    }

    public boolean isAdShowing() {
        return isAdShowing;
    }
}
