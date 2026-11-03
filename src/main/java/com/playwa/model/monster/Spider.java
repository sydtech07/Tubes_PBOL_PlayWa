package com.playwa.model.monster;

public class Spider extends Monster {
    public Spider() { super("Laba-laba", 38, 25, 15); }
    @Override public int attack() { return 3 + RNG.nextInt(5); } // 3-7
}
