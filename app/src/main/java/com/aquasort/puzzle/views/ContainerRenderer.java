package com.aquasort.puzzle.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

import com.aquasort.puzzle.models.SkinGeometry;

/**
 * Dedicated Canvas renderer for container glass silhouettes.
 * Renders multi-layer glass effects: soft shadow, rear glass, front glass sheens,
 * specular reflection strips, rim highlights, and dynamic selection glows.
 */
public class ContainerRenderer {

    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glassBackingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glassStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rimStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rimFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sheenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint decorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float density;

    public ContainerRenderer(Context context) {
        this.density = context.getResources().getDisplayMetrics().density;
        initPaints();
    }

    public ContainerRenderer(float density) {
        this.density = density;
        initPaints();
    }

    private void initPaints() {
        // Shadow
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#33000000"));

        // Rear glass interior fill
        glassBackingPaint.setStyle(Paint.Style.FILL);
        glassBackingPaint.setColor(Color.parseColor("#18FFFFFF"));

        // Outer glass contour stroke
        glassStrokePaint.setStyle(Paint.Style.STROKE);
        glassStrokePaint.setStrokeWidth(2.5f * density);
        glassStrokePaint.setColor(Color.parseColor("#70FFFFFF"));

        // Rim stroke and fill
        rimStrokePaint.setStyle(Paint.Style.STROKE);
        rimStrokePaint.setStrokeWidth(2.0f * density);
        rimStrokePaint.setColor(Color.parseColor("#90FFFFFF"));

        rimFillPaint.setStyle(Paint.Style.FILL);
        rimFillPaint.setColor(Color.parseColor("#38FFFFFF"));

        // Specular sheen
        sheenPaint.setStyle(Paint.Style.FILL);

        // Decor lines (handles, measurement marks, ribs)
        decorPaint.setStyle(Paint.Style.STROKE);
        decorPaint.setStrokeWidth(1.8f * density);
        decorPaint.setColor(Color.parseColor("#60FFFFFF"));

        // Glow aura
        glowPaint.setStyle(Paint.Style.STROKE);
        glowPaint.setStrokeWidth(4.5f * density);
        glowPaint.setStrokeJoin(Paint.Join.ROUND);
        glowPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * Draws selection / hint / invalid move glow aura around outer container.
     */
    public void drawGlow(Canvas canvas, SkinGeometry geom, int glowColor, float glowAlpha) {
        if (geom == null || glowAlpha <= 0.01f) return;
        glowPaint.setColor(glowColor);
        glowPaint.setAlpha((int) (255 * glowAlpha * 0.75f));
        canvas.drawPath(geom.getOuterPath(), glowPaint);
        canvas.drawPath(geom.getRimPath(), glowPaint);
    }

    /**
     * Draws soft bottom shadow under the container base.
     */
    public void drawShadow(Canvas canvas, SkinGeometry geom, float width, float height) {
        if (geom == null) return;
        RectF inner = geom.getInnerRect();
        float shadowY = Math.min(height - (4f * density), inner.bottom + (3f * density));
        float shadowL = inner.left + (4f * density);
        float shadowR = inner.right - (4f * density);
        RectF shadowOval = new RectF(shadowL, shadowY, shadowR, shadowY + (6f * density));
        canvas.drawOval(shadowOval, shadowPaint);
    }

    /**
     * Draws translucent rear glass backing inside the inner cavity.
     */
    public void drawGlassBacking(Canvas canvas, SkinGeometry geom) {
        if (geom == null) return;
        canvas.drawPath(geom.getInnerLiquidPath(), glassBackingPaint);
    }

    /**
     * Draws glass specular reflections and curved vertical sheen lines.
     */
    public void drawGlassSheen(Canvas canvas, SkinGeometry geom, float width, float height) {
        if (geom == null) return;
        RectF inner = geom.getInnerRect();
        float innerW = inner.width();
        float innerH = inner.height();
        if (innerW <= 0 || innerH <= 0) return;

        // Primary reflection strip on left edge
        float sheenL = inner.left + (2.5f * density);
        float sheenW = 3.5f * density;
        float sheenTop = inner.top + (4f * density);
        float sheenBot = inner.bottom - (8f * density);

        if (sheenBot > sheenTop) {
            RectF sheenRect = new RectF(sheenL, sheenTop, sheenL + sheenW, sheenBot);
            sheenPaint.setColor(Color.parseColor("#4DFFFFFF"));
            canvas.drawRoundRect(sheenRect, 2f * density, 2f * density, sheenPaint);
        }

        // Secondary subtle specular line on right edge
        float rightL = inner.right - (4f * density);
        float rightW = 1.8f * density;
        float rightTop = inner.top + (14f * density);
        float rightBot = inner.bottom - (14f * density);

        if (rightBot > rightTop) {
            RectF rightRect = new RectF(rightL, rightTop, rightL + rightW, rightBot);
            sheenPaint.setColor(Color.parseColor("#28FFFFFF"));
            canvas.drawRoundRect(rightRect, 1f * density, 1f * density, sheenPaint);
        }
    }

    /**
     * Draws front glass silhouette stroke, rim highlight, and decorative accents.
     */
    public void drawGlassFrontAndRim(Canvas canvas, SkinGeometry geom) {
        if (geom == null) return;

        // Outer glass contour
        canvas.drawPath(geom.getOuterPath(), glassStrokePaint);

        // Decor elements (mug handle, measurement tick marks, crystal cuts, etc.)
        Path decor = geom.getDecorPath();
        if (decor != null && !decor.isEmpty()) {
            canvas.drawPath(decor, decorPaint);
        }

        // Top rim lip fill & stroke
        canvas.drawPath(geom.getRimPath(), rimFillPaint);
        canvas.drawPath(geom.getRimPath(), rimStrokePaint);
    }
}
