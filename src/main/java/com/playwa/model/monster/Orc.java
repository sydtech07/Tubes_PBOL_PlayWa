package com.playwa.model.monster;

public class Orc extends Monster {
    public Orc() { super("Orc", 75, 55, 35); }
    @Override public int attack() { return 8 + RNG.nextInt(8); } // 8-15
}
