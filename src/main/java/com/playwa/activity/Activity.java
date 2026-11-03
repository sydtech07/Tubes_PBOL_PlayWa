package com.playwa.activity;

import com.playwa.model.Player;

/**
 * Abstract class + Template Method.
 * run() = kerangka tetap (cek HP, kurangi stamina, beri hadiah).
 * execute() = bagian yang diisi berbeda oleh tiap subclass.
 */
public abstract class Activity {
    private final String name;
    private final int staminaCost;

    protected Activity(String name, int staminaCost) {
        this.name = name;
        this.staminaCost = staminaCost;
    }

    public final ActivityResult run(Player player) {
        ActivityResult result = new ActivityResult();
        if (!player.isAlive()) {
            result.log("HP kamu 0, istirahat dulu!");
            return result;
        }
        if (!canStart(player, result)) return result;
        if (!player.useStamina(staminaCost)) {
            result.log("Stamina kurang untuk " + name + " (butuh " + staminaCost + "). Istirahat dulu!");
            return result;
        }
        execute(player, result);
        player.addGold(result.getGold());
        result.setLeveledUp(player.gainExp(result.getExp()));
        if (result.isLeveledUp()) result.log("LEVEL UP! Sekarang level " + player.getLevel());
        return result;
    }

    /** Hook: subclass boleh menambah syarat (misalnya level minimum). */
    protected boolean canStart(Player player, ActivityResult result) { return true; }

    protected abstract void execute(Player player, ActivityResult result);

    public String getName() { return name; }
    public int getStaminaCost() { return staminaCost; }
}
