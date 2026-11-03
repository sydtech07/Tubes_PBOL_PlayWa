package com.playwa.model.monster;

public class Slime extends Monster {
    public Slime() { super("Slime", 25, 15, 10); }
    @Override public int attack() { return 2 + RNG.nextInt(3); } // 2-4
}
