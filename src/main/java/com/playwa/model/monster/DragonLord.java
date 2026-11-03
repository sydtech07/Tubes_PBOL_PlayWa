package com.playwa.model.monster;

public class DragonLord extends Monster {
    public DragonLord() { super("Raja Naga", 300, 600, 500); }
    @Override public int attack() { return 15 + RNG.nextInt(14); } // 15-28
}
