package com.playwa.activity;

import com.playwa.model.Player;
import com.playwa.model.monster.Monster;
import java.util.Random;

/** Logika pertarungan satu lawan satu, dipakai bersama oleh Hunting dan DungeonRun. */
public final class Battle {
    private static final Random RNG = new Random();
    private Battle() {}

    /** @return true kalau pemain menang. detailed=false hanya mencatat ringkasan. */
    public static boolean fight(Player player, Monster monster, ActivityResult result, boolean detailed) {
        int rounds = 0;
        while (player.isAlive() && monster.isAlive()) {
            rounds++;
            int dmg = player.getTotalAttack() + RNG.nextInt(4);
            monster.takeDamage(dmg);
            if (detailed) result.log(BattleNarrator.playerHit(monster.getName(), dmg));
            if (!monster.isAlive()) break;

            int hit = Math.max(1, monster.attack() - player.getTotalDefense());
            player.takeDamage(hit);
            if (detailed) result.log(BattleNarrator.monsterHit(monster.getName(), hit));
        }
        boolean won = player.isAlive();
        String ending = won ? BattleNarrator.victory(monster.getName()) : BattleNarrator.defeat(monster.getName());
        result.log(detailed ? ending : ending + " (" + rounds + " ronde, HP kamu: " + player.getHp() + ")");
        return won;
    }
}
