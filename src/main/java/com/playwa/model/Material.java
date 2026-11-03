package com.playwa.model;

import jakarta.persistence.*;

@Entity @DiscriminatorValue("MATERIAL")
public class Material extends Item {
    @Enumerated(EnumType.STRING)
    private MaterialCategory materialCategory;

    protected Material() {}
    public Material(String name, int price) { this(name, MaterialCategory.MISC, Rarity.COMMON, price); }
    public Material(String name, MaterialCategory category, Rarity rarity, int price) {
        super(name, price, rarity); this.materialCategory = category;
    }

    @Override public String getCategory() { return "Material"; }
    @Override public String getDescription() {
        return (materialCategory == null ? "Bahan" : materialCategory.getLabel()) + ", bahan crafting";
    }
}
