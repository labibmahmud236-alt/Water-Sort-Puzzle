package com.aquasort.puzzle.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;

import com.aquasort.puzzle.models.SkinGeometry;
import com.aquasort.puzzle.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Dedicated renderer for layered liquid graphics.
 * Features rich linear gradients, curved meniscuses, specular reflections,
 * effervescent micro-bubbles, dynamic receiving wave ripples,
 * and realistic surface tilt reaction during pouring.
 */
public class LiquidRenderer {

    private final Paint liquidPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint meniscusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path surfaceWavePath = new Path();
    private final Path layerPolygonPath = new Path();
    private final List<PointF> bubbles = new ArrayList<>();

    private final float density;

    public LiquidRenderer(Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
        initPaints();
    }

    public LiquidRenderer(float density) {
        this.density = density;
        initPaints();
    }

    private void initPaints() {
        liquidPaint.setStyle(Paint.Style.FILL);

        meniscusPaint.setStyle(Paint.Style.FILL);

        shinePaint.setStyle(Paint.Style.STROKE);
        shinePaint.setStrokeCap(Paint.Cap.ROUND);
        shinePaint.setStrokeWidth(1.2f * density);
        shinePaint.setColor(Color.parseColor("#99FFFFFF"));

        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setColor(Color.parseColor("#55FFFFFF"));

        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#26000000"));

        // Effervescent sparkles
        bubbles.add(new PointF(0.32f, 0.30f));
        bubbles.add(new PointF(0.68f, 0.60f));
        bubbles.add(new PointF(0.42f, 0.80f));
        bubbles.add(new PointF(0.75f, 0.25f));
    }

    /**
     * Renders all liquid layers inside the container clipped strictly to the inner path.
     *
     * @param canvas Canvas to draw onto
     * @param geom Container geometry holding innerLiquidPath and innerRect
     * @param layers Color IDs of liquid layers from bottom to top
     * @param capacity Maximum container capacity
     * @param layerFractions Normalized fill/drain fraction [0..1] for each layer slot
     * @param tiltDegrees Current container tilt angle in degrees (for surface deformation)
     * @param wavePhase Phase of receiving wave ripple [0..2*PI]
     * @param waveAmplitude Amplitude of receiving wave in pixels
     */
    public void render(
            Canvas canvas,
            SkinGeometry geom,
            List<Integer> layers,
            int capacity,
            float[] layerFractions,
            float tiltDegrees,
            float wavePhase,
            float waveAmplitude
    ) {
        if (geom == null || layers == null || layers.isEmpty()) return;

        RectF inner = geom.getInnerRect();
        float totalInnerH = inner.height();
        if (totalInnerH <= 0 || inner.width() <= 0) return;

        float layerHeight = totalInnerH / Math.max(1, capacity);

        canvas.save();
        // Crucial: Clip all liquid strictly to the container cavity silhouette
        canvas.clipPath(geom.getInnerLiquidPath());

        for (int i = 0; i < layers.size(); i++) {
            int colorId = layers.get(i);
            if (colorId <= 0) continue;

            ColorUtils.LiquidPalette pal = ColorUtils.getLiquidPalette(colorId);

            float bottomY = inner.bottom - (i * layerHeight);
            float fraction = (layerFractions != null && i < layerFractions.length) ? layerFractions[i] : 1.0f;
            if (fraction <= 0.001f) continue;

            float curH = layerHeight * fraction;
            float topY = bottomY - curH;

            // Vertical gradient: Light luminous shade at top, rich deep tone at base
            LinearGradient grad = new LinearGradient(
                    inner.left, topY,
                    inner.left, bottomY,
                    pal.lightColor, pal.darkColor,
                    Shader.TileMode.CLAMP
            );
            liquidPaint.setShader(grad);

            boolean isTopLayer = (i == layers.size() - 1);

            if (Math.abs(tiltDegrees) > 2f && isTopLayer) {
                // Physical tilt response: Liquid surface levels against gravity inside the container
                // Tilting clockwise (positive tilt) shifts surface up on the left and down on the right
                float tiltFactor = (float) Math.tan(Math.toRadians(tiltDegrees * 0.65f));
                float dy = (inner.width() * 0.5f) * tiltFactor;

                layerPolygonPath.reset();
                layerPolygonPath.moveTo(inner.left - (10f * density), bottomY + (4f * density));
                layerPolygonPath.lineTo(inner.right + (10f * density), bottomY + (4f * density));
                layerPolygonPath.lineTo(inner.right + (10f * density), topY + dy);
                layerPolygonPath.lineTo(inner.left - (10f * density), topY - dy);
                layerPolygonPath.close();

                canvas.drawPath(layerPolygonPath, liquidPaint);

                // Tilted surface meniscus highlight line
                meniscusPaint.setShader(null);
                meniscusPaint.setColor(pal.meniscusColor);
                meniscusPaint.setAlpha(180);
                canvas.drawLine(inner.left, topY - dy, inner.right, topY + dy, shinePaint);
            } else if (isTopLayer && waveAmplitude > 0.5f) {
                // Destination liquid receiving wave ripple
                surfaceWavePath.reset();
                surfaceWavePath.moveTo(inner.left - (5f * density), bottomY + (4f * density));
                surfaceWavePath.lineTo(inner.right + (5f * density), bottomY + (4f * density));
                surfaceWavePath.lineTo(inner.right + (5f * density), topY);

                int steps = 12;
                float stepW = (inner.width() + 10f * density) / steps;
                for (int s = steps; s >= 0; s--) {
                    float x = inner.left - (5f * density) + (s * stepW);
                    float y = topY + (float) Math.sin(wavePhase + (s * 0.6f)) * waveAmplitude;
                    surfaceWavePath.lineTo(x, y);
                }
                surfaceWavePath.close();
                canvas.drawPath(surfaceWavePath, liquidPaint);

                // Wave crest shine
                canvas.drawPath(surfaceWavePath, shinePaint);
            } else {
                // Standard resting layer
                RectF layerRect = new RectF(inner.left - 2, topY, inner.right + 2, bottomY + 2);
                canvas.drawRect(layerRect, liquidPaint);

                // Top surface meniscus
                if (isTopLayer && curH > 3f * density) {
                    float meniscusH = 3.5f * density;
                    RectF meniscusRect = new RectF(inner.left, topY - meniscusH, inner.right, topY + meniscusH);

                    meniscusPaint.setShader(null);
                    meniscusPaint.setColor(pal.meniscusColor);
                    meniscusPaint.setAlpha(180);
                    canvas.drawOval(meniscusRect, meniscusPaint);

                    // Specular shine curve on top of meniscus
                    canvas.drawArc(meniscusRect, 15, 150, false, shinePaint);
                }
            }

            // Layer boundary shadow (creates depth between distinct liquid colors)
            if (i > 0) {
                RectF boundaryShadow = new RectF(inner.left, bottomY - (1.5f * density), inner.right, bottomY + (1.5f * density));
                canvas.drawRect(boundaryShadow, shadowPaint);
            }

            // Effervescent micro-bubbles
            if (curH > 8f * density) {
                for (PointF b : bubbles) {
                    float bx = inner.left + (inner.width() * b.x);
                    float by = topY + (curH * b.y);
                    if (by > topY + (3f * density) && by < bottomY - (2f * density)) {
                        canvas.drawCircle(bx, by, 1.4f * density, bubblePaint);
                    }
                }
            }
        }

        canvas.restore();
    }
}
