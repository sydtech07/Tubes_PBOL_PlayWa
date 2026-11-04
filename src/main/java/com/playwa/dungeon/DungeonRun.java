package com.playwa.dungeon;

import com.playwa.activity.Activity;
import com.playwa.activity.ActivityResult;
import com.playwa.activity.Battle;
import com.playwa.activity.BattleNarrator;
import com.playwa.model.Item;
import com.playwa.model.Material;
import com.playwa.model.Player;
import com.playwa.model.monster.Monster;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Menjelajah dungeon lantai demi lantai. Satu lagi turunan Activity.
 * Mati = semua hasil run hilang dan kehilangan 10% gold.
 * HP di bawah 50% = otomatis mundur sambil membawa hasil sejauh ini.
 */
public class DungeonRun extends Activity {
    private static final Random RNG = new Random();
    private final Dungeon dungeon;

    public DungeonRun(Dungeon dungeon) {
        super(dungeon.getName(), dungeon.getStaminaCost());
        this.dungeon = dungeon;
    }

    @Override
    protected boolean canStart(Player player, ActivityResult result) {
        if (player.getLevel() >= dungeon.getMinLevel()) return true;
        result.log(dungeon.getName() + " butuh level " + dungeon.getMinLevel() + " (levelmu " + player.getLevel() + ").");
        return false;
    }

    @Override
    protected void execute(Player player, ActivityResult result) {
        int exp = 0, gold = 0, cleared = 0;
        List<Item> loot = new ArrayList<>();
        result.log("Kamu memasuki " + dungeon.getName() + " (" + dungeon.getFloors() + " lantai).");

        for (int floor = 1; floor <= dungeon.getFloors(); floor++) {
            if (floor > 1 && player.getHp() * 100 / player.getMaxHp() < 50) {
                result.log("HP tinggal sedikit, kamu mundur sebelum lantai " + floor + ".");
                break;
            }
            boolean isBoss = floor == dungeon.getFloors();
            Monster monster = isBoss ? dungeon.spawnBoss() : dungeon.spawnMonster();
            result.log("Lantai " + floor + (isBoss ? " (BOSS)" : "") + ": " + BattleNarrator.encounter(monster.getName()));

            if (!Battle.fight(player, monster, result, false)) {
                int lost = player.getGold() / 10;
                player.spendGold(lost);
                player.heal(10);
                result.log("Kamu gugur! Semua hasil run hilang, kehilangan " + lost + " gold.");
                return;
            }
            cleared++;
            exp += monster.getExpReward();
            gold += monster.getGoldReward();
            roll(dungeon.getLootTable(), loot);
            if (isBoss) roll(dungeon.getBossLoot(), loot);
        }

        result.addExp(exp);
        result.addGold(gold);
        loot.forEach(result::addLoot);
        result.log(cleared == dungeon.getFloors() ? "Dungeon selesai!" : "Kamu keluar dari dungeon.");
    }

    private void roll(List<LootEntry> table, List<Item> into) {
        for (LootEntry e : table) {
            if (RNG.nextInt(100) >= e.chancePercent()) continue;
            int qty = e.minQty() + RNG.nextInt(e.maxQty() - e.minQty() + 1);
            for (int i = 0; i < qty; i++) into.add(new Material(e.itemName(), 0)); // hanya nama yang dipakai
        }
    }
}
