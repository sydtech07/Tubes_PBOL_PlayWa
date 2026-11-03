package com.playwa.model;

import jakarta.persistence.*;

@Entity @DiscriminatorValue("WEAPON")
public class Weapon extends Item implements Usable {
    @Enumerated(EnumType.STRING)
    private WeaponType weaponType;
    private Integer attackPower;

    protected Weapon() {}
    public Weapon(String name, WeaponType type, Rarity rarity, int price, int attackPower) {
        super(name, price, rarity); this.weaponType = type; this.attackPower = attackPower;
    }

    public int getAttackPower() { return attackPower; }
    @Override public String getCategory() { return "Senjata"; }
    @Override public String getDescription() {
        return (weaponType == null ? "Senjata" : weaponType.getLabel()) + ", ATK +" + attackPower;
    }
    @Override public String use(Player player) {
        player.equip(this);
        return player.getName() + " memakai " + getName() + " (ATK +" + attackPower + ")";
    }
    @Override public boolean isConsumedOnUse() { return false; }
}
