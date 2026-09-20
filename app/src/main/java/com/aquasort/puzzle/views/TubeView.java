package com.aquasort.puzzle.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.models.SkinGeometry;
import com.aquasort.puzzle.services.ContainerSkinManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Premium container view rendered with custom Canvas graphics.
 * Fully supports dynamic container skins, realistic tilted liquid surfaces,
 * destination receiving wave ripples, multi-stage pour animations, and tactile selection feedback.
 */
public class TubeView extends View {

    private int capacity = Tube.DEFAULT_CAPACITY;
    private final List<Integer> layers = new ArrayList<>();
    private final float[] animatedLayerFractions = new float[Tube.DEFAULT_CAPACITY];

    // Renderers and Skin
    private ContainerSkin currentSkin;
    private SkinGeometry geometry;
    private ContainerRenderer containerRenderer;
    private LiquidRenderer liquidRenderer;

    // State
    private boolean isSelected = false;
    private boolean isHinted = false;
    private boolean isInvalidShaking = false;
    private float glowAlphaFraction = 0f;
    private int glowColor = Color.parseColor("#8800E5FF");

    // Dynamic Animation Parameters
    private float tiltDegrees = 0f;
    private float wavePhase = 0f;
    private float waveAmplitude = 0f;
    private ValueAnimator waveAnimator;

    private float density;

    public TubeView(Context context) {
        super(context);
        init(context);
    }

    public TubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TubeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        density = getResources().getDisplayMetrics().density;

        containerRenderer = new ContainerRenderer(density);
        liquidRenderer = new LiquidRenderer(density);

        currentSkin = ContainerSkinManager.getInstance(context).getEquippedSkin();

