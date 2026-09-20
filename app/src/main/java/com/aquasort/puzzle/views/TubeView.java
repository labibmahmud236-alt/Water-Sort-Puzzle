package com.aquasort.puzzle.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.aquasort.puzzle.game.Tube;
import com.aquasort.puzzle.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Premium glass test tube view with realistic liquid shaders, curved meniscuses,
 * glass reflection highlights, selection glows, shake animations, and tilt pours.
 */
public class TubeView extends View {

    private int capacity = Tube.DEFAULT_CAPACITY;
    private final List<Integer> layers = new ArrayList<>();
    private final float[] animatedLayerFractions = new float[Tube.DEFAULT_CAPACITY];

    // Paints
    private final Paint glassFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glassStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glassHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint liquidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint meniscusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Paths and Rects
    private final Path tubeOuterPath = new Path();
    private final Path tubeInnerPath = new Path();
    private final Path rimPath = new Path();
    private final RectF tubeBodyRect = new RectF();
    private final RectF tubeInnerRect = new RectF();

    // State
    private boolean isSelected = false;
    private boolean isHinted = false;
    private boolean isInvalidShaking = false;
    private float glowAlphaFraction = 0f;
    private int glowColor = Color.parseColor("#8800E5FF");

    // Dimensions
    private float wallThickness;
    private float rimExtraWidth;
    private float rimHeight;
    private float cornerRadius;

    // Bubbles
    private final List<PointF> bubbles = new ArrayList<>();

    public TubeView(Context context) {
        super(context);
        init();
    }

    public TubeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TubeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        for (int i = 0; i < Tube.DEFAULT_CAPACITY; i++) {
            animatedLayerFractions[i] = 1.0f;
        }

        // Glass Paints
        glassFillPaint.setStyle(Paint.Style.FILL);
        glassFillPaint.setColor(Color.parseColor("#1FFFFFFF"));

        glassStrokePaint.setStyle(Paint.Style.STROKE);
        glassStrokePaint.setStrokeWidth(dpToPx(2.5f));
        glassStrokePaint.setColor(Color.parseColor("#66FFFFFF"));

        glassHighlightPaint.setStyle(Paint.Style.FILL);
        glassHighlightPaint.setColor(Color.parseColor("#4DFFFFFF"));

        meniscusPaint.setStyle(Paint.Style.FILL);

        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(dpToPx(5f));
        glowPaint.setColor(Color.parseColor("#AA00E5FF"));

        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setColor(Color.parseColor("#66FFFFFF"));

        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#33000000"));

