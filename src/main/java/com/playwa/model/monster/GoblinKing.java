package com.playwa.model.monster;

public class GoblinKing extends Monster {
    public GoblinKing() { super("Raja Goblin", 55, 90, 60); }
    @Override public int attack() { return 5 + RNG.nextInt(4); } // 5-8
}
