
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
        int criticalCount = 0;
        int dodgeCount = 0;

        while (player.isAlive() && monster.isAlive()) {
            rounds++;

            // Serangan pemain dengan peluang critical hit 15%
            int dmg = player.getTotalAttack() + RNG.nextInt(4);
            boolean critical = RNG.nextInt(100) < 15;

            if (critical) {
                dmg *= 2;
                criticalCount++;
            }

            monster.takeDamage(dmg);

            if (detailed) {
                result.log(BattleNarrator.playerHit(monster.getName(), dmg));
                if (critical) result.log("Critical Hit! Damage menjadi 2x lipat!");
            }

            if (!monster.isAlive()) break;

            // Pemain memiliki peluang menghindar sebesar 10%
            boolean dodge = RNG.nextInt(100) < 10;

            if (dodge) {
                dodgeCount++;
                if (detailed) result.log("Dodge! Kamu berhasil menghindari serangan!");
                continue;
            }

            // Serangan monster
            int hit = Math.max(1, monster.attack() - player.getTotalDefense());
            player.takeDamage(hit);

            if (detailed) result.log(BattleNarrator.monsterHit(monster.getName(), hit));
        }

        boolean won = player.isAlive();
        String ending = won ? BattleNarrator.victory(monster.getName()) : BattleNarrator.defeat(monster.getName());

        result.log(detailed ? ending : ending + " (" + rounds + " ronde, HP kamu: " + player.getHp() + ")");

        // Ringkasan tambahan pertarungan
        result.log("Total ronde: " + rounds);
        result.log("Critical Hit: " + criticalCount + " kali");
        result.log("Dodge: " + dodgeCount + " kali");

        return won;
    }
}
