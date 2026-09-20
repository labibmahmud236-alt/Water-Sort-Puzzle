package com.aquasort.puzzle.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.models.SkinGeometry;
import com.aquasort.puzzle.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interactive preview view for the Shop and preview dialogs.
 * Renders the chosen container skin in high resolution with sample colored liquid,
 * and demonstrates a physical demo pour tilt animation with a miniature liquid stream.
 */
public class SkinPreviewView extends View {

    private ContainerSkin skin;
    private SkinGeometry geometry;
    private ContainerRenderer containerRenderer;
    private LiquidRenderer liquidRenderer;

    private final List<Integer> sampleLayers = new ArrayList<>(Arrays.asList(6, 4, 3, 1)); // Blue, Green, Yellow, Red
    private final float[] layerFractions = new float[]{1f, 1f, 1f, 1f};

    private float tiltDegrees = 0f;
    private float streamAlpha = 0f;
    private final Path miniStreamPath = new Path();
    private final Paint miniStreamPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint streamCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean isAnimatingPreview = false;
    private float density;

    public SkinPreviewView(Context context) {
        super(context);
        init(context);
    }

    public SkinPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SkinPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        density = getResources().getDisplayMetrics().density;
        containerRenderer = new ContainerRenderer(density);
        liquidRenderer = new LiquidRenderer(density);

        miniStreamPaint.setStyle(Paint.Style.STROKE);
        miniStreamPaint.setStrokeCap(Paint.Cap.ROUND);
        miniStreamPaint.setStrokeWidth(5f * density);

        streamCorePaint.setStyle(Paint.Style.STROKE);
        streamCorePaint.setStrokeCap(Paint.Cap.ROUND);
        streamCorePaint.setStrokeWidth(2f * density);
        streamCorePaint.setColor(Color.parseColor("#99FFFFFF"));

        setOnClickListener(v -> playDemoTiltAnimation());
    }

    public void setSkin(ContainerSkin skin) {
        this.skin = skin;
        if (getWidth() > 0 && getHeight() > 0 && skin != null) {
            this.geometry = skin.createGeometry(getWidth(), getHeight(), density);
        }
        invalidate();
    }

    public ContainerSkin getSkin() {
        return skin;
    }

    /**
     * Executes an interactive demonstration tilt and mini liquid stream.
     */
    public void playDemoTiltAnimation() {
        if (isAnimatingPreview || skin == null || geometry == null) return;
        isAnimatingPreview = true;

        float targetAngle = geometry.getTiltAngle() * 0.70f;
        PointF pivot = geometry.getPourPivotRight();
        setPivotX(pivot.x);
        setPivotY(pivot.y);

        // Stage 1: Tilt right
        ValueAnimator tiltAnim = ValueAnimator.ofFloat(0f, targetAngle);
        tiltAnim.setDuration(400);
        tiltAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        tiltAnim.addUpdateListener(animation -> {
            tiltDegrees = (float) animation.getAnimatedValue();
            setRotation(tiltDegrees);
            invalidate();
        });

        // Mini stream fade in
        ValueAnimator streamAnim = ValueAnimator.ofFloat(0f, 1f);
        streamAnim.setDuration(250);
        streamAnim.addUpdateListener(anim -> {
            streamAlpha = (float) anim.getAnimatedValue();
            invalidate();
        });

        AnimatorSet forwardSet = new AnimatorSet();
        forwardSet.playSequentially(tiltAnim, streamAnim);

        forwardSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(() -> {
                    // Stage 2: Return and settle
                    ValueAnimator streamOut = ValueAnimator.ofFloat(1f, 0f);
                    streamOut.setDuration(200);
                    streamOut.addUpdateListener(anim -> {
                        streamAlpha = (float) anim.getAnimatedValue();
                        invalidate();
                    });

                    ValueAnimator returnAnim = ValueAnimator.ofFloat(targetAngle, 0f);
                    returnAnim.setDuration(350);
                    returnAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                    returnAnim.addUpdateListener(anim -> {
                        tiltDegrees = (float) anim.getAnimatedValue();
                        setRotation(tiltDegrees);
                        invalidate();
                    });

                    AnimatorSet backSet = new AnimatorSet();
                    backSet.playSequentially(streamOut, returnAnim);
                    backSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setRotation(0f);
                            tiltDegrees = 0f;
                            streamAlpha = 0f;
                            setPivotX(getWidth() / 2f);
                            setPivotY(getHeight() / 2f);
                            isAnimatingPreview = false;
                            invalidate();
                        }
                    });
                    backSet.start();
                }, 400);
            }
        });

        forwardSet.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (skin != null && w > 0 && h > 0) {
            geometry = skin.createGeometry(w, h, density);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() <= 0 || getHeight() <= 0 || geometry == null) return;

        // Shadow
        containerRenderer.drawShadow(canvas, geometry, getWidth(), getHeight());

        // Rear glass backing
        containerRenderer.drawGlassBacking(canvas, geometry);

        // Liquids with tilt response
        liquidRenderer.render(
                canvas,
                geometry,
                sampleLayers,
                sampleLayers.size(),
                layerFractions,
                tiltDegrees,
                0f,
                0f
        );

        // Glass sheens
        containerRenderer.drawGlassSheen(canvas, geometry, getWidth(), getHeight());

        // Front glass contour and rim
        containerRenderer.drawGlassFrontAndRim(canvas, geometry);

        // Mini stream preview when tilted
        if (streamAlpha > 0.01f) {
            PointF open = geometry.getOpeningPointRight();
            miniStreamPath.reset();
            miniStreamPath.moveTo(open.x, open.y);
            miniStreamPath.quadTo(open.x + (16f * density), open.y + (20f * density), open.x + (22f * density), open.y + (48f * density));

            ColorUtils.LiquidPalette pal = ColorUtils.getLiquidPalette(sampleLayers.get(sampleLayers.size() - 1));
            miniStreamPaint.setColor(pal.baseColor);
            miniStreamPaint.setAlpha((int) (255 * streamAlpha));
            canvas.drawPath(miniStreamPath, miniStreamPaint);

            streamCorePaint.setAlpha((int) (200 * streamAlpha));
            canvas.drawPath(miniStreamPath, streamCorePaint);
        }
    }
}
