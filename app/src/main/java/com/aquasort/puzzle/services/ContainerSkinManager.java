package com.aquasort.puzzle.services;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.models.SkinGeometry;
import com.aquasort.puzzle.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the collection of 20 distinct container skins, each with unique mathematical silhouettes,
 * outer glass paths, inner liquid clipping boundaries, rim shapes, and pour pivots.
 * Also handles skin unlocking, persistence, and equipping via StorageManager.
 */
public class ContainerSkinManager {

    private static ContainerSkinManager instance;

    private final Context context;
    private final StorageManager storage;
    private final List<ContainerSkin> skins = new ArrayList<>();
    private final Map<String, ContainerSkin> skinMap = new HashMap<>();

    public static synchronized ContainerSkinManager getInstance(Context context) {
        if (instance == null) {
            instance = new ContainerSkinManager(context.getApplicationContext());
        }
        return instance;
    }

    private ContainerSkinManager(Context context) {
        this.context = context;
        this.storage = StorageManager.getInstance(context);
        registerSkins();
    }

    public List<ContainerSkin> getAllSkins() {
        return Collections.unmodifiableList(skins);
    }

    public List<ContainerSkin> getSkinsByCategory(String category) {
        if (category == null || category.isEmpty() || ContainerSkin.CAT_ALL.equalsIgnoreCase(category)) {
            return getAllSkins();
        }
        List<ContainerSkin> filtered = new ArrayList<>();
        for (ContainerSkin s : skins) {
            if (s.getCategory().equalsIgnoreCase(category)) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    public ContainerSkin getSkin(String id) {
        if (id == null) return getSkin(Constants.DEFAULT_SKIN_ID);
        ContainerSkin skin = skinMap.get(id);
        return skin != null ? skin : skinMap.get(Constants.DEFAULT_SKIN_ID);
    }

    public ContainerSkin getEquippedSkin() {
        String equippedId = storage.getEquippedSkinId();
        return getSkin(equippedId);
    }

    public void setEquippedSkin(String id) {
        if (isSkinUnlocked(id)) {
            storage.setEquippedSkinId(id);
        }
    }

    public boolean isSkinUnlocked(String id) {
        if (Constants.DEFAULT_SKIN_ID.equals(id)) return true;
        return storage.isSkinUnlocked(id);
    }

    public boolean unlockSkin(String id) {
        ContainerSkin skin = getSkin(id);
        if (skin == null) return false;
        storage.unlockSkin(id);
        return true;
    }

    private void addSkin(ContainerSkin skin) {
        skins.add(skin);
        skinMap.put(skin.getId(), skin);
    }

    // ==================== SKIN REGISTRY (20 DISTINCT SILHOUETTES) ====================

    private void registerSkins() {
        // 1. Classic Glass Tube (Free / Default)
        addSkin(new ContainerSkin(
                "classic_tube",
                "Classic Tube",
                0,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_COMMON,
                "Standard laboratory glass test tube with smooth rounded bottom.",
                (w, h, d) -> buildClassicTube(w, h, d)
        ));

        // 2. Slim Glass
        addSkin(new ContainerSkin(
                "slim_glass",
                "Slim Glass",
                300,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_COMMON,
                "Slender modern silhouette with thick flat glass base.",
                (w, h, d) -> buildSlimGlass(w, h, d)
        ));

        // 3. Wide Tumbler
        addSkin(new ContainerSkin(
                "wide_tumbler",
                "Wide Tumbler",
                500,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_COMMON,
                "Broad drinking glass tapering gracefully toward the base.",
                (w, h, d) -> buildWideTumbler(w, h, d)
        ));

        // 4. Tall Cylinder
        addSkin(new ContainerSkin(
                "tall_cylinder",
                "Tall Cylinder",
                600,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_RARE,
                "Towering highball cylinder with a reinforced heavy glass base.",
                (w, h, d) -> buildTallCylinder(w, h, d)
        ));

        // 5. Short Glass
        addSkin(new ContainerSkin(
                "short_glass",
                "Short Glass",
                700,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_RARE,
                "Compact, sturdy tumbler with faceted beveled bottom corners.",
                (w, h, d) -> buildShortGlass(w, h, d)
        ));

        // 6. Laboratory Beaker
        addSkin(new ContainerSkin(
                "lab_beaker",
                "Laboratory Beaker",
                900,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_RARE,
                "Scientific beaker with pouring spout and measurement graduation marks.",
                (w, h, d) -> buildLabBeaker(w, h, d)
        ));

        // 7. Erlenmeyer Flask
        addSkin(new ContainerSkin(
                "erlenmeyer_flask",
                "Erlenmeyer Flask",
                1200,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_EPIC,
                "Classic conical science flask with narrow neck and wide triangular base.",
                (w, h, d) -> buildErlenmeyerFlask(w, h, d)
        ));

        // 8. Round Potion Bottle
        addSkin(new ContainerSkin(
                "potion_bottle",
                "Potion Bottle",
                1500,
                ContainerSkin.CAT_FANTASY,
                ContainerSkin.RARITY_EPIC,
                "Spherical alchemist bottle with cork collar and bulbous body.",
                (w, h, d) -> buildPotionBottle(w, h, d)
        ));

        // 9. Magic Potion Vial
        addSkin(new ContainerSkin(
                "magic_vial",
                "Magic Vial",
                2000,
                ContainerSkin.CAT_FANTASY,
                ContainerSkin.RARITY_LEGENDARY,
                "Amphora-inspired enchanted vial with pointed teardrop bottom.",
                (w, h, d) -> buildMagicVial(w, h, d)
        ));

        // 10. Mason Jar
        addSkin(new ContainerSkin(
                "mason_jar",
                "Mason Jar",
                750,
                ContainerSkin.CAT_BOTTLES,
                ContainerSkin.RARITY_COMMON,
                "Rustic square-shouldered jar with ribbed neck collar.",
                (w, h, d) -> buildMasonJar(w, h, d)
        ));

        // 11. Handled Mug
        addSkin(new ContainerSkin(
                "handled_mug",
                "Handled Mug",
                950,
                ContainerSkin.CAT_MUGS,
                ContainerSkin.RARITY_RARE,
                "Clear glass mug featuring a prominent side handle loop.",
                (w, h, d) -> buildHandledMug(w, h, d)
        ));

        // 12. Square Bottle
        addSkin(new ContainerSkin(
                "square_bottle",
                "Square Bottle",
                1100,
                ContainerSkin.CAT_BOTTLES,
                ContainerSkin.RARITY_RARE,
                "Geometric apothecary bottle with sharp angular shoulders.",
                (w, h, d) -> buildSquareBottle(w, h, d)
        ));

        // 13. Vintage Bottle
        addSkin(new ContainerSkin(
                "vintage_bottle",
                "Vintage Bottle",
                1400,
                ContainerSkin.CAT_BOTTLES,
                ContainerSkin.RARITY_EPIC,
                "Long-necked vintage elixir bottle with flared lip and punt base.",
                (w, h, d) -> buildVintageBottle(w, h, d)
        ));

        // 14. Crystal Decanter
        addSkin(new ContainerSkin(
                "crystal_decanter",
                "Crystal Decanter",
                2500,
                ContainerSkin.CAT_PREMIUM,
                ContainerSkin.RARITY_LEGENDARY,
                "Royal cut-crystal container with diamond faceted sides.",
                (w, h, d) -> buildCrystalDecanter(w, h, d)
        ));

        // 15. Diamond Flask
        addSkin(new ContainerSkin(
                "diamond_flask",
                "Diamond Flask",
                1800,
                ContainerSkin.CAT_FANTASY,
                ContainerSkin.RARITY_EPIC,
                "Futuristic hexagonal diamond container with sharp angular edges.",
                (w, h, d) -> buildDiamondFlask(w, h, d)
        ));

        // 16. Bubble Tube
        addSkin(new ContainerSkin(
                "bubble_tube",
                "Bubble Tube",
                1300,
                ContainerSkin.CAT_FANTASY,
                ContainerSkin.RARITY_RARE,
                "Playful container featuring three connected vertical glass bubbles.",
                (w, h, d) -> buildBubbleTube(w, h, d)
        ));

        // 17. Elegant Goblet
        addSkin(new ContainerSkin(
                "elegant_goblet",
                "Elegant Goblet",
                1600,
                ContainerSkin.CAT_CUPS,
                ContainerSkin.RARITY_EPIC,
                "Graceful wine goblet bowl raised on a slender glass stem and pedestal.",
                (w, h, d) -> buildElegantGoblet(w, h, d)
        ));

        // 18. Royal Chalice
        addSkin(new ContainerSkin(
                "royal_chalice",
                "Royal Chalice",
                2200,
                ContainerSkin.CAT_CUPS,
                ContainerSkin.RARITY_LEGENDARY,
                "Flared ceremonial chalice with scalloped waist and stepped base.",
                (w, h, d) -> buildRoyalChalice(w, h, d)
        ));

        // 19. Luxury Flute
        addSkin(new ContainerSkin(
                "luxury_flute",
                "Luxury Flute",
                1750,
                ContainerSkin.CAT_PREMIUM,
                ContainerSkin.RARITY_EPIC,
                "Tall, slender champagne flute on a delicate pedestal foot.",
                (w, h, d) -> buildLuxuryFlute(w, h, d)
        ));

        // 20. Celestial Urn
        addSkin(new ContainerSkin(
                "celestial_urn",
                "Celestial Urn",
                3000,
                ContainerSkin.CAT_PREMIUM,
                ContainerSkin.RARITY_LEGENDARY,
                "Classical Greek amphora with twin ornate loop handles and star engraving.",
                (w, h, d) -> buildCelestialUrn(w, h, d)
        ));
    }

    // ==================== GEOMETRY GENERATORS ====================

    // 1. Classic Tube
    private SkinGeometry buildClassicTube(float w, float h, float d) {
        float pad = 6f * d;
        float rimExtra = 4f * d;
        float rimH = 7f * d;
        float wall = 3.5f * d;

        float left = pad + rimExtra;
        float right = w - pad - rimExtra;
        float top = pad + rimH;
        float bottom = h - pad;
        float radius = (right - left) / 2f;

        Path outer = new Path();
        outer.moveTo(left, top);
        outer.lineTo(left, bottom - radius);
        outer.arcTo(left, bottom - 2 * radius, right, bottom, 180, -180, false);
        outer.lineTo(right, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(left - rimExtra, pad, right + rimExtra, top + rimH * 0.25f), 3f * d, 3f * d, Path.Direction.CW);

        float inLeft = left + wall;
        float inRight = right - wall;
        float inTop = top;
        float inBottom = bottom - wall;
        float inRadius = (inRight - inLeft) / 2f;

        Path inner = new Path();
        inner.moveTo(inLeft, inTop);
        inner.lineTo(inLeft, inBottom - inRadius);
        inner.arcTo(inLeft, inBottom - 2 * inRadius, inRight, inBottom, 180, -180, false);
        inner.lineTo(inRight, inTop);
        inner.close();

        RectF innerRect = new RectF(inLeft, inTop, inRight, inBottom);
        PointF openL = new PointF(left, top);
        PointF openR = new PointF(right, top);
        PointF pivL = new PointF(left - rimExtra, pad);
        PointF pivR = new PointF(right + rimExtra, pad);

        return new SkinGeometry(outer, inner, rim, null, innerRect, openL, openR, pivL, pivR, 52f);
    }

    // 2. Slim Glass
    private SkinGeometry buildSlimGlass(float w, float h, float d) {
        float pad = 6f * d;
        float rimExtra = 2.5f * d;
        float rimH = 5f * d;
        float wall = 3f * d;
        float baseThick = 12f * d;

        float left = pad + rimExtra + (w * 0.08f);
        float right = w - pad - rimExtra - (w * 0.08f);
        float top = pad + rimH;
        float bottom = h - pad;

        Path outer = new Path();
        outer.moveTo(left, top);
        outer.lineTo(left, bottom - 4f * d);
        outer.quadTo(left, bottom, left + 4f * d, bottom);
        outer.lineTo(right - 4f * d, bottom);
        outer.quadTo(right, bottom, right, bottom - 4f * d);
        outer.lineTo(right, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(left - rimExtra, pad, right + rimExtra, top + 2f * d), 2f * d, 2f * d, Path.Direction.CW);

        float inLeft = left + wall;
        float inRight = right - wall;
        float inTop = top;
        float inBottom = bottom - baseThick;

        Path inner = new Path();
        inner.moveTo(inLeft, inTop);
        inner.lineTo(inLeft, inBottom - 2f * d);
        inner.quadTo(inLeft, inBottom, inLeft + 2f * d, inBottom);
        inner.lineTo(inRight - 2f * d, inBottom);
        inner.quadTo(inRight, inBottom, inRight, inBottom - 2f * d);
        inner.lineTo(inRight, inTop);
        inner.close();

        // Base line decor
        Path decor = new Path();
        decor.moveTo(left + 2f * d, inBottom);
        decor.lineTo(right - 2f * d, inBottom);

        RectF innerRect = new RectF(inLeft, inTop, inRight, inBottom);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(left, top), new PointF(right, top), new PointF(left - rimExtra, pad), new PointF(right + rimExtra, pad), 50f);
    }

    // 3. Wide Tumbler
    private SkinGeometry buildWideTumbler(float w, float h, float d) {
        float pad = 6f * d;
        float rimH = 6f * d;
        float wall = 3.5f * d;

        float topL = pad;
        float topR = w - pad;
        float topY = pad + rimH;
        float botL = pad + (w * 0.12f);
        float botR = w - pad - (w * 0.12f);
        float botY = h - pad;

        Path outer = new Path();
        outer.moveTo(topL, topY);
        outer.lineTo(botL, botY - 5f * d);
        outer.quadTo(botL, botY, botL + 6f * d, botY);
        outer.lineTo(botR - 6f * d, botY);
        outer.quadTo(botR, botY, botR, botY - 5f * d);
        outer.lineTo(topR, topY);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(topL - 2f * d, pad, topR + 2f * d, topY + 2f * d), 3f * d, 3f * d, Path.Direction.CW);

        float inTopL = topL + wall;
        float inTopR = topR - wall;
        float inBotL = botL + wall;
        float inBotR = botR - wall;
        float inBotY = botY - (8f * d);

        Path inner = new Path();
        inner.moveTo(inTopL, topY);
        inner.lineTo(inBotL, inBotY - 3f * d);
        inner.quadTo(inBotL, inBotY, inBotL + 4f * d, inBotY);
        inner.lineTo(inBotR - 4f * d, inBotY);
        inner.quadTo(inBotR, inBotY, inBotR, inBotY - 3f * d);
        inner.lineTo(inTopR, topY);
        inner.close();

        RectF innerRect = new RectF(inBotL, topY, inBotR, inBotY);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(topL, topY), new PointF(topR, topY), new PointF(topL, pad), new PointF(topR, pad), 45f);
    }

    // 4. Tall Cylinder
    private SkinGeometry buildTallCylinder(float w, float h, float d) {
        float pad = 5f * d;
        float left = pad + (w * 0.05f);
        float right = w - pad - (w * 0.05f);
        float top = pad + (7f * d);
        float bottom = h - pad;
        float wall = 3.5f * d;
        float pedestalH = 14f * d;

        Path outer = new Path();
        outer.moveTo(left, top);
        outer.lineTo(left, bottom - pedestalH);
        outer.lineTo(left - 3f * d, bottom - pedestalH);
        outer.lineTo(left - 3f * d, bottom);
        outer.lineTo(right + 3f * d, bottom);
        outer.lineTo(right + 3f * d, bottom - pedestalH);
        outer.lineTo(right, bottom - pedestalH);
        outer.lineTo(right, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(left - 2.5f * d, pad, right + 2.5f * d, top + 2f * d), 3f * d, 3f * d, Path.Direction.CW);

        float inLeft = left + wall;
        float inRight = right - wall;
        float inBottom = bottom - pedestalH;

        Path inner = new Path();
        inner.moveTo(inLeft, top);
        inner.lineTo(inLeft, inBottom - 3f * d);
        inner.quadTo(inLeft, inBottom, inLeft + 3f * d, inBottom);
        inner.lineTo(inRight - 3f * d, inBottom);
        inner.quadTo(inRight, inBottom, inRight, inBottom - 3f * d);
        inner.lineTo(inRight, top);
        inner.close();

        Path decor = new Path();
        decor.moveTo(left - 3f * d, bottom - pedestalH);
        decor.lineTo(right + 3f * d, bottom - pedestalH);

        RectF innerRect = new RectF(inLeft, top, inRight, inBottom);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(left, top), new PointF(right, top), new PointF(left, pad), new PointF(right, pad), 54f);
    }

    // 5. Short Glass
    private SkinGeometry buildShortGlass(float w, float h, float d) {
        float pad = 8f * d;
        float left = pad;
        float right = w - pad;
        float top = pad + (8f * d);
        float bottom = h - pad;
        float wall = 4f * d;
        float bevel = 12f * d;

        Path outer = new Path();
        outer.moveTo(left, top);
        outer.lineTo(left, bottom - bevel);
        outer.lineTo(left + bevel, bottom);
        outer.lineTo(right - bevel, bottom);
        outer.lineTo(right, bottom - bevel);
        outer.lineTo(right, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(left - 2f * d, pad, right + 2f * d, top + 3f * d), 3f * d, 3f * d, Path.Direction.CW);

        float inLeft = left + wall;
        float inRight = right - wall;
        float inBottom = bottom - wall;
        float inBevel = bevel * 0.7f;

        Path inner = new Path();
        inner.moveTo(inLeft, top);
        inner.lineTo(inLeft, inBottom - inBevel);
        inner.lineTo(inLeft + inBevel, inBottom);
        inner.lineTo(inRight - inBevel, inBottom);
        inner.lineTo(inRight, inBottom - inBevel);
        inner.lineTo(inRight, top);
        inner.close();

        RectF innerRect = new RectF(inLeft, top, inRight, inBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(left, top), new PointF(right, top), new PointF(left, pad), new PointF(right, pad), 48f);
    }

    // 6. Laboratory Beaker
    private SkinGeometry buildLabBeaker(float w, float h, float d) {
        float pad = 7f * d;
        float left = pad + (4f * d);
        float right = w - pad - (4f * d);
        float top = pad + (8f * d);
        float bottom = h - pad;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(left, top);
        outer.lineTo(left, bottom - 6f * d);
        outer.quadTo(left, bottom, left + 6f * d, bottom);
        outer.lineTo(right - 6f * d, bottom);
        outer.quadTo(right, bottom, right, bottom - 6f * d);
        outer.lineTo(right, top);
        outer.close();

        // Rim with prominent flared pouring spout on both sides
        Path rim = new Path();
        rim.moveTo(left - (6f * d), pad + (2f * d));
        rim.lineTo(left, top);
        rim.lineTo(right, top);
        rim.lineTo(right + (6f * d), pad + (2f * d));
        rim.lineTo(right + (4f * d), pad);
        rim.lineTo(left - (4f * d), pad);
        rim.close();

        float inLeft = left + wall;
        float inRight = right - wall;
        float inBottom = bottom - (4f * d);

        Path inner = new Path();
        inner.moveTo(inLeft, top);
        inner.lineTo(inLeft, inBottom - 4f * d);
        inner.quadTo(inLeft, inBottom, inLeft + 4f * d, inBottom);
        inner.lineTo(inRight - 4f * d, inBottom);
        inner.quadTo(inRight, inBottom, inRight, inBottom - 4f * d);
        inner.lineTo(inRight, top);
        inner.close();

        // Measurement tick marks along the right side
        Path decor = new Path();
        float innerH = inBottom - top;
        for (int i = 1; i <= 4; i++) {
            float y = inBottom - (innerH * (i / 5f));
            float tickLen = (i % 2 == 0) ? 9f * d : 5f * d;
            decor.moveTo(inRight - tickLen, y);
            decor.lineTo(inRight - 2f * d, y);
        }

        RectF innerRect = new RectF(inLeft, top, inRight, inBottom);
        PointF openL = new PointF(left - (6f * d), pad + (2f * d));
        PointF openR = new PointF(right + (6f * d), pad + (2f * d));
        return new SkinGeometry(outer, inner, rim, decor, innerRect, openL, openR, openL, openR, 46f);
    }

    // 7. Erlenmeyer Flask
    private SkinGeometry buildErlenmeyerFlask(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.38f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float neckH = h * 0.32f;
        float top = pad + (7f * d);
        float bottom = h - pad;
        float baseL = pad;
        float baseR = w - pad;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, top + neckH);
        outer.lineTo(baseL, bottom - (8f * d));
        outer.quadTo(baseL, bottom, baseL + (10f * d), bottom);
        outer.lineTo(baseR - (10f * d), bottom);
        outer.quadTo(baseR, bottom, baseR, bottom - (8f * d));
        outer.lineTo(neckR, top + neckH);
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3f * d), pad, neckR + (3f * d), top + (2.5f * d)), 3f * d, 3f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inBaseL = baseL + wall;
        float inBaseR = baseR - wall;
        float inBottom = bottom - (4f * d);

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, top + neckH);
        inner.lineTo(inBaseL, inBottom - (6f * d));
        inner.quadTo(inBaseL, inBottom, inBaseL + (8f * d), inBottom);
        inner.lineTo(inBaseR - (8f * d), inBottom);
        inner.quadTo(inBaseR, inBottom, inBaseR, inBottom - (6f * d));
        inner.lineTo(inNeckR, top + neckH);
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inBaseL, top, inBaseR, inBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL - (3f * d), pad), new PointF(neckR + (3f * d), pad), 58f);
    }

    // 8. Round Potion Bottle
    private SkinGeometry buildPotionBottle(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.36f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float top = pad + (7f * d);
        float neckBottom = pad + (h * 0.28f);
        float bulbCenterY = (neckBottom + h - pad) / 2f;
        float bulbRadius = (w - 2 * pad) / 2f;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, neckBottom);
        // Bulb arc
        RectF bulbRect = new RectF(w / 2f - bulbRadius, bulbCenterY - bulbRadius, w / 2f + bulbRadius, bulbCenterY + bulbRadius);
        outer.arcTo(bulbRect, 220, 260, false);
        outer.lineTo(neckR, neckBottom);
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (4f * d), pad, neckR + (4f * d), top + (3f * d)), 3f * d, 3f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inBulbRadius = bulbRadius - wall;
        RectF inBulbRect = new RectF(w / 2f - inBulbRadius, bulbCenterY - inBulbRadius, w / 2f + inBulbRadius, bulbCenterY + inBulbRadius);

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, neckBottom);
        inner.arcTo(inBulbRect, 220, 260, false);
        inner.lineTo(inNeckR, neckBottom);
        inner.lineTo(inNeckR, top);
        inner.close();

        // Decorative cork collar ring
        Path decor = new Path();
        decor.addRoundRect(new RectF(neckL - (2f * d), neckBottom - (4f * d), neckR + (2f * d), neckBottom), 2f * d, 2f * d, Path.Direction.CW);

        RectF innerRect = new RectF(w / 2f - inBulbRadius, top, w / 2f + inBulbRadius, bulbCenterY + inBulbRadius);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL - (4f * d), pad), new PointF(neckR + (4f * d), pad), 60f);
    }

    // 9. Magic Potion Vial
    private SkinGeometry buildMagicVial(float w, float h, float d) {
        float pad = 6f * d;
        float midX = w / 2f;
        float top = pad + (7f * d);
        float neckW = w * 0.4f;
        float neckL = midX - neckW / 2f;
        float neckR = midX + neckW / 2f;
        float bottom = h - pad;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.quadTo(neckL, top + (h * 0.15f), pad, top + (h * 0.4f));
        outer.cubicTo(pad, top + (h * 0.65f), midX - (6f * d), bottom - (4f * d), midX, bottom);
        outer.cubicTo(midX + (6f * d), bottom - (4f * d), w - pad, top + (h * 0.65f), w - pad, top + (h * 0.4f));
        outer.quadTo(neckR, top + (h * 0.15f), neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3f * d), pad, neckR + (3f * d), top + (2.5f * d)), 3f * d, 3f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inPad = pad + wall;

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.quadTo(inNeckL, top + (h * 0.15f), inPad, top + (h * 0.4f));
        inner.cubicTo(inPad, top + (h * 0.65f), midX - (4f * d), bottom - wall - (3f * d), midX, bottom - wall);
        inner.cubicTo(midX + (4f * d), bottom - wall - (3f * d), w - inPad, top + (h * 0.65f), w - inPad, top + (h * 0.4f));
        inner.quadTo(inNeckR, top + (h * 0.15f), inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inPad, top, w - inPad, bottom - wall);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 55f);
    }

    // 10. Mason Jar
    private SkinGeometry buildMasonJar(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.72f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float top = pad + (9f * d);
        float shoulderY = top + (h * 0.10f);
        float bottom = h - pad;
        float bodyL = pad + (3f * d);
        float bodyR = w - pad - (3f * d);
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, shoulderY);
        outer.quadTo(neckL, shoulderY + (6f * d), bodyL, shoulderY + (10f * d));
        outer.lineTo(bodyL, bottom - (6f * d));
        outer.quadTo(bodyL, bottom, bodyL + (6f * d), bottom);
        outer.lineTo(bodyR - (6f * d), bottom);
        outer.quadTo(bodyR, bottom, bodyR, bottom - (6f * d));
        outer.lineTo(bodyR, shoulderY + (10f * d));
        outer.quadTo(neckR, shoulderY + (6f * d), neckR, shoulderY);
        outer.lineTo(neckR, top);
        outer.close();

        // Ribbed neck rim
        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3f * d), pad, neckR + (3f * d), top + (2f * d)), 2f * d, 2f * d, Path.Direction.CW);
        rim.addRoundRect(new RectF(neckL - (2f * d), top + (3f * d), neckR + (2f * d), top + (6f * d)), 1.5f * d, 1.5f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inBodyL = bodyL + wall;
        float inBodyR = bodyR - wall;
        float inBottom = bottom - wall;

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, shoulderY);
        inner.quadTo(inNeckL, shoulderY + (5f * d), inBodyL, shoulderY + (9f * d));
        inner.lineTo(inBodyL, inBottom - (5f * d));
        inner.quadTo(inBodyL, inBottom, inBodyL + (5f * d), inBottom);
        inner.lineTo(inBodyR - (5f * d), inBottom);
        inner.quadTo(inBodyR, inBottom, inBodyR, inBottom - (5f * d));
        inner.lineTo(inBodyR, shoulderY + (9f * d));
        inner.quadTo(inNeckR, shoulderY + (5f * d), inNeckR, shoulderY);
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inBodyL, top, inBodyR, inBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 50f);
    }

    // 11. Handled Mug
    private SkinGeometry buildHandledMug(float w, float h, float d) {
        float pad = 6f * d;
        float handleW = w * 0.18f;
        float bodyL = pad + (2f * d);
        float bodyR = w - pad - handleW;
        float top = pad + (7f * d);
        float bottom = h - pad;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(bodyL, top);
        outer.lineTo(bodyL, bottom - (6f * d));
        outer.quadTo(bodyL, bottom, bodyL + (6f * d), bottom);
        outer.lineTo(bodyR - (6f * d), bottom);
        outer.quadTo(bodyR, bottom, bodyR, bottom - (6f * d));
        outer.lineTo(bodyR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(bodyL - (2f * d), pad, bodyR + (2f * d), top + (2.5f * d)), 2.5f * d, 2.5f * d, Path.Direction.CW);

        // Glass Handle loop on the right side
        Path decor = new Path();
        float handleTopY = top + (h * 0.18f);
        float handleBotY = bottom - (h * 0.22f);
        decor.moveTo(bodyR, handleTopY);
        decor.cubicTo(w - pad + (4f * d), handleTopY - (4f * d), w - pad + (4f * d), handleBotY + (4f * d), bodyR, handleBotY);
        decor.lineTo(bodyR, handleBotY - (6f * d));
        decor.cubicTo(w - pad - (2f * d), handleBotY - (2f * d), w - pad - (2f * d), handleTopY + (2f * d), bodyR, handleTopY + (6f * d));
        decor.close();

        float inL = bodyL + wall;
        float inR = bodyR - wall;
        float inBottom = bottom - wall;

        Path inner = new Path();
        inner.moveTo(inL, top);
        inner.lineTo(inL, inBottom - (4f * d));
        inner.quadTo(inL, inBottom, inL + (4f * d), inBottom);
        inner.lineTo(inR - (4f * d), inBottom);
        inner.quadTo(inR, inBottom, inR, inBottom - (4f * d));
        inner.lineTo(inR, top);
        inner.close();

        RectF innerRect = new RectF(inL, top, inR, inBottom);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(bodyL, top), new PointF(bodyR, top), new PointF(bodyL, pad), new PointF(bodyR, pad), 48f);
    }

    // 12. Square Bottle
    private SkinGeometry buildSquareBottle(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.35f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float top = pad + (7f * d);
        float neckBottom = top + (h * 0.18f);
        float bodyL = pad + (3f * d);
        float bodyR = w - pad - (3f * d);
        float bottom = h - pad;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, neckBottom);
        outer.lineTo(bodyL + (4f * d), neckBottom + (4f * d));
        outer.lineTo(bodyL, neckBottom + (8f * d));
        outer.lineTo(bodyL, bottom - (4f * d));
        outer.lineTo(bodyL + (4f * d), bottom);
        outer.lineTo(bodyR - (4f * d), bottom);
        outer.lineTo(bodyR, bottom - (4f * d));
        outer.lineTo(bodyR, neckBottom + (8f * d));
        outer.lineTo(bodyR - (4f * d), neckBottom + (4f * d));
        outer.lineTo(neckR, neckBottom);
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3f * d), pad, neckR + (3f * d), top + (2.5f * d)), 2f * d, 2f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inBodyL = bodyL + wall;
        float inBodyR = bodyR - wall;
        float inBottom = bottom - wall;

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, neckBottom);
        inner.lineTo(inBodyL, neckBottom + (8f * d));
        inner.lineTo(inBodyL, inBottom);
        inner.lineTo(inBodyR, inBottom);
        inner.lineTo(inBodyR, neckBottom + (8f * d));
        inner.lineTo(inNeckR, neckBottom);
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inBodyL, top, inBodyR, inBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 54f);
    }

    // 13. Vintage Bottle
    private SkinGeometry buildVintageBottle(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.32f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float top = pad + (8f * d);
        float neckH = h * 0.30f;
        float bottom = h - pad;
        float bodyL = pad + (4f * d);
        float bodyR = w - pad - (4f * d);
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, top + neckH);
        outer.cubicTo(neckL, top + neckH + (12f * d), bodyL, top + neckH + (18f * d), bodyL, top + neckH + (26f * d));
        outer.lineTo(bodyL, bottom - (6f * d));
        outer.quadTo(bodyL, bottom, bodyL + (6f * d), bottom);
        outer.lineTo(bodyR - (6f * d), bottom);
        outer.quadTo(bodyR, bottom, bodyR, bottom - (6f * d));
        outer.lineTo(bodyR, top + neckH + (26f * d));
        outer.cubicTo(bodyR, top + neckH + (18f * d), neckR, top + neckH + (12f * d), neckR, top + neckH);
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3.5f * d), pad, neckR + (3.5f * d), top + (3f * d)), 2.5f * d, 2.5f * d, Path.Direction.CW);

        // Raised vintage neck collar ring
        Path decor = new Path();
        decor.addRoundRect(new RectF(neckL - (2f * d), top + (8f * d), neckR + (2f * d), top + (12f * d)), 2f * d, 2f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inBodyL = bodyL + wall;
        float inBodyR = bodyR - wall;
        float inBottom = bottom - wall;

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, top + neckH);
        inner.cubicTo(inNeckL, top + neckH + (11f * d), inBodyL, top + neckH + (17f * d), inBodyL, top + neckH + (25f * d));
        inner.lineTo(inBodyL, inBottom - (4f * d));
        inner.quadTo(inBodyL, inBottom, inBodyL + (4f * d), inBottom);
        inner.lineTo(inBodyR - (4f * d), inBottom);
        inner.quadTo(inBodyR, inBottom, inBodyR, inBottom - (4f * d));
        inner.lineTo(inBodyR, top + neckH + (25f * d));
        inner.cubicTo(inBodyR, top + neckH + (17f * d), inNeckR, top + neckH + (11f * d), inNeckR, top + neckH);
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inBodyL, top, inBodyR, inBottom);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 56f);
    }

    // 14. Crystal Decanter
    private SkinGeometry buildCrystalDecanter(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.44f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float top = pad + (8f * d);
        float bottom = h - pad;
        float midY = top + (h * 0.42f);
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, top + (h * 0.12f));
        outer.lineTo(pad, midY);
        outer.lineTo(pad + (w * 0.15f), bottom - (10f * d));
        outer.lineTo(pad + (w * 0.10f), bottom);
        outer.lineTo(w - pad - (w * 0.10f), bottom);
        outer.lineTo(w - pad - (w * 0.15f), bottom - (10f * d));
        outer.lineTo(w - pad, midY);
        outer.lineTo(neckR, top + (h * 0.12f));
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (4f * d), pad, neckR + (4f * d), top + (3f * d)), 3f * d, 3f * d, Path.Direction.CW);

        // Faceted diamond cuts decor
        Path decor = new Path();
        decor.moveTo(pad, midY);
        decor.lineTo(w / 2f, midY - (h * 0.12f));
        decor.lineTo(w - pad, midY);
        decor.lineTo(w / 2f, midY + (h * 0.16f));
        decor.close();

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inPad = pad + wall;
        float inBottom = bottom - (10f * d);

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, top + (h * 0.12f));
        inner.lineTo(inPad, midY);
        inner.lineTo(inPad + (w * 0.15f), inBottom);
        inner.lineTo(w - inPad - (w * 0.15f), inBottom);
        inner.lineTo(w - inPad, midY);
        inner.lineTo(inNeckR, top + (h * 0.12f));
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inPad, top, w - inPad, inBottom);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 52f);
    }

    // 15. Diamond Flask
    private SkinGeometry buildDiamondFlask(float w, float h, float d) {
        float pad = 6f * d;
        float neckW = w * 0.38f;
        float neckL = (w - neckW) / 2f;
        float neckR = neckL + neckW;
        float top = pad + (7f * d);
        float bottom = h - pad;
        float shoulderY = top + (h * 0.18f);
        float waistY = top + (h * 0.50f);
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, shoulderY);
        outer.lineTo(pad, waistY);
        outer.lineTo(pad + (w * 0.16f), bottom);
        outer.lineTo(w - pad - (w * 0.16f), bottom);
        outer.lineTo(w - pad, waistY);
        outer.lineTo(neckR, shoulderY);
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3f * d), pad, neckR + (3f * d), top + (2.5f * d)), 2.5f * d, 2.5f * d, Path.Direction.CW);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inPad = pad + wall;
        float inBottom = bottom - wall;

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, shoulderY);
        inner.lineTo(inPad, waistY);
        inner.lineTo(inPad + (w * 0.16f), inBottom);
        inner.lineTo(w - inPad - (w * 0.16f), inBottom);
        inner.lineTo(w - inPad, waistY);
        inner.lineTo(inNeckR, shoulderY);
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inPad, top, w - inPad, inBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 54f);
    }

    // 16. Bubble Tube (Three stacked spheres)
    private SkinGeometry buildBubbleTube(float w, float h, float d) {
        float pad = 6f * d;
        float top = pad + (7f * d);
        float bottom = h - pad;
        float midX = w / 2f;
        float totalH = bottom - top;
        float sphereR = (w - 2 * pad) / 2f;
        float wall = 3.5f * d;

        // 3 spheres stacked
        float cY1 = top + totalH * 0.22f;
        float cY2 = top + totalH * 0.55f;
        float cY3 = top + totalH * 0.85f;

        Path outer = new Path();
        outer.addCircle(midX, cY1, sphereR * 0.85f, Path.Direction.CW);
        outer.addCircle(midX, cY2, sphereR * 0.95f, Path.Direction.CW);
        outer.addCircle(midX, cY3, sphereR, Path.Direction.CW);

        float neckW = w * 0.35f;
        Path neck = new Path();
        neck.addRect(new RectF(midX - neckW / 2f, top, midX + neckW / 2f, cY1), Path.Direction.CW);
        outer.op(neck, Path.Op.UNION);

        Path rim = new Path();
        rim.addRoundRect(new RectF(midX - neckW / 2f - 3f * d, pad, midX + neckW / 2f + 3f * d, top + 2.5f * d), 2f * d, 2f * d, Path.Direction.CW);

        Path inner = new Path();
        inner.addCircle(midX, cY1, (sphereR * 0.85f) - wall, Path.Direction.CW);
        inner.addCircle(midX, cY2, (sphereR * 0.95f) - wall, Path.Direction.CW);
        inner.addCircle(midX, cY3, sphereR - wall, Path.Direction.CW);
        Path inNeck = new Path();
        inNeck.addRect(new RectF(midX - neckW / 2f + wall, top, midX + neckW / 2f - wall, cY1), Path.Direction.CW);
        inner.op(inNeck, Path.Op.UNION);

        RectF innerRect = new RectF(midX - sphereR + wall, top, midX + sphereR - wall, cY3 + sphereR - wall);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(midX - neckW / 2f, top), new PointF(midX + neckW / 2f, top), new PointF(midX - neckW / 2f, pad), new PointF(midX + neckW / 2f, pad), 55f);
    }

    // 17. Elegant Goblet
    private SkinGeometry buildElegantGoblet(float w, float h, float d) {
        float pad = 6f * d;
        float midX = w / 2f;
        float top = pad + (7f * d);
        float bowlBottom = top + (h * 0.58f);
        float bottom = h - pad;
        float stemW = 5f * d;
        float footW = w * 0.65f;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(pad, top);
        outer.cubicTo(pad, bowlBottom, midX - stemW / 2f, bowlBottom, midX - stemW / 2f, bottom - (6f * d));
        // Pedestal foot
        outer.lineTo(midX - footW / 2f, bottom);
        outer.lineTo(midX + footW / 2f, bottom);
        outer.lineTo(midX + stemW / 2f, bottom - (6f * d));
        outer.cubicTo(midX + stemW / 2f, bowlBottom, w - pad, bowlBottom, w - pad, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(pad - (2f * d), pad, w - pad + (2f * d), top + (2.5f * d)), 2.5f * d, 2.5f * d, Path.Direction.CW);

        float inL = pad + wall;
        float inR = w - pad - wall;
        float inBowlBottom = bowlBottom - wall;

        Path inner = new Path();
        inner.moveTo(inL, top);
        inner.cubicTo(inL, inBowlBottom, midX - (2f * d), inBowlBottom, midX, inBowlBottom);
        inner.cubicTo(midX + (2f * d), inBowlBottom, inR, inBowlBottom, inR, top);
        inner.close();

        RectF innerRect = new RectF(inL, top, inR, inBowlBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(pad, top), new PointF(w - pad, top), new PointF(pad, pad), new PointF(w - pad, pad), 46f);
    }

    // 18. Royal Chalice
    private SkinGeometry buildRoyalChalice(float w, float h, float d) {
        float pad = 6f * d;
        float midX = w / 2f;
        float top = pad + (7f * d);
        float waistY = top + (h * 0.48f);
        float bottom = h - pad;
        float footW = w * 0.75f;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(pad, top);
        outer.quadTo(pad + (w * 0.12f), waistY * 0.6f, midX - (6f * d), waistY);
        outer.lineTo(midX - (7f * d), bottom - (10f * d));
        outer.lineTo(midX - footW / 2f, bottom);
        outer.lineTo(midX + footW / 2f, bottom);
        outer.lineTo(midX + (7f * d), bottom - (10f * d));
        outer.lineTo(midX + (6f * d), waistY);
        outer.quadTo(w - pad - (w * 0.12f), waistY * 0.6f, w - pad, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(pad - (3f * d), pad, w - pad + (3f * d), top + (3f * d)), 3f * d, 3f * d, Path.Direction.CW);

        // Decorative royal waistband
        Path decor = new Path();
        decor.addRoundRect(new RectF(midX - (10f * d), waistY - (4f * d), midX + (10f * d), waistY + (2f * d)), 2f * d, 2f * d, Path.Direction.CW);

        float inL = pad + wall;
        float inR = w - pad - wall;
        float inWaistY = waistY - wall;

        Path inner = new Path();
        inner.moveTo(inL, top);
        inner.quadTo(inL + (w * 0.12f), inWaistY * 0.6f, midX - (3f * d), inWaistY);
        inner.lineTo(midX + (3f * d), inWaistY);
        inner.quadTo(w - inL - (w * 0.12f), inWaistY * 0.6f, inR, top);
        inner.close();

        RectF innerRect = new RectF(inL, top, inR, inWaistY);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(pad, top), new PointF(w - pad, top), new PointF(pad, pad), new PointF(w - pad, pad), 47f);
    }

    // 19. Luxury Flute
    private SkinGeometry buildLuxuryFlute(float w, float h, float d) {
        float pad = 6f * d;
        float midX = w / 2f;
        float top = pad + (7f * d);
        float fluteW = w * 0.46f;
        float fluteL = midX - fluteW / 2f;
        float fluteR = midX + fluteW / 2f;
        float bowlBottom = top + (h * 0.64f);
        float bottom = h - pad;
        float stemW = 4f * d;
        float footW = w * 0.60f;
        float wall = 3f * d;

        Path outer = new Path();
        outer.moveTo(fluteL, top);
        outer.lineTo(fluteL + (2f * d), bowlBottom - (12f * d));
        outer.quadTo(fluteL + (2f * d), bowlBottom, midX - stemW / 2f, bowlBottom + (4f * d));
        outer.lineTo(midX - stemW / 2f, bottom - (6f * d));
        outer.lineTo(midX - footW / 2f, bottom);
        outer.lineTo(midX + footW / 2f, bottom);
        outer.lineTo(midX + stemW / 2f, bottom - (6f * d));
        outer.lineTo(midX + stemW / 2f, bowlBottom + (4f * d));
        outer.quadTo(fluteR - (2f * d), bowlBottom, fluteR - (2f * d), bowlBottom - (12f * d));
        outer.lineTo(fluteR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(fluteL - (2f * d), pad, fluteR + (2f * d), top + (2.5f * d)), 2f * d, 2f * d, Path.Direction.CW);

        float inL = fluteL + wall;
        float inR = fluteR - wall;
        float inBowlBottom = bowlBottom - (2f * d);

        Path inner = new Path();
        inner.moveTo(inL, top);
        inner.lineTo(inL + (2f * d), inBowlBottom - (10f * d));
        inner.quadTo(inL + (2f * d), inBowlBottom, midX, inBowlBottom);
        inner.quadTo(inR - (2f * d), inBowlBottom, inR - (2f * d), inBowlBottom - (10f * d));
        inner.lineTo(inR, top);
        inner.close();

        RectF innerRect = new RectF(inL, top, inR, inBowlBottom);
        return new SkinGeometry(outer, inner, rim, null, innerRect, new PointF(fluteL, top), new PointF(fluteR, top), new PointF(fluteL, pad), new PointF(fluteR, pad), 52f);
    }

    // 20. Celestial Urn
    private SkinGeometry buildCelestialUrn(float w, float h, float d) {
        float pad = 6f * d;
        float midX = w / 2f;
        float handleW = w * 0.14f;
        float neckW = w * 0.42f;
        float neckL = midX - neckW / 2f;
        float neckR = midX + neckW / 2f;
        float top = pad + (7f * d);
        float neckH = h * 0.16f;
        float bodyL = pad + handleW;
        float bodyR = w - pad - handleW;
        float bottom = h - pad;
        float wall = 3.5f * d;

        Path outer = new Path();
        outer.moveTo(neckL, top);
        outer.lineTo(neckL, top + neckH);
        outer.cubicTo(bodyL, top + neckH + (8f * d), bodyL, bottom - (h * 0.20f), midX - (w * 0.15f), bottom - (8f * d));
        outer.lineTo(midX - (w * 0.20f), bottom);
        outer.lineTo(midX + (w * 0.20f), bottom);
        outer.lineTo(midX + (w * 0.15f), bottom - (8f * d));
        outer.cubicTo(bodyR, bottom - (h * 0.20f), bodyR, top + neckH + (8f * d), neckR, top + neckH);
        outer.lineTo(neckR, top);
        outer.close();

        Path rim = new Path();
        rim.addRoundRect(new RectF(neckL - (3.5f * d), pad, neckR + (3.5f * d), top + (3f * d)), 2.5f * d, 2.5f * d, Path.Direction.CW);

        // Twin ornate loop handles on both sides
        Path decor = new Path();
        float hTopY = top + neckH + (4f * d);
        float hBotY = top + (h * 0.55f);
        // Left handle
        decor.moveTo(bodyL + (2f * d), hTopY);
        decor.cubicTo(pad - (2f * d), hTopY - (4f * d), pad - (2f * d), hBotY + (4f * d), bodyL + (2f * d), hBotY);
        // Right handle
        decor.moveTo(bodyR - (2f * d), hTopY);
        decor.cubicTo(w - pad + (2f * d), hTopY - (4f * d), w - pad + (2f * d), hBotY + (4f * d), bodyR - (2f * d), hBotY);

        float inNeckL = neckL + wall;
        float inNeckR = neckR - wall;
        float inBodyL = bodyL + wall;
        float inBodyR = bodyR - wall;
        float inBottom = bottom - (8f * d);

        Path inner = new Path();
        inner.moveTo(inNeckL, top);
        inner.lineTo(inNeckL, top + neckH);
        inner.cubicTo(inBodyL, top + neckH + (8f * d), inBodyL, inBottom - (h * 0.15f), midX - (w * 0.14f), inBottom);
        inner.lineTo(midX + (w * 0.14f), inBottom);
        inner.cubicTo(inBodyR, inBottom - (h * 0.15f), inBodyR, top + neckH + (8f * d), inNeckR, top + neckH);
        inner.lineTo(inNeckR, top);
        inner.close();

        RectF innerRect = new RectF(inBodyL, top, inBodyR, inBottom);
        return new SkinGeometry(outer, inner, rim, decor, innerRect, new PointF(neckL, top), new PointF(neckR, top), new PointF(neckL, pad), new PointF(neckR, pad), 52f);
    }
}
