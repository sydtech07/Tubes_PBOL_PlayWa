package com.playwa.model.monster;

public class Skeleton extends Monster {
    public Skeleton() { super("Kerangka", 52, 40, 25); }
    @Override public int attack() { return 6 + RNG.nextInt(6); } // 6-11
}