        for (int i = 0; i < animatedLayerFractions.length; i++) {
            animatedLayerFractions[i] = 1.0f;
        }
    }

    public void setSkin(ContainerSkin skin) {
        if (skin != null) {
            this.currentSkin = skin;
            if (getWidth() > 0 && getHeight() > 0) {
                this.geometry = skin.createGeometry(getWidth(), getHeight(), density);
            }
            invalidate();
        }
    }

    public ContainerSkin getSkin() {
        return currentSkin;
    }

    public SkinGeometry getGeometry() {
        return geometry;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        invalidate();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setTubeData(Tube tube) {
        layers.clear();
        if (tube != null) {
            this.capacity = tube.getCapacity();
            layers.addAll(tube.getLayers());
        }
        for (int i = 0; i < animatedLayerFractions.length; i++) {
            animatedLayerFractions[i] = 1.0f;
        }
        tiltDegrees = 0f;
        waveAmplitude = 0f;
        invalidate();
    }

    public List<Integer> getLayers() {
        return layers;
    }

    public void setTiltDegrees(float tilt) {
        this.tiltDegrees = tilt;
        invalidate();
    }

    public float getTiltDegrees() {
        return tiltDegrees;
    }

    public PointF getOpeningPoint(boolean isPouringRight) {
        if (geometry == null) {
            return new PointF(getWidth() / 2f, 0f);
        }
        return isPouringRight ? geometry.getOpeningPointRight() : geometry.getOpeningPointLeft();
    }

    public PointF getPourPivot(boolean isPouringRight) {
        if (geometry == null) {
            return new PointF(getWidth() / 2f, 0f);
        }
        return isPouringRight ? geometry.getPourPivotRight() : geometry.getPourPivotLeft();
    }

    public float getOptimalTiltAngle() {
        return geometry != null ? geometry.getTiltAngle() : 52f;
    }

    // ==================== SELECTION & FEEDBACK ====================

    public void setSelectedState(boolean selected) {
        if (this.isSelected == selected) return;
        this.isSelected = selected;

        float targetY = selected ? -22f * density : 0f;
        float targetScale = selected ? 1.06f : 1.0f;
        glowColor = Color.parseColor("#CC00E5FF");

        animate()
                .translationY(targetY)
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(220)
                .setInterpolator(selected ? new OvershootInterpolator(1.4f) : new DecelerateInterpolator())
                .start();

        ValueAnimator glowAnim = ValueAnimator.ofFloat(glowAlphaFraction, selected ? 1.0f : 0f);
        glowAnim.setDuration(200);
        glowAnim.addUpdateListener(animation -> {
            glowAlphaFraction = (float) animation.getAnimatedValue();
            invalidate();
        });
        glowAnim.start();
    }

    public void playInvalidShake() {
        isInvalidShaking = true;
        glowColor = Color.parseColor("#E6FF3366");
        glowAlphaFraction = 1.0f;
        invalidate();

        ValueAnimator shake = ValueAnimator.ofFloat(0, -12f * density, 12f * density, -8f * density, 8f * density, -4f * density, 0);
        shake.setDuration(360);
        shake.setInterpolator(new AccelerateDecelerateInterpolator());
        shake.addUpdateListener(animation -> setTranslationX((float) animation.getAnimatedValue()));
        shake.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isInvalidShaking = false;
                glowAlphaFraction = isSelected ? 1.0f : 0f;
                glowColor = Color.parseColor("#CC00E5FF");
                setTranslationX(0);
                invalidate();
            }
        });
        shake.start();
    }

    public void setHintActive(boolean active) {
        this.isHinted = active;
        if (active) {
            glowColor = Color.parseColor("#E6FFD166");
            glowAlphaFraction = 1.0f;
            animate()
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .setDuration(280)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
        } else {
            if (!isSelected) {
                glowAlphaFraction = 0f;
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
            }
        }
        invalidate();
    }

    // ==================== POUR ANIMATIONS ====================

    /**
     * Smooth drain of the top layer.
     */
    public void animateDrainTop(final int layerIndex, long duration, final Runnable onEnd) {
        if (layerIndex < 0 || layerIndex >= animatedLayerFractions.length) {
            if (onEnd != null) onEnd.run();
            return;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            animatedLayerFractions[layerIndex] = (float) animation.getAnimatedValue();
            invalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatedLayerFractions[layerIndex] = 1.0f;
                if (onEnd != null) onEnd.run();
            }
        });
        anim.start();
    }

    /**
     * Smooth fill of the target layer with surface ripple waves.
     */
    public void animateFillTarget(final int layerIndex, long duration, final Runnable onEnd) {
        if (layerIndex < 0 || layerIndex >= animatedLayerFractions.length) {
            if (onEnd != null) onEnd.run();
            return;
        }
        animatedLayerFractions[layerIndex] = 0.0f;
        invalidate();

        startReceivingWave(duration);

        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(animation -> {
            animatedLayerFractions[layerIndex] = (float) animation.getAnimatedValue();
            invalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatedLayerFractions[layerIndex] = 1.0f;
                if (onEnd != null) onEnd.run();
            }
        });
        anim.start();
    }

    public void startReceivingWave(long duration) {
        if (waveAnimator != null) waveAnimator.cancel();

        waveAmplitude = 3.5f * density;
        waveAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 4));
        waveAnimator.setDuration(duration);
        waveAnimator.addUpdateListener(animation -> {
            wavePhase = (float) animation.getAnimatedValue();
            // Gradually decay wave amplitude towards the end
            float progress = animation.getAnimatedFraction();
            waveAmplitude = (1.0f - progress) * (3.5f * density);
            invalidate();
        });
        waveAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                waveAmplitude = 0f;
                invalidate();
            }
        });
        waveAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (currentSkin == null) {
            currentSkin = ContainerSkinManager.getInstance(getContext()).getEquippedSkin();
        }
        if (currentSkin != null && w > 0 && h > 0) {
            geometry = currentSkin.createGeometry(w, h, density);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() <= 0 || getHeight() <= 0 || geometry == null) return;

        // 1. Glow aura (when selected, hinted, or shaking)
        if (glowAlphaFraction > 0.01f) {
            containerRenderer.drawGlow(canvas, geometry, glowColor, glowAlphaFraction);
        }

        // 2. Soft bottom shadow
        containerRenderer.drawShadow(canvas, geometry, getWidth(), getHeight());

        // 3. Rear glass backing
        containerRenderer.drawGlassBacking(canvas, geometry);

        // 4. Liquid layers (strictly clipped inside inner cavity)
        liquidRenderer.render(
                canvas,
                geometry,
                layers,
                capacity,
                animatedLayerFractions,
                tiltDegrees,
                wavePhase,
                waveAmplitude
        );

        // 5. Specular sheen reflections
        containerRenderer.drawGlassSheen(canvas, geometry, getWidth(), getHeight());

        // 6. Front glass contour, decorative accents, and rim lip
        containerRenderer.drawGlassFrontAndRim(canvas, geometry);
    }
}
