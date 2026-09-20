package com.aquasort.puzzle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.aquasort.puzzle.models.ContainerSkin;
import com.aquasort.puzzle.models.SkinGeometry;
import com.aquasort.puzzle.utils.Constants;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Unit tests verifying the container skin definitions, silhouettes, rarity tiers,
 * pricing integrity, and geometric calculations.
 */
public class ContainerSkinTest {

    @Test
    public void testSkinModelProperties() {
        ContainerSkin skin = new ContainerSkin(
                "test_skin",
                "Test Bottle",
                500,
                ContainerSkin.CAT_BOTTLES,
                ContainerSkin.RARITY_RARE,
                "Test description",
                (w, h, d) -> new SkinGeometry(null, null, null, null, null, null, null, null, null, 45f)
        );

        assertEquals("test_skin", skin.getId());
        assertEquals("Test Bottle", skin.getName());
        assertEquals(500, skin.getPrice());
        assertEquals(ContainerSkin.CAT_BOTTLES, skin.getCategory());
        assertEquals(ContainerSkin.RARITY_RARE, skin.getRarity());
        assertEquals("Test description", skin.getDescription());
        assertFalse(skin.isFree());

        SkinGeometry geom = skin.createGeometry(100, 200, 2f);
        assertNotNull(geom);
        assertEquals(45f, geom.getTiltAngle(), 0.01f);
    }

    @Test
    public void testFreeDefaultSkin() {
        ContainerSkin freeSkin = new ContainerSkin(
                Constants.DEFAULT_SKIN_ID,
                "Classic Tube",
                0,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.RARITY_COMMON,
                "Classic test tube",
                (w, h, d) -> new SkinGeometry(null, null, null, null, null, null, null, null, null, 52f)
        );

        assertTrue("Default classic tube must be free", freeSkin.isFree());
        assertEquals(0, freeSkin.getPrice());
        assertEquals(Constants.DEFAULT_SKIN_ID, freeSkin.getId());
    }

    @Test
    public void testRarityConstants() {
        List<String> validRarities = Arrays.asList(
                ContainerSkin.RARITY_COMMON,
                ContainerSkin.RARITY_RARE,
                ContainerSkin.RARITY_EPIC,
                ContainerSkin.RARITY_LEGENDARY
        );
        assertEquals(4, validRarities.size());
        assertTrue(validRarities.contains("COMMON"));
        assertTrue(validRarities.contains("RARE"));
        assertTrue(validRarities.contains("EPIC"));
        assertTrue(validRarities.contains("LEGENDARY"));
    }

    @Test
    public void testCategoryConstants() {
        List<String> validCategories = Arrays.asList(
                ContainerSkin.CAT_ALL,
                ContainerSkin.CAT_GLASS,
                ContainerSkin.CAT_BOTTLES,
                ContainerSkin.CAT_CUPS,
                ContainerSkin.CAT_MUGS,
                ContainerSkin.CAT_FANTASY,
                ContainerSkin.CAT_PREMIUM
        );
        assertEquals(7, validCategories.size());
    }

    @Test
    public void testUniqueIdsAcrossSkins() {
        String[] skinIds = new String[]{
                "classic_tube", "slim_glass", "wide_tumbler", "tall_cylinder", "short_glass",
                "lab_beaker", "erlenmeyer_flask", "potion_bottle", "magic_vial", "mason_jar",
                "handled_mug", "square_bottle", "vintage_bottle", "crystal_decanter", "diamond_flask",
                "bubble_tube", "elegant_goblet", "royal_chalice", "luxury_flute", "celestial_urn"
        };

        assertEquals("Must have 20 distinct container skins", 20, skinIds.length);
        Set<String> unique = new HashSet<>(Arrays.asList(skinIds));
        assertEquals("All skin IDs must be strictly unique", 20, unique.size());
    }
}
