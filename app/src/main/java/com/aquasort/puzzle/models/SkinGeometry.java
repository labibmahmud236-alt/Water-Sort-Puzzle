package com.aquasort.puzzle.models;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Geometric definition for a container silhouette at a specific width and height.
 * Provides paths for outer glass, inner liquid cavity clipping, rim, decorative elements,
 * liquid bounding box, and physical pour pivots.
 */
public class SkinGeometry {

    private final Path outerPath;
    private final Path innerLiquidPath;
    private final Path rimPath;
    private final Path decorPath;
    private final RectF innerRect;
    private final PointF openingPointLeft;
    private final PointF openingPointRight;
    private final PointF pourPivotLeft;
    private final PointF pourPivotRight;
    private final float tiltAngle;

    public SkinGeometry(
            Path outerPath,
            Path innerLiquidPath,
            Path rimPath,
            Path decorPath,
            RectF innerRect,
            PointF openingPointLeft,
            PointF openingPointRight,
            PointF pourPivotLeft,
            PointF pourPivotRight,
            float tiltAngle
    ) {
        this.outerPath = outerPath != null ? outerPath : new Path();
        this.innerLiquidPath = innerLiquidPath != null ? innerLiquidPath : new Path();
        this.rimPath = rimPath != null ? rimPath : new Path();
        this.decorPath = decorPath != null ? decorPath : new Path();
        this.innerRect = innerRect != null ? innerRect : new RectF();
        this.openingPointLeft = openingPointLeft != null ? openingPointLeft : new PointF();
        this.openingPointRight = openingPointRight != null ? openingPointRight : new PointF();
        this.pourPivotLeft = pourPivotLeft != null ? pourPivotLeft : new PointF();
        this.pourPivotRight = pourPivotRight != null ? pourPivotRight : new PointF();
        this.tiltAngle = tiltAngle;
    }

    public Path getOuterPath() {
        return outerPath;
    }

    public Path getInnerLiquidPath() {
        return innerLiquidPath;
    }

    public Path getRimPath() {
        return rimPath;
    }

    public Path getDecorPath() {
        return decorPath;
    }

    public RectF getInnerRect() {
        return innerRect;
    }

    public PointF getOpeningPointLeft() {
        return openingPointLeft;
    }

    public PointF getOpeningPointRight() {
        return openingPointRight;
    }

    public PointF getPourPivotLeft() {
        return pourPivotLeft;
    }

    public PointF getPourPivotRight() {
        return pourPivotRight;
    }

    public float getTiltAngle() {
        return tiltAngle;
    }
}
