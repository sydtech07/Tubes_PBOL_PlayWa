package com.playwa.model;

public enum ArmorSlot {
    HELMET("Helm"), CHEST("Zirah"), BOOTS("Sepatu");
    private final String label;
    ArmorSlot(String label) { this.label = label; }
    public String getLabel() { return label; }
}
