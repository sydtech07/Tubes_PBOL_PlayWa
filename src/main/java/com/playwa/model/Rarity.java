package com.playwa.model;

public enum Rarity {
    COMMON("Biasa"), UNCOMMON("Tidak Biasa"), RARE("Langka"), EPIC("Epik"), LEGENDARY("Legendaris");
    private final String label;
    Rarity(String label) { this.label = label; }
    public String getLabel() { return label; }
}
