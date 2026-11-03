package com.playwa.model;

import jakarta.persistence.*;

@Entity @DiscriminatorValue("FOOD")
public class Food extends Item implements Usable {
    private Integer restoreHp;
    private Integer restoreStamina;

    protected Food() {}
    public Food(String name, Rarity rarity, int price, int restoreHp, int restoreStamina) {
        super(name, price, rarity); this.restoreHp = restoreHp; this.restoreStamina = restoreStamina;
    }

    @Override public String getCategory() { return "Makanan"; }
    @Override public String getDescription() { return "HP +" + restoreHp + ", Stamina +" + restoreStamina; }
    @Override public String use(Player player) {
        player.heal(restoreHp);
        player.restoreStamina(restoreStamina);
        return player.getName() + " makan " + getName() + " (HP +" + restoreHp + ", Stamina +" + restoreStamina + ")";
    }
}
