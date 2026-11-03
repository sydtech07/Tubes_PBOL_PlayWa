package com.playwa.model;

import jakarta.persistence.*;

@Entity @DiscriminatorValue("ARMOR")
public class Armor extends Item implements Usable {
    @Enumerated(EnumType.STRING) @Column(name = "armor_slot")
    private ArmorSlot slot;
    private Integer defense;

    protected Armor() {}
    public Armor(String name, ArmorSlot slot, Rarity rarity, int price, int defense) {
        super(name, price, rarity); this.slot = slot; this.defense = defense;
    }

    public ArmorSlot getSlot() { return slot; }
    public int getDefense() { return defense; }
    @Override public String getCategory() { return "Armor"; }
    @Override public String getDescription() { return slot.getLabel() + ", DEF +" + defense; }
    @Override public String use(Player player) {
        player.equip(this);
        return player.getName() + " memakai " + getName() + " (DEF +" + defense + ")";
    }
    @Override public boolean isConsumedOnUse() { return false; }
}
