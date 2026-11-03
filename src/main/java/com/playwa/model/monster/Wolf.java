package com.playwa.model.monster;

public class Wolf extends Monster {
    public Wolf() { super("Serigala", 40, 25, 15); }
    @Override public int attack() { return 4 + RNG.nextInt(4); } // 4-7
}
