package com.playwa.model;

public enum WeaponType {
    SWORD("Pedang"), AXE("Kapak"), SPEAR("Tombak"), DAGGER("Belati"), BOW("Busur"), STAFF("Tongkat");
    private final String label;
    WeaponType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
