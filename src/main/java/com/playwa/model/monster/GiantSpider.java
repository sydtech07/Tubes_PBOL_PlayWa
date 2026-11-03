package com.playwa.model.monster;

public class GiantSpider extends Monster {
    public GiantSpider() { super("Laba-laba Raksasa", 85, 130, 80); }
    @Override public int attack() { return 6 + RNG.nextInt(6); } // 6-11
}
