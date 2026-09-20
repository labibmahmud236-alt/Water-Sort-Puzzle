package com.aquasort.puzzle.models;

/**
 * Model representing a container skin/design in Aqua Sort.
 * Defines metadata (id, name, price, category, rarity) and provides
 * a geometry builder to construct unique vector paths for any screen density and view size.
 */
public class ContainerSkin {

    public interface GeometryBuilder {
        SkinGeometry build(float width, float height, float density);
    }

    public static final String CAT_ALL = "ALL";
    public static final String CAT_GLASS = "GLASS";
    public static final String CAT_BOTTLES = "BOTTLES";
    public static final String CAT_CUPS = "CUPS";
    public static final String CAT_MUGS = "MUGS";
    public static final String CAT_FANTASY = "FANTASY";
    public static final String CAT_PREMIUM = "PREMIUM";

    public static final String RARITY_COMMON = "COMMON";
    public static final String RARITY_RARE = "RARE";
    public static final String RARITY_EPIC = "EPIC";
    public static final String RARITY_LEGENDARY = "LEGENDARY";

    private final String id;
    private final String name;
    private final int price;
    private final String category;
    private final String rarity;
    private final String description;
    private final GeometryBuilder geometryBuilder;

    public ContainerSkin(
            String id,
            String name,
            int price,
            String category,
            String rarity,
            String description,
            GeometryBuilder geometryBuilder
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.rarity = rarity;
        this.description = description;
        this.geometryBuilder = geometryBuilder;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getRarity() {
        return rarity;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFree() {
        return price <= 0;
    }

    public SkinGeometry createGeometry(float width, float height, float density) {
        if (geometryBuilder != null) {
            return geometryBuilder.build(width, height, density);
        }
        return null;
    }
}
