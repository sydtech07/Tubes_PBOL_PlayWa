package com.playwa.model.monster;

public class StoneGolem extends Monster {
    public StoneGolem() { super("Golem Batu", 150, 200, 120); }
    @Override public int attack() { return 9 + RNG.nextInt(8); } // 9-16
}
