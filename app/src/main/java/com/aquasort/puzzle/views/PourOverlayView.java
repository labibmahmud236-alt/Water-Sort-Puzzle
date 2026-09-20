package com.aquasort.puzzle.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.aquasort.puzzle.utils.ColorUtils;

/**
 * Overlay view that draws the animated flowing liquid stream during a pour.
 */
public class PourOverlayView extends View {

    private final Paint streamPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path streamPath = new Path();
    private boolean isPouring = false;
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();
    private int liquidColor = Color.TRANSPARENT;
    private float streamWidth = 14f;

    public PourOverlayView(Context context) {
        super(context);
        init();
    }

    public PourOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PourOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        streamPaint.setStyle(Paint.Style.STROKE);
        streamPaint.setStrokeCap(Paint.Cap.ROUND);
        streamPaint.setStrokeJoin(Paint.Join.ROUND);
        streamWidth = 5f * getResources().getDisplayMetrics().density;
        streamPaint.setStrokeWidth(streamWidth);
    }

    public void startStream(float startX, float startY, float endX, float endY, int colorId) {
        this.isPouring = true;
        this.startPoint.set(startX, startY);
        this.endPoint.set(endX, endY);
        ColorUtils.LiquidPalette pal = ColorUtils.getLiquidPalette(colorId);
        this.liquidColor = pal.baseColor;
        streamPaint.setColor(this.liquidColor);
        invalidate();
    }

    public void updateStream(float startX, float startY) {
        if (isPouring) {
            this.startPoint.set(startX, startY);
            invalidate();
        }
    }

    public void stopStream() {
        this.isPouring = false;
        streamPath.reset();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isPouring) return;

        streamPath.reset();
        streamPath.moveTo(startPoint.x, startPoint.y);

        // Natural smooth gravity curve
        float controlX = (startPoint.x + endPoint.x) / 2f;
        float controlY = Math.min(startPoint.y, endPoint.y) - (12f * getResources().getDisplayMetrics().density);

        streamPath.quadTo(controlX, controlY, endPoint.x, endPoint.y);
        canvas.drawPath(streamPath, streamPaint);

        // Highlight stream core
        Paint corePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        corePaint.setStyle(Paint.Style.STROKE);
        corePaint.setStrokeCap(Paint.Cap.ROUND);
        corePaint.setStrokeWidth(streamWidth * 0.4f);
        corePaint.setColor(Color.parseColor("#99FFFFFF"));
        canvas.drawPath(streamPath, corePaint);
    }
}
