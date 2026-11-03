package com.playwa.model.monster;

public class Goblin extends Monster {
    public Goblin() { super("Goblin", 40, 30, 20); }
    @Override public int attack() { return 4 + RNG.nextInt(5); } // 4-8
}
