package com.aquasort.puzzle.views;

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
import android.view.animation.LinearInterpolator;

import com.aquasort.puzzle.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Premium overlay view that draws a realistic, organic liquid pour stream.
 * Employs a dynamic cubic Bézier ribbon with natural physical thickness variation
 * (narrow source neck, thicker fluid body, smooth entry), internal specular highlight,
 * subtle ripple oscillations, and effervescent droplet particles.
 */
public class PourOverlayView extends View {

    private final Paint streamBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint streamCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dropletPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path ribbonPath = new Path();
    private final Path centerLinePath = new Path();

    private boolean isPouring = false;
    private final PointF startPoint = new PointF();
    private final PointF endPoint = new PointF();
    private int currentColorId = 1;
    private ColorUtils.LiquidPalette currentPalette;

    private float streamProgress = 1.0f; // [0..1] for stream eruption and cut-off
    private float wobblePhase = 0f;
    private ValueAnimator wobbleAnimator;

    private final float density;

    // Small droplet particles
    private static class Droplet {
        float x, y, radius, alpha;
    }
    private final List<Droplet> droplets = new ArrayList<>();

    public PourOverlayView(Context context) {
        super(context);
        this.density = getResources().getDisplayMetrics().density;
        init();
    }

    public PourOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = getResources().getDisplayMetrics().density;
        init();
    }

    public PourOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.density = getResources().getDisplayMetrics().density;
        init();
    }

    private void init() {
        streamBodyPaint.setStyle(Paint.Style.FILL);

        streamCorePaint.setStyle(Paint.Style.STROKE);
        streamCorePaint.setStrokeCap(Paint.Cap.ROUND);
        streamCorePaint.setStrokeWidth(2.0f * density);
        streamCorePaint.setColor(Color.parseColor("#99FFFFFF"));

        dropletPaint.setStyle(Paint.Style.FILL);

        wobbleAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI * 4));
        wobbleAnimator.setDuration(800);
        wobbleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        wobbleAnimator.setInterpolator(new LinearInterpolator());
        wobbleAnimator.addUpdateListener(animation -> {
            wobblePhase = (float) animation.getAnimatedValue();
            if (isPouring) invalidate();
        });
    }

    /**
     * Starts the animated stream from source opening to destination opening.
     */
    public void startStream(float startX, float startY, float endX, float endY, int colorId) {
        this.isPouring = true;
        this.streamProgress = 1.0f;
        this.startPoint.set(startX, startY);
        this.endPoint.set(endX, endY);
        this.currentColorId = colorId;
        this.currentPalette = ColorUtils.getLiquidPalette(colorId);

        if (!wobbleAnimator.isRunning()) {
            wobbleAnimator.start();
        }
        invalidate();
    }

    /**
     * Updates source opening coordinates during moving/tilting.
     */
    public void updateStream(float startX, float startY) {
        if (isPouring) {
            this.startPoint.set(startX, startY);
            invalidate();
        }
    }

    /**
     * Updates destination opening coordinates if needed.
     */
    public void updateDestination(float endX, float endY) {
        if (isPouring) {
            this.endPoint.set(endX, endY);
            invalidate();
        }
    }

    /**
     * Stops and hides the stream immediately.
     */
    public void stopStream() {
        this.isPouring = false;
        if (wobbleAnimator.isRunning()) {
            wobbleAnimator.cancel();
        }
        ribbonPath.reset();
        centerLinePath.reset();
        droplets.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isPouring || currentPalette == null) return;

        float sx = startPoint.x;
        float sy = startPoint.y;
        float ex = endPoint.x;
        float ey = endPoint.y;

        // Natural gravity curve control points
        // Direction of pour
        float dx = ex - sx;
        float dy = ey - sy;

        // Control point 1 shoots slightly outward and arcs down
        float c1x = sx + (dx * 0.25f);
        float c1y = sy - (10f * density);

        // Control point 2 descends vertically into destination
        float c2x = ex - (dx * 0.15f);
        float c2y = ey - Math.max(30f * density, Math.abs(dy) * 0.4f);

        // Subdivide cubic curve to construct variable-thickness ribbon
        int steps = 20;
        PointF[] curvePoints = new PointF[steps + 1];
        PointF[] leftSide = new PointF[steps + 1];
        PointF[] rightSide = new PointF[steps + 1];

        centerLinePath.reset();

        for (int i = 0; i <= steps; i++) {
            float t = (float) i / steps;

            // Cubic Bézier calculation
            float u = 1f - t;
            float tt = t * t;
            float uu = u * u;
            float uuu = uu * u;
            float ttt = tt * t;

            float x = uuu * sx + 3f * uu * t * c1x + 3f * u * tt * c2x + ttt * ex;
            float y = uuu * sy + 3f * uu * t * c1y + 3f * u * tt * c2y + ttt * ey;
            curvePoints[i] = new PointF(x, y);

            if (i == 0) {
                centerLinePath.moveTo(x, y);
            } else {
                centerLinePath.lineTo(x, y);
            }

            // Derivative / Tangent vector for normal calculation
            float tx = 3f * uu * (c1x - sx) + 6f * u * t * (c2x - c1x) + 3f * tt * (ex - c2x);
            float ty = 3f * uu * (c1y - sy) + 6f * u * t * (c2y - c1y) + 3f * tt * (ey - c2y);
            float len = (float) Math.hypot(tx, ty);
            if (len < 0.001f) len = 1f;

            // Unit normal (-ty, tx)
            float nx = -ty / len;
            float ny = tx / len;

            // Organic thickness profile: Narrow at lip, swelling gracefully in middle, tapering slightly at entry
            // + subtle sine wave oscillation along stream
            float baseW = (float) Math.sin(t * Math.PI) * (4.5f * density) + (3.5f * density);
            float oscillation = (float) Math.sin(wobblePhase + (t * 8f)) * (0.6f * density);
            float halfWidth = (baseW + oscillation) / 2f;

            leftSide[i] = new PointF(x + nx * halfWidth, y + ny * halfWidth);
            rightSide[i] = new PointF(x - nx * halfWidth, y - ny * halfWidth);
        }

        // Build continuous closed ribbon contour
        ribbonPath.reset();
        ribbonPath.moveTo(leftSide[0].x, leftSide[0].y);
        for (int i = 1; i <= steps; i++) {
            ribbonPath.lineTo(leftSide[i].x, leftSide[i].y);
        }
        for (int i = steps; i >= 0; i--) {
            ribbonPath.lineTo(rightSide[i].x, rightSide[i].y);
        }
        ribbonPath.close();

        // Fluid linear gradient from light luminous source to destination
        LinearGradient streamGrad = new LinearGradient(
                sx, sy, ex, ey,
                currentPalette.lightColor, currentPalette.darkColor,
                Shader.TileMode.CLAMP
        );
        streamBodyPaint.setShader(streamGrad);
        canvas.drawPath(ribbonPath, streamBodyPaint);

        // Core specular highlight line
        canvas.drawPath(centerLinePath, streamCorePaint);

        // Dynamic effervescent droplet beads falling along the flow
        dropletPaint.setColor(currentPalette.meniscusColor);
        for (int i = 3; i < steps - 2; i += 5) {
            float dropT = ((i / (float) steps) + (wobblePhase * 0.1f)) % 1.0f;
            int idx = Math.min(steps, Math.max(0, (int) (dropT * steps)));
            PointF p = curvePoints[idx];
            canvas.drawCircle(p.x, p.y, 1.8f * density, dropletPaint);
        }
    }
}
