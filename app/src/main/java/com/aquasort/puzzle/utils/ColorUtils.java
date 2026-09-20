package com.aquasort.puzzle.utils;

import android.graphics.Color;

/**
 * Provides color mappings, gradients, and specular highlights for realistic liquid rendering.
 */
public final class ColorUtils {
    private ColorUtils() {}

    public static class LiquidPalette {
        public final int baseColor;
        public final int darkColor;
        public final int lightColor;
        public final int meniscusColor;

        public LiquidPalette(int baseColor, int darkColor, int lightColor, int meniscusColor) {
            this.baseColor = baseColor;
            this.darkColor = darkColor;
            this.lightColor = lightColor;
            this.meniscusColor = meniscusColor;
        }
    }

    public static LiquidPalette getLiquidPalette(int colorId) {
        switch (colorId) {
            case Constants.COLOR_RED:
                return new LiquidPalette(
                        Color.parseColor("#FF2A55"),
                        Color.parseColor("#C9002B"),
                        Color.parseColor("#FF6B8B"),
                        Color.parseColor("#FF8FA3")
                );
            case Constants.COLOR_ORANGE:
                return new LiquidPalette(
                        Color.parseColor("#FF8500"),
                        Color.parseColor("#C25400"),
                        Color.parseColor("#FFA726"),
                        Color.parseColor("#FFBE76")
                );
            case Constants.COLOR_YELLOW:
                return new LiquidPalette(
                        Color.parseColor("#FFD000"),
                        Color.parseColor("#C99700"),
                        Color.parseColor("#FFE066"),
                        Color.parseColor("#FFF0A6")
                );
            case Constants.COLOR_GREEN:
                return new LiquidPalette(
                        Color.parseColor("#10B981"),
                        Color.parseColor("#047857"),
                        Color.parseColor("#34D399"),
                        Color.parseColor("#6EE7B7")
                );
            case Constants.COLOR_CYAN:
                return new LiquidPalette(
                        Color.parseColor("#06B6D4"),
                        Color.parseColor("#0E7490"),
                        Color.parseColor("#67E8F9"),
                        Color.parseColor("#A5F3FC")
                );
            case Constants.COLOR_BLUE:
                return new LiquidPalette(
                        Color.parseColor("#3B82F6"),
                        Color.parseColor("#1D4ED8"),
                        Color.parseColor("#60A5FA"),
                        Color.parseColor("#93C5FD")
                );
            case Constants.COLOR_PURPLE:
                return new LiquidPalette(
                        Color.parseColor("#8B5CF6"),
                        Color.parseColor("#5B21B6"),
                        Color.parseColor("#A78BFA"),
                        Color.parseColor("#C4B5FD")
                );
            case Constants.COLOR_PINK:
                return new LiquidPalette(
                        Color.parseColor("#EC4899"),
                        Color.parseColor("#9D174D"),
                        Color.parseColor("#F472B6"),
                        Color.parseColor("#FBCFE8")
                );
            case Constants.COLOR_LIME:
                return new LiquidPalette(
                        Color.parseColor("#84CC16"),
                        Color.parseColor("#4D7C0F"),
                        Color.parseColor("#A3E635"),
                        Color.parseColor("#BEF264")
                );
            case Constants.COLOR_BROWN:
                return new LiquidPalette(
                        Color.parseColor("#B45309"),
                        Color.parseColor("#78350F"),
                        Color.parseColor("#D97706"),
                        Color.parseColor("#FBBF24")
                );
            case Constants.COLOR_GREY:
                return new LiquidPalette(
                        Color.parseColor("#94A3B8"),
                        Color.parseColor("#475569"),
                        Color.parseColor("#CBD5E1"),
                        Color.parseColor("#E2E8F0")
                );
            case Constants.COLOR_INDIGO:
                return new LiquidPalette(
                        Color.parseColor("#6366F1"),
                        Color.parseColor("#3730A3"),
                        Color.parseColor("#818CF8"),
                        Color.parseColor("#A5B4FC")
                );
            default:
                return new LiquidPalette(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                );
        }
    }
}
