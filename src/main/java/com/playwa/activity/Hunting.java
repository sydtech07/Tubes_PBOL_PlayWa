package com.playwa.activity;

import com.playwa.model.Material;
import com.playwa.model.Player;
import com.playwa.model.monster.Goblin;
import com.playwa.model.monster.Monster;
import com.playwa.model.monster.Slime;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/** Hunting: lawan satu monster acak. */
public class Hunting extends Activity {
    private static final Random RNG = new Random();
    private final List<Supplier<Monster>> spawnList = List.of(Goblin::new, Slime::new);

    public Hunting() { super("Hunting", 15); }

    @Override
    protected void execute(Player player, ActivityResult result) {
        Monster monster = spawnList.get(RNG.nextInt(spawnList.size())).get(); // polymorphism
        result.log(BattleNarrator.encounter(monster.getName()) + " (HP " + monster.getHp() + ")");

        if (Battle.fight(player, monster, result, true)) {
            result.addExp(monster.getExpReward());
            result.addGold(monster.getGoldReward());
            result.log("Kamu menang! +" + monster.getExpReward() + " EXP, +" + monster.getGoldReward() + " gold");
            if (RNG.nextInt(100) < 50) {
                Material drop = new Material("Kulit " + monster.getName(), 12);
                result.addLoot(drop);
                result.log("Loot: " + drop.getName());
            }
        } else {
            int lost = player.getGold() / 10;
            player.spendGold(lost);
            player.heal(10);
            result.log("Kamu kalah... kehilangan " + lost + " gold, bangun dengan HP 10.");
        }
    }
}
