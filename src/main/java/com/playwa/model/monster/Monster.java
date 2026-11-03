package com.playwa.model.monster;

import java.util.Random;

/** Abstract class: tiap monster menyerang dengan caranya sendiri (polymorphism). */
public abstract class Monster {
    protected static final Random RNG = new Random();
    private final String name;
    private int hp;
    private final int expReward, goldReward;

    protected Monster(String name, int hp, int expReward, int goldReward) {
        this.name = name; this.hp = hp; this.expReward = expReward; this.goldReward = goldReward;
    }

    public abstract int attack();

    public void takeDamage(int dmg) { hp = Math.max(0, hp - dmg); }
    public boolean isAlive() { return hp > 0; }
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }
}
