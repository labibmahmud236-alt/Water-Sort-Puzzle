package com.aquasort.puzzle.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.aquasort.puzzle.game.GameState;
import com.aquasort.puzzle.game.Move;

/**
 * Controller coordinating the complete physical multi-stage pouring sequence:
 * 1. Approach destination container
 * 2. Natural tilt around container physical opening pivot
 * 3. Dynamic liquid surface inclination inside source container
 * 4. Cubic Bézier stream eruption and fluid flow
 * 5. Destination receiving wave ripples and smooth layer rise
 * 6. Sequential multi-layer pours
 * 7. Stream cut-off, smooth return, and liquid settling
 */
public class PourAnimationController {

    private boolean isRunning = false;

    public PourAnimationController() {}

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Executes the realistic multi-stage pouring sequence.
     */
    public void executePour(
            final TubeView srcView,
            final TubeView dstView,
            final PourOverlayView pourOverlay,
            final Move move,
            final GameState gameState,
            final Runnable onComplete
    ) {
        if (srcView == null || dstView == null || pourOverlay == null || move == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        isRunning = true;

        // Calculate positions on screen
        int[] srcPos = new int[2];
        int[] dstPos = new int[2];
        srcView.getLocationOnScreen(srcPos);
        dstView.getLocationOnScreen(dstPos);

        float deltaX = dstPos[0] - srcPos[0];
        final boolean isPouringRight = deltaX >= 0;

        // Container-specific tilt and pivot
        float optimalTilt = srcView.getOptimalTiltAngle();
        final float targetTilt = isPouringRight ? optimalTilt : -optimalTilt;

        PointF pivot = srcView.getPourPivot(isPouringRight);
        srcView.setPivotX(pivot.x);
        srcView.setPivotY(pivot.y);

        // Calculate approach offset
        float approachX = isPouringRight
                ? (deltaX - srcView.getWidth() * 0.72f)
                : (deltaX + dstView.getWidth() * 0.72f);
        float liftY = -srcView.getHeight() * 0.28f;

        // Elevate source view in Z space during pour
        srcView.bringToFront();

        // 1. Stage 1 & 2: Translate and Tilt to opening position
        ObjectAnimator animX = ObjectAnimator.ofFloat(srcView, "translationX", srcView.getTranslationX(), approachX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(srcView, "translationY", srcView.getTranslationY(), liftY);
        ObjectAnimator animRot = ObjectAnimator.ofFloat(srcView, "rotation", 0f, targetTilt);

        ValueAnimator animLiquidTilt = ValueAnimator.ofFloat(0f, targetTilt);
        animLiquidTilt.addUpdateListener(animation -> srcView.setTiltDegrees((float) animation.getAnimatedValue()));

        AnimatorSet approachSet = new AnimatorSet();
        approachSet.playTogether(animX, animY, animRot, animLiquidTilt);
        approachSet.setDuration(280);
        approachSet.setInterpolator(new AccelerateDecelerateInterpolator());

        approachSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 2. Stage 3: Calculate dynamic stream endpoints in overlay coordinates
                int[] overlayPos = new int[2];
                pourOverlay.getLocationOnScreen(overlayPos);

                int[] sPos = new int[2];
                int[] dPos = new int[2];
                srcView.getLocationOnScreen(sPos);
                dstView.getLocationOnScreen(dPos);

                PointF openPt = srcView.getOpeningPoint(isPouringRight);
                float streamStartX = (sPos[0] + openPt.x) - overlayPos[0];
                float streamStartY = (sPos[1] + openPt.y) - overlayPos[1];
                float streamEndX = (dPos[0] + dstView.getWidth() * 0.5f) - overlayPos[0];
                float streamEndY = (dPos[1] + dstView.getHeight() * 0.10f) - overlayPos[1];

                // Start dynamic liquid stream
                pourOverlay.startStream(streamStartX, streamStartY, streamEndX, streamEndY, move.getColor());

                // 3. Stage 4: Multi-layer pouring flow
                final int totalUnits = move.getCount();
                animatePoursRecursively(srcView, dstView, pourOverlay, move, gameState, totalUnits, 0, () -> {
                    // Stop stream
                    pourOverlay.stopStream();

                    // 4. Stage 5: Return source container to resting board position
                    srcView.setPivotX(srcView.getWidth() / 2f);
                    srcView.setPivotY(srcView.getHeight() / 2f);

                    ObjectAnimator retX = ObjectAnimator.ofFloat(srcView, "translationX", 0f);
                    ObjectAnimator retY = ObjectAnimator.ofFloat(srcView, "translationY", 0f);
                    ObjectAnimator retRot = ObjectAnimator.ofFloat(srcView, "rotation", 0f);
                    ValueAnimator retLiquidTilt = ValueAnimator.ofFloat(srcView.getTiltDegrees(), 0f);
                    retLiquidTilt.addUpdateListener(anim -> srcView.setTiltDegrees((float) anim.getAnimatedValue()));

                    AnimatorSet returnSet = new AnimatorSet();
                    returnSet.playTogether(retX, retY, retRot, retLiquidTilt);
                    returnSet.setDuration(250);
                    returnSet.setInterpolator(new DecelerateInterpolator());

                    returnSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // Update both views with settled game state
                            srcView.setTubeData(gameState.getTube(move.getFromIndex()));
                            dstView.setTubeData(gameState.getTube(move.getToIndex()));
                            srcView.setSelectedState(false);
                            dstView.setSelectedState(false);

                            isRunning = false;
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        }
                    });
                    returnSet.start();
                });
            }
        });

        approachSet.start();
    }

    /**
     * Handles single or multi-layer sequential liquid flow with ripple reactions.
     */
    private void animatePoursRecursively(
            final TubeView srcView,
            final TubeView dstView,
            final PourOverlayView pourOverlay,
            final Move move,
            final GameState gameState,
            final int totalUnits,
            final int currentStep,
            final Runnable onAllPoursFinished
    ) {
        if (currentStep >= totalUnits) {
            if (onAllPoursFinished != null) onAllPoursFinished.run();
            return;
        }

        long stepDuration = totalUnits > 1 ? 260 : 340;

        // Current layer index being drained in source
        int srcLayerIdx = srcView.getLayers().size() - 1;
        // Target slot index in destination
        int dstLayerIdx = dstView.getLayers().size();

        // Add placeholder layer to destination view so fill target can render
        if (dstView.getLayers().size() <= dstLayerIdx) {
            dstView.getLayers().add(move.getColor());
        }

        srcView.animateDrainTop(srcLayerIdx, stepDuration, () -> {
            if (srcView.getLayers().size() > 0) {
                srcView.getLayers().remove(srcView.getLayers().size() - 1);
                srcView.invalidate();
            }
        });

        dstView.animateFillTarget(dstLayerIdx, stepDuration, () -> {
            // Recurse for remaining layers in a multi-unit pour
            animatePoursRecursively(srcView, dstView, pourOverlay, move, gameState, totalUnits, currentStep + 1, onAllPoursFinished);
        });
    }
}