        // Fixed bubble offsets for visual sparkle
        bubbles.add(new PointF(0.35f, 0.25f));
        bubbles.add(new PointF(0.65f, 0.55f));
        bubbles.add(new PointF(0.40f, 0.75f));
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        invalidate();
    }

    public void setTubeData(Tube tube) {
        layers.clear();
        if (tube != null) {
            this.capacity = tube.getCapacity();
            layers.addAll(tube.getLayers());
        }
        for (int i = 0; i < Tube.DEFAULT_CAPACITY; i++) {
            animatedLayerFractions[i] = 1.0f;
        }
        invalidate();
    }

    public List<Integer> getLayers() {
        return layers;
    }

    public void setSelectedState(boolean selected) {
        if (this.isSelected == selected) return;
        this.isSelected = selected;

        float targetY = selected ? -dpToPx(24f) : 0f;
        float targetScale = selected ? 1.05f : 1.0f;
        glowColor = Color.parseColor("#CC00E5FF");

        animate()
                .translationY(targetY)
                .scaleX(targetScale)
                .scaleY(targetScale)
                .setDuration(240)
                .setInterpolator(selected ? new OvershootInterpolator(1.4f) : new DecelerateInterpolator())
                .start();

        ValueAnimator glowAnim = ValueAnimator.ofFloat(glowAlphaFraction, selected ? 1.0f : 0f);
        glowAnim.setDuration(200);
        glowAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                glowAlphaFraction = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        glowAnim.start();
    }

    public void playInvalidShake() {
        isInvalidShaking = true;
        glowColor = Color.parseColor("#E6FF3366");
        glowAlphaFraction = 1.0f;
        invalidate();

        ValueAnimator shake = ValueAnimator.ofFloat(0, -dpToPx(12f), dpToPx(12f), -dpToPx(8f), dpToPx(8f), -dpToPx(4f), 0);
        shake.setDuration(360);
        shake.setInterpolator(new AccelerateDecelerateInterpolator());
        shake.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setTranslationX((float) animation.getAnimatedValue());
            }
        });
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
                    .setDuration(300)
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

    /**
     * Animates smooth pouring reduction of top layer.
     */
    public void animateDrainTop(final int layerIndex, long duration, final Runnable onEnd) {
        if (layerIndex < 0 || layerIndex >= animatedLayerFractions.length) {
            if (onEnd != null) onEnd.run();
            return;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedLayerFractions[layerIndex] = (float) animation.getAnimatedValue();
                invalidate();
            }
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
     * Animates smooth fill of target layer.
     */
    public void animateFillTarget(final int layerIndex, long duration, final Runnable onEnd) {
        if (layerIndex < 0 || layerIndex >= animatedLayerFractions.length) {
            if (onEnd != null) onEnd.run();
            return;
        }
        animatedLayerFractions[layerIndex] = 0.0f;
        invalidate();

        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedLayerFractions[layerIndex] = (float) animation.getAnimatedValue();
                invalidate();
            }
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildPaths(w, h);
    }

    private void buildPaths(int w, int h) {
        float padding = dpToPx(8f);
        rimExtraWidth = dpToPx(4f);
        rimHeight = dpToPx(8f);
        wallThickness = dpToPx(3.5f);

        float outerLeft = padding + rimExtraWidth;
        float outerRight = w - padding - rimExtraWidth;
        float outerTop = padding + rimHeight;
        float outerBottom = h - padding;
        float tubeWidth = outerRight - outerLeft;
        cornerRadius = tubeWidth / 2f;

        tubeBodyRect.set(outerLeft, outerTop, outerRight, outerBottom);

        // Outer glass contour (straight body with rounded semicircular bottom)
        tubeOuterPath.reset();
        tubeOuterPath.moveTo(outerLeft, outerTop);
        tubeOuterPath.lineTo(outerLeft, outerBottom - cornerRadius);
        tubeOuterPath.arcTo(outerLeft, outerBottom - 2 * cornerRadius, outerRight, outerBottom, 180, -180, false);
        tubeOuterPath.lineTo(outerRight, outerTop);
        tubeOuterPath.close();

        // Rim lip
        rimPath.reset();
        RectF rimRect = new RectF(outerLeft - rimExtraWidth, padding, outerRight + rimExtraWidth, outerTop + rimHeight * 0.3f);
        rimPath.addRoundRect(rimRect, dpToPx(4f), dpToPx(4f), Path.Direction.CW);

        // Inner liquid cavity
        float innerLeft = outerLeft + wallThickness;
        float innerRight = outerRight - wallThickness;
        float innerTop = outerTop;
        float innerBottom = outerBottom - wallThickness;
        float innerRadius = (innerRight - innerLeft) / 2f;

        tubeInnerRect.set(innerLeft, innerTop, innerRight, innerBottom);

        tubeInnerPath.reset();
        tubeInnerPath.moveTo(innerLeft, innerTop);
        tubeInnerPath.lineTo(innerLeft, innerBottom - innerRadius);
        tubeInnerPath.arcTo(innerLeft, innerBottom - 2 * innerRadius, innerRight, innerBottom, 180, -180, false);
        tubeInnerPath.lineTo(innerRight, innerTop);
        tubeInnerPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() <= 0 || getHeight() <= 0) return;

        // 1. Draw Glow Aura if selected / hinted / invalid
        if (glowAlphaFraction > 0.01f) {
            glowPaint.setColor(glowColor);
            glowPaint.setAlpha((int) (255 * glowAlphaFraction * 0.7f));
            canvas.drawPath(tubeOuterPath, glowPaint);
            canvas.drawPath(rimPath, glowPaint);
        }

        // 2. Draw Soft Bottom Shadow
        float shadowY = tubeBodyRect.bottom + dpToPx(3f);
        RectF shadowOval = new RectF(tubeBodyRect.left + dpToPx(6f), shadowY, tubeBodyRect.right - dpToPx(6f), shadowY + dpToPx(6f));
        canvas.drawOval(shadowOval, shadowPaint);

        // 3. Draw Inner Glass Backing
        canvas.drawPath(tubeInnerPath, glassFillPaint);

        // 4. Draw Liquids inside clipped inner path
        canvas.save();
        canvas.clipPath(tubeInnerPath);
        drawLiquids(canvas);
        canvas.restore();

        // 5. Draw Glass Highlights and Reflection Sheen
        drawGlassReflections(canvas);

        // 6. Draw Tube Outer Rim and Stroke
        glassStrokePaint.setColor(Color.parseColor("#70FFFFFF"));
        canvas.drawPath(tubeOuterPath, glassStrokePaint);
        canvas.drawPath(rimPath, glassStrokePaint);

        // Rim inner glass highlight
        glassHighlightPaint.setColor(Color.parseColor("#4DFFFFFF"));
        canvas.drawPath(rimPath, glassHighlightPaint);
    }

    private void drawLiquids(Canvas canvas) {
        if (layers.isEmpty()) return;

        float totalInnerHeight = tubeInnerRect.height();
        float layerHeight = totalInnerHeight / capacity;

        for (int i = 0; i < layers.size(); i++) {
            int colorId = layers.get(i);
            if (colorId <= 0) continue;

            ColorUtils.LiquidPalette pal = ColorUtils.getLiquidPalette(colorId);

            float bottomY = tubeInnerRect.bottom - (i * layerHeight);
            float fraction = (i < animatedLayerFractions.length) ? animatedLayerFractions[i] : 1.0f;
            float currentLayerHeight = layerHeight * fraction;
            float topY = bottomY - currentLayerHeight;

            // Gradient shader: Lighter at top, deep and rich at bottom
            LinearGradient gradient = new LinearGradient(
                    tubeInnerRect.left, topY,
                    tubeInnerRect.left, bottomY,
                    pal.lightColor, pal.darkColor,
                    Shader.TileMode.CLAMP
            );
            liquidPaint.setShader(gradient);

            // Draw layer rect
            RectF layerRect = new RectF(tubeInnerRect.left - 1, topY, tubeInnerRect.right + 1, bottomY + 2);
            canvas.drawRect(layerRect, liquidPaint);

            // Meniscus on the top liquid surface
            if (i == layers.size() - 1 && currentLayerHeight > dpToPx(4f)) {
                meniscusPaint.setColor(pal.meniscusColor);
                meniscusPaint.setAlpha(180);
                float meniscusRadiusY = dpToPx(3.5f);
                RectF meniscusRect = new RectF(
                        tubeInnerRect.left,
                        topY - meniscusRadiusY,
                        tubeInnerRect.right,
                        topY + meniscusRadiusY
                );
                canvas.drawOval(meniscusRect, meniscusPaint);

                // Meniscus specular shine line
                Paint shinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                shinePaint.setColor(Color.parseColor("#A0FFFFFF"));
                shinePaint.setStyle(Paint.Style.STROKE);
                shinePaint.setStrokeWidth(dpToPx(1.2f));
                canvas.drawArc(meniscusRect, 10, 160, false, shinePaint);
            }

            // Draw tiny effervescent bubbles
            if (currentLayerHeight > dpToPx(8f)) {
                for (PointF b : bubbles) {
                    float bx = tubeInnerRect.left + (tubeInnerRect.width() * b.x);
                    float by = topY + (currentLayerHeight * b.y);
                    if (by > topY && by < bottomY) {
                        canvas.drawCircle(bx, by, dpToPx(1.5f), bubblePaint);
                    }
                }
            }
        }
    }

    private void drawGlassReflections(Canvas canvas) {
        // Vertical specular reflection highlight on the left edge
        float highlightLeft = tubeInnerRect.left + dpToPx(2.5f);
        float highlightWidth = dpToPx(3.5f);
        float highlightTop = tubeInnerRect.top + dpToPx(4f);
        float highlightBottom = tubeInnerRect.bottom - cornerRadius;

        RectF sheenRect = new RectF(highlightLeft, highlightTop, highlightLeft + highlightWidth, highlightBottom);
        glassHighlightPaint.setColor(Color.parseColor("#55FFFFFF"));
        canvas.drawRoundRect(sheenRect, dpToPx(2f), dpToPx(2f), glassHighlightPaint);

        // Thin secondary reflection on the right edge
        float rightSheenLeft = tubeInnerRect.right - dpToPx(4f);
        RectF rightSheenRect = new RectF(rightSheenLeft, highlightTop + dpToPx(12f), rightSheenLeft + dpToPx(1.5f), highlightBottom - dpToPx(8f));
        glassHighlightPaint.setColor(Color.parseColor("#2EFFFFFF"));
        canvas.drawRoundRect(rightSheenRect, dpToPx(1f), dpToPx(1f), glassHighlightPaint);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
