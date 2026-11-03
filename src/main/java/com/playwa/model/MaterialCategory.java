package com.playwa.model;

public enum MaterialCategory {
    ORE("Bijih"), WOOD("Kayu"), HIDE("Kulit"), HERB("Herbal"), GEM("Permata"), MISC("Lainnya");
    private final String label;
    MaterialCategory(String label) { this.label = label; }
    public String getLabel() { return label; }
}
