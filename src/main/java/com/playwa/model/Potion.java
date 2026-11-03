package com.playwa.model;

import jakarta.persistence.*;

@Entity @DiscriminatorValue("POTION")
public class Potion extends Item implements Usable {
    private Integer healAmount;

    protected Potion() {}
    public Potion(String name, Rarity rarity, int price, int healAmount) {
        super(name, price, rarity); this.healAmount = healAmount;
    }

    @Override public String getCategory() { return "Potion"; }
    @Override public String getDescription() { return "Memulihkan " + healAmount + " HP"; }
    @Override public String use(Player player) {
        player.heal(healAmount);
        return player.getName() + " memakai " + getName() + " (+" + healAmount + " HP)";
    }
}
