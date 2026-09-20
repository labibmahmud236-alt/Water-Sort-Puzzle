package com.aquasort.puzzle.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated confetti particle explosion view for level victory celebrations.
 */
public class ConfettiView extends View {

    private static class Particle {
        float x, y;
        float vx, vy;
        float rotation;
        float rotationSpeed;
        float width, height;
        int color;
        float alpha;

        Particle(float startX, float startY, Random rng, int[] colors) {
            this.x = startX;
            this.y = startY;
            float angle = (float) (rng.nextDouble() * 2 * Math.PI);
            float speed = 8f + rng.nextFloat() * 18f;
            this.vx = (float) Math.cos(angle) * speed;
            this.vy = (float) Math.sin(angle) * speed - 12f; // Initial upward burst
            this.rotation = rng.nextFloat() * 360f;
            this.rotationSpeed = (rng.nextFloat() - 0.5f) * 20f;
            this.width = 12f + rng.nextFloat() * 14f;
            this.height = 8f + rng.nextFloat() * 10f;
            this.color = colors[rng.nextInt(colors.length)];
            this.alpha = 1.0f;
        }

        void update() {
            x += vx;
            y += vy;
            vy += 0.6f; // Gravity
            vx *= 0.98f; // Air drag
            rotation += rotationSpeed;
            if (y > 300) {
                alpha = Math.max(0f, alpha - 0.015f);
            }
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private ValueAnimator animator;

    private static final int[] CONFETTI_COLORS = {
            Color.parseColor("#00E5FF"),
            Color.parseColor("#06D6A0"),
            Color.parseColor("#FFD166"),
            Color.parseColor("#F72585"),
            Color.parseColor("#8B5CF6"),
            Color.parseColor("#FF8500"),
            Color.parseColor("#FFFFFF")
    };

    public ConfettiView(Context context) {
        super(context);
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConfettiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startExplosion() {
        particles.clear();
        int count = 90;
        float centerX = getWidth() > 0 ? getWidth() / 2f : 500f;
        float centerY = getHeight() > 0 ? getHeight() * 0.4f : 600f;

        for (int i = 0; i < count; i++) {
            particles.add(new Particle(centerX, centerY, random, CONFETTI_COLORS));
        }

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(2500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (Particle p : particles) {
                    p.update();
                }
                invalidate();
            }
        });
        animator.start();
    }

    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
        particles.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (particles.isEmpty()) return;

        for (Particle p : particles) {
            if (p.alpha <= 0.01f) continue;
            canvas.save();
            canvas.translate(p.x, p.y);
            canvas.rotate(p.rotation);
            paint.setColor(p.color);
            paint.setAlpha((int) (255 * p.alpha));
            canvas.drawRoundRect(new RectF(-p.width / 2, -p.height / 2, p.width / 2, p.height / 2), 3f, 3f, paint);
            canvas.restore();
        }
    }
}
